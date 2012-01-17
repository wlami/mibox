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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppFolders;
import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.networking.synchronization.MChunkUpload;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;
import com.wlami.mibox.client.networking.synchronization.UploadCallback;
import com.wlami.mibox.core.util.HashUtil;

/**
 * This class represents the worker thread, which is controlled the
 * {@link MetadataRepositoryImpl}.
 * 
 * @author Wladislaw Mitzel
 * 
 */
class MetadataWorker extends Thread {

	/** Defines the period between writes of metadata in seconds. */
	protected static final int WRITE_PERIOD_SECONDS = 60;

	/** Constant for accessing the metadata file on disk. */
	private static final String METADATA_DEFAULT_FILENAME = ".usermetadata";

	/** internal logger. */
	private final Logger log = LoggerFactory.getLogger(MetadataWorker.class);

	/**
	 * Defines the current state. Thread runs until active is set to false. Then
	 * the thread dies and a new Worker has to be created.
	 */
	private boolean active = true;

	/** Time period between checking the incoming set. */
	private static final long DEFAULT_SLEEP_TIME_MILLIS = 250L;

	/** the internal file structure stored as an {@link MFolder} instance. */
	private MFolder rootFolder;

	/** JSON Object mapper for persistence. */
	private ObjectMapper objectMapper = new ObjectMapper();

	/** File instance of json persistence file. */
	private File metadataFile = new File(AppFolders.getConfigFolder(),
			METADATA_DEFAULT_FILENAME);

	/** Next time the metadata shall be written to disk */
	private Date nextWrite;

	/** Used for time calculations */
	private Calendar calendar = Calendar.getInstance();

	/**
	 * set of incoming {@link ObservedFilesystemEvent} instances which is
	 * acquired from the {@link MetadataRepository}.
	 */
	private ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents = new ConcurrentSkipListSet<ObservedFilesystemEvent>();

	/** Reference to the {@link AppSettingsDao} bean. */
	AppSettingsDao appSettingsDao;

	/** Reference to the {@link TransportProvider} bean. */
	TransportProvider transportProvider;

