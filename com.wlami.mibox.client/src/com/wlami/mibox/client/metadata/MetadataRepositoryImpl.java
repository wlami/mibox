/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2011 Wladislaw Mitzel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wlami.mibox.client.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppFolders;
import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class MetadataRepositoryImpl implements MetadataRepository {

	/** internal logging object. */
	private static final Logger log = LoggerFactory
			.getLogger(MetadataRepositoryImpl.class);

	/**
	 * Reference to the {@link AppSettingsDao} bean.
	 */
	AppSettingsDao appSettingsDao;

	/**
	 * reference to a {@link MetadataWorker} instance which handles the file
	 * structure.
	 */
	private MetadataWorker worker;

	/**
	 * set of incoming {@link ObservedFilesystemEvent} instances which shall be
	 * processed by the {@link MetadataWorker}.
	 */
	private ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents = new ConcurrentSkipListSet<ObservedFilesystemEvent>();

	/**
	 * default constructor.
	 */
	@Inject
	public MetadataRepositoryImpl(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#start()
	 */
	@Override
	public void startProcessing() {
		if (worker == null) {
			worker = new MetadataWorker();
			worker.start();
			log.info("Starting MetadataRepository");
		} else {
			log.debug("MetadataRepository already started.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#stop()
	 */
	@Override
	public void stopProcessing() {
		log.info("Stopping MetadataRepository...");
		worker.active = false;
		try {
			worker.join();
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
		log.info("MetadataRepository stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.metadata.MetadataRepository#addEvent(com.wlami
	 * .mibox.client.metadata.ObservedFilesystemEvent)
	 */
	@Override
	public void addEvent(ObservedFilesystemEvent observedFilesystemEvent) {
		if (!incomingEvents.add(observedFilesystemEvent)) {
			log.debug("ObservedFilesystemEvent not added - already existing");
		}
	}

	/**
	 * Creates a String from a MessageDigest result.
	 * 
	 * @param input
	 * @return
	 */
	private static String digestToString(byte[] input) {
		Formatter formatter = new Formatter();
		for (byte b : input) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	/**
	 * This class represents the worker thread, which is controlled the
	 * {@link MetadataRepositoryImpl}.
	 * 
	 * @author Wladislaw Mitzel
	 * 
	 */
	class MetadataWorker extends Thread {

		/**
		 * 
		 */
		private static final String SHA_1_MESSAGE_DIGEST = "SHA1";

		/**
		 * 
		 */
		protected static final String SHA_256_MESSAGE_DIGEST = "SHA-256";

		/**
		 * 
		 */
		private static final String METADATA_DEFAULT_FILENAME = ".usermetadata";

		private final Logger log = LoggerFactory
				.getLogger(MetadataWorker.class);

		private boolean active = true;

		private static final long DEFAULT_SLEEP_TIME = 250L;

		/** the internal file structure stored as an {@link MFolder} instance. */
		private MFolder rootFolder;

		/** JSON Object mapper for persistence. */
		private ObjectMapper objectMapper = new ObjectMapper();

		/** File instance of json persistence file. */
		private File metadataFile = new File(AppFolders.getConfigFolder(),
				METADATA_DEFAULT_FILENAME);

		/**
		 * 
		 */
		public MetadataWorker() {
			AppSettings appSetting;
			try {
				appSetting = appSettingsDao.load();
				if (metadataFile.exists()) {
					// read the metadata from disk
					rootFolder = objectMapper.readValue(new BufferedReader(
							new FileReader(metadataFile)), MFolder.class);
				} else {
					// create a new metadata file
					rootFolder = new MFolder(null);
					rootFolder.setName("/");
					objectMapper.writeValue(metadataFile, rootFolder);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Synchronizes the metadata and the state of the file system. For this
		 * purpose the following algorithm is used:
		 * <ol>
		 * <li>update the root node to match the {@link AppSettings}
		 * .watchDirectory</li>
		 * <li>traverse the file system recursively
		 * <ol>
		 * <li>add non existing {@link MFolder}s</li>
		 * <li>add non existing {@link MFile}s or update them if they exist</li>
		 * </ol>
		 * </li>
		 * </ol>
		 * 
		 * @throws IOException
		 *             Thrown on io errors.
		 */
		private void synchronizeMetadata() throws IOException {
			log.debug("Synchronizing metadata with filesystem");
			AppSettings appSettings = appSettingsDao.load();
			String watchDir = appSettings.getWatchDirectory();
			traverseFileSystem(new File(watchDir), rootFolder);
			objectMapper.writeValue(metadataFile, rootFolder);
		}

		/**
		 * Helper-method of synchronizeMetadata. Algorithm is described there.
		 * 
		 * @param filesystemFolder
		 *            {@link File} instance which shall be synchronized in this
		 *            recursion step.
		 * @param mFolder
		 *            {@link MFolder} instance which shall contain the metadata.
		 */
		private void traverseFileSystem(File filesystemFolder, MFolder mFolder) {
			log.debug("traversing " + filesystemFolder.getAbsolutePath());
			for (File f : filesystemFolder.listFiles()) {
				if (f.isDirectory()) {
					// We got a folder and have to recurse again.
					MFolder subFolder;
					if (mFolder.getSubfolders().containsKey(f.getName())) {
						// There is a matching MFolder so we use it.
						subFolder = mFolder.getSubfolders().get(f.getName());
					} else {
						// We did not find a matching MFolder so we create it!
						subFolder = new MFolder();
						subFolder.setName(f.getName());
						subFolder.setParentFolder(mFolder);
						mFolder.getSubfolders().put(subFolder.getName(),
								subFolder);
					}
					traverseFileSystem(f, subFolder);
				} else if (f.isFile()) {
					MFile mFile;
					if (mFolder.getFiles().containsKey(f.getName())) {
						// There is a matching MFile.
						mFile = mFolder.getFiles().get(f.getName());
						log.debug("Found file in metadata. " + f.getName());
					} else {
						// There is no matching file, so we create it first
						mFile = new MFile();
						mFile.setFolder(mFolder);
						mFile.setName(f.getName());
						// TODO set hash
						mFile.setFileHash("1234567890abcdef");
						mFolder.getFiles().put(mFile.getName(), mFile);
						log.debug("Creating a new MFile in metadata for "
								+ f.getName());
					}
					synchronizeFileMetadata(f, mFile);
				} else {
					// What do we have over here, not a file and not a dir?!
					log.error("wtf? " + f.getName());
				}
			}
		}

		/**
		 * Synchronizes the file system state with the metadata.<br/>
		 * <br/>
		 * First checks whether file system lastModified date is later than the
		 * metadata. In this case the hashes are updated.
		 * 
		 * @param f
		 *            Referende to the filesystem file.
		 * @param mFile
		 *            Reference to the metadata file.
		 */
		private void synchronizeFileMetadata(File f, MFile mFile) {
			// Check whether the file has been modified since the last meta sync
			Date filesystemLastModified = new Date(f.lastModified());
			if ((mFile.getLastModified() == null)
					|| (filesystemLastModified.after(mFile.getLastModified()))) {
				// The file has been modified, so we have to update metadata
				log.debug("Calculating file and chunk hashes for "
						+ f.getName());
				try {
					// create two digests. One is for the whole file. The other
					// is for the chunks and gets reseted after each chunk.
					MessageDigest fileDigest = MessageDigest.getInstance(
							SHA_1_MESSAGE_DIGEST, "BC");
					MessageDigest chunkDigest = MessageDigest.getInstance(
							SHA_1_MESSAGE_DIGEST, "BC");
					FileInputStream fileInputStream = new FileInputStream(f);
					int currentByte;
					int readBytes = 0;
					int currentChunk = 0;
					int chunkSize = mFile.getChunkSize();
					// Read the file until EOF == -1
					byte[] currentBytes = new byte[chunkSize];
					while ((readBytes = fileInputStream.read(currentBytes)) != -1) {
						fileDigest.update(currentBytes, 0, readBytes);
						chunkDigest.update(currentBytes, 0, readBytes);
						// If we have finished the chunk
						MChunk chunk;
						// Check whether we have the chunk data already
						if (mFile.getChunks().size() > currentChunk) {
							// We found the chunk
							chunk = mFile.getChunks().get(currentChunk);
						} else {
							// There is no chunk and we create a new one.
							chunk = new MChunk();
							mFile.getChunks().add(chunk);
							chunk.setFile(mFile);
						}
						String newChunkHash = digestToString(chunkDigest
								.digest());
						if (!newChunkHash.equals(chunk.getDecryptedChunkHash())) {
							chunk.setLastChange(new Date());
							chunk.setDecryptedChunkHash(newChunkHash);
						}
						currentChunk++;
						log.debug("Neu Chunk " + currentChunk
								+ " finished with hash " + newChunkHash);
					}
					mFile.setFileHash(digestToString(fileDigest.digest()));
					mFile.setLastModified(filesystemLastModified);
				} catch (NoSuchAlgorithmException e) {
					log.error("No SHA availabe", e);
				} catch (IOException | NoSuchProviderException e) {
					log.error("", e);
				}
			} else {
				log.debug("The file has not been modified "
						+ f.getAbsolutePath());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			log.debug("Starting");
			try {
				synchronizeMetadata();
				while (active) {
					for (ObservedFilesystemEvent ofe : incomingEvents) {
						log.debug("Processing event " + ofe);
						// TODO: process the event!
						incomingEvents.remove(ofe);
					}
					try {
						Thread.sleep(DEFAULT_SLEEP_TIME);
					} catch (InterruptedException e) {
					}
				}
			} catch (IOException e) {
				log.error("Cannot load Appsettings - MetadataRepository cannot be started!");
			}
		}
	}

}