	/**
	 * @param active
	 *            the active to set
	 */
	protected void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Default constructor. Loads appSettings from {@link AppSettingsDao} and
	 * the metadata from disk.
	 */
	public MetadataWorker(AppSettingsDao appSettingsDao,
			TransportProvider transportProvider,
			ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents) {
		this.incomingEvents = incomingEvents;
		this.appSettingsDao = appSettingsDao;
		this.transportProvider = transportProvider;
		try {
			if (metadataFile.exists()) {
				// read the metadata from disk
				ZipInputStream zip = new ZipInputStream(new FileInputStream(
						metadataFile));
				zip.getNextEntry();
				rootFolder = objectMapper.readValue(zip, MFolder.class);
				zip.close();
				zip = null;
			} else {
				// create a new metadata file
				rootFolder = new MFolder(null);
				rootFolder.setName("/");
				objectMapper.writeValue(metadataFile, rootFolder);
			}
			writeMetadata(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Synchronizes the metadata and the state of the file system. For this
	 * purpose the following algorithm is used:
	 * <ol>
	 * <li>update the root node to match the {@link AppSettings} .watchDirectory
	 * </li>
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
		writeMetadata(true);
	}

	/**
	 * Writes the metadata to the filesystem.
	 * 
	 * @param write
	 *            If <code>true</code> the metadata really gets written.<br/>
	 *            If <code>false</code> only the next write time will be
	 *            calculated.
	 * @throws IOException
	 *             Thrown on IO-Exceptions: noe permissions, etc.
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 */
	protected synchronized void writeMetadata(boolean write)
			throws IOException, JsonGenerationException, JsonMappingException {
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, WRITE_PERIOD_SECONDS);
		nextWrite = calendar.getTime();
		if (write) {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
					metadataFile));
			zip.putNextEntry(new ZipEntry(METADATA_DEFAULT_FILENAME));
			objectMapper.writeValue(zip, rootFolder);
			log.info("Written metadata. Next write will probably be "
					+ nextWrite.toString());
			zip.close();
			zip = null;
		}

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
		if (!filesystemFolder.exists()) {
			log.error("folder does not exist! " + filesystemFolder);
			return;
		}
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
					mFolder.getSubfolders().put(subFolder.getName(), subFolder);
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
			log.debug("Calculating file and chunk hashes for " + f.getName());
			try {
				// create two digests. One is for the whole file. The other
				// is for the chunks and gets reseted after each chunk.
				MessageDigest fileDigest = MessageDigest.getInstance(
						HashUtil.SHA_256_MESSAGE_DIGEST, "BC");
				MessageDigest chunkDigest = MessageDigest.getInstance(
						HashUtil.SHA_256_MESSAGE_DIGEST, "BC");
				FileInputStream fileInputStream = new FileInputStream(f);
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
						chunk = new MChunk(currentChunk);
						mFile.getChunks().add(chunk);
						chunk.setMFile(mFile);
					}
					String newChunkHash = HashUtil.digestToString(chunkDigest
							.digest());
					if (!newChunkHash.equals(chunk.getDecryptedChunkHash())) {
						chunk.setLastChange(new Date());
						chunk.setDecryptedChunkHash(newChunkHash);
						log.debug("Neu Chunk " + currentChunk
								+ " finished with hash " + newChunkHash);
						// Create Upload request
						createUploadRequest(chunk);
					}
					currentChunk++;

				}
				//TODO split hashing and chunk-upload
				
				mFile.setFileHash(HashUtil.digestToString(fileDigest.digest()));
				mFile.setLastModified(filesystemLastModified);

				// if a certain amount of time has elapsed persist
				if (new Date().after(nextWrite)) {
					writeMetadata(true);
				}

			} catch (NoSuchAlgorithmException e) {
				log.error("No SHA availabe", e);
			} catch (IOException | NoSuchProviderException e) {
				log.error("", e);
			}
		} else {
			log.debug("The file has not been modified " + f.getAbsolutePath());
		}
	}

	/**
	 * Creates a {@link MChunkUpload} object and turns it over to the
	 * {@link TransportProvider}. Persists the metadata to disk.
	 * 
	 * @param chunk
	 *            the chunk which shall be uploaded.
	 */
	protected void createUploadRequest(MChunk chunk) {
		MChunkUpload mChunkUpload = new MChunkUpload();
		mChunkUpload.setMChunk(chunk);
		mChunkUpload.setUploadCallback(new UploadCallback() {
			public void uploadCallback(MChunk mChunk, String result) {
				mChunk.setEncryptedChunkHash(result);
				mChunk.setLastSync(new Date());
				try {
					writeMetadata(true);
				} catch (IOException e) {
					log.error(
							"Could not persist metadata. Maybe you have to toggle this process manually.",
							e);
				}
			}
		});
		transportProvider.addChunkUpload(mChunkUpload);
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
			AppSettings appSettings = appSettingsDao.load();
			while (active) {
				for (ObservedFilesystemEvent ofe : incomingEvents) {
					log.debug("Processing event " + ofe);
					File f = new File(ofe.getFilename());
					if (f.isFile()) {
						String relativePath = StringUtils.substringAfter(
								FilenameUtils.separatorsToUnix(ofe
										.getFilename()), appSettings
										.getWatchDirectory());
						System.out.println(relativePath);
						MFile mFile = MetadataUtil.locateMFile(rootFolder,
								relativePath);
						synchronizeFileMetadata(f, mFile);
					}

					incomingEvents.remove(ofe);
				}
				try {
					Thread.sleep(DEFAULT_SLEEP_TIME_MILLIS);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			log.error(
					"Cannot load Appsettings - MetadataRepository cannot be started!",
					e);
		}
	}
}