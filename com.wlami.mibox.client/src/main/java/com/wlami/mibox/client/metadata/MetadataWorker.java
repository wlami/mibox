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
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeRepository;
import com.wlami.mibox.client.networking.synchronization.MChunkUpload;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;
import com.wlami.mibox.client.networking.synchronization.UploadCallback;
import com.wlami.mibox.core.encryption.KeyGen;
import com.wlami.mibox.core.encryption.PBKDF2;
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

	/** reference to a loader */
	private EncryptedMiTreeRepository encryptedMiTreeRepo;

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
			ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents,
			EncryptedMiTreeRepository encryptedMiTreeRepo) {
		this.incomingEvents = incomingEvents;
		this.appSettingsDao = appSettingsDao;
		this.transportProvider = transportProvider;
		this.encryptedMiTreeRepo = encryptedMiTreeRepo;
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

		EncryptedMiTreeInformation miTreeInformation = retrieveRootMiTreeInformation(appSettings);

		EncryptedMiTree encryptedRoot = encryptedMiTreeRepo
				.loadEncryptedMiTree(miTreeInformation.getFileName());
		DecryptedMiTree root;
		if (encryptedRoot == null) {
			root = new DecryptedMiTree();
			root.setFolderName("/");
		} else {
			try {
				root = encryptedRoot.decrypt(miTreeInformation.getKey(),
						miTreeInformation.getIv());
			} catch (CryptoException e) {
				log.error("Error during decrypting the root metadata!", e);
				return;
			}
		}
		traverseFileSystem(new File(watchDir), root, miTreeInformation);
	}

	/**
	 * @param appSettings
	 * @return
	 */
	public EncryptedMiTreeInformation retrieveRootMiTreeInformation(
			AppSettings appSettings) {
		EncryptedMiTreeInformation miTreeInformation = new EncryptedMiTreeInformation();
		byte[] key = PBKDF2.getKeyFromPasswordAndSalt(
				appSettings.getPassword(), appSettings.getUsername());
		byte[] iv = HashUtil.calculateMD5Bytes(appSettings.getUsername()
				.getBytes());
		miTreeInformation.setKey(key);
		miTreeInformation.setIv(iv);
		miTreeInformation.setFileName("root");
		return miTreeInformation;
	}

	/**
	 * 
	 * @param rootFolder
	 * @param decryptedMiTree
	 *            MUST NOT BE NULL!
	 */
	public void traverseFileSystem(File rootFolder,
			DecryptedMiTree decryptedMiTree,
			EncryptedMiTreeInformation miTreeInformation) {
		if (!rootFolder.exists()) {
			log.error("folder does not exist! [{}]", rootFolder);
			return;
		}
		log.debug("Traversing file system. Processing folder [{}]",
				rootFolder.getName());
		for (File file : rootFolder.listFiles()) {
			if (file.isFile()) {
				MFile mFile;
				log.debug("Processing file [{}]", file.getName());
				if (decryptedMiTree.getFiles().containsKey(file.getName())) {
					// There is a matching MFile.
					mFile = decryptedMiTree.getFiles().get(file.getName());
					log.debug("Found file in metadata. [{}]", file.getName());
				} else {
					// There is no matching file, so we create it first
					mFile = new MFile();
					mFile.setName(file.getName());
					decryptedMiTree.getFiles().put(mFile.getName(), mFile);
					log.debug("Creating a new MFile in metadata for [{}]",
							file.getName());
				}
				synchronizeFileMetadata(file, mFile);
			} else if (file.isDirectory()) {
				// find the right metadata
				log.debug("Search folder metadata for [{}]", file.getName());
				EncryptedMiTreeInformation encryptedMiTreeInformation = decryptedMiTree
						.getSubfolder().get(file.getName());
				if (encryptedMiTreeInformation == null) {
					// Let's create a new subtree!
					DecryptedMiTree subTree = new DecryptedMiTree();
					subTree.setFolderName(file.getName());
					encryptedMiTreeInformation = new EncryptedMiTreeInformation();
					KeyGen keyGen = new KeyGen();
					encryptedMiTreeInformation.setIv(keyGen
							.generateRandomBytes(16));
					encryptedMiTreeInformation.setKey(keyGen
							.generateRandomBytes(32));
					encryptedMiTreeInformation.setFileName(UUID.randomUUID()
							.toString());
					decryptedMiTree.getSubfolder().put(file.getName(),
							encryptedMiTreeInformation);
					log.debug(
							"No information available yet. Creating new data file [{}]",
							encryptedMiTreeInformation.getFileName());
					traverseFileSystem(file, subTree,
							encryptedMiTreeInformation);
				} else {
					// load the metadata for the subtree
					log.debug("Found information for folder. Trying to load it");
					EncryptedMiTree encryptedMiTree = encryptedMiTreeRepo
							.loadEncryptedMiTree(encryptedMiTreeInformation
									.getFileName());
					log.debug("Trying to decrypt the metadata now.");
					try {
						DecryptedMiTree subTree = encryptedMiTree.decrypt(
								encryptedMiTreeInformation.getKey(),
								encryptedMiTreeInformation.getIv());
						traverseFileSystem(file, subTree,
								encryptedMiTreeInformation);
					} catch (CryptoException | IOException e) {
						log.error("Could not decrypt subfolder metadata", e);
					}
				}
			}
		}

		// Save the tree
		EncryptedMiTree encryptedMiTree = decryptedMiTree.encrypt(
				miTreeInformation.getKey(), miTreeInformation.getIv());
		encryptedMiTreeRepo.saveEncryptedMiTree(encryptedMiTree,
				miTreeInformation.getFileName());
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
		log.debug("Start synchronization for file [{}]", f.getAbsolutePath());
		Date filesystemLastModified = new Date(f.lastModified());
		if ((mFile.getLastModified() == null)
				|| (filesystemLastModified.after(mFile.getLastModified()))) {
			// The file has been modified, so we have to update metadata
			log.debug("File newer than last modification date. Calculating file and chunk hashes for [{}]", f.getName());
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
						log.debug("Neu Chunk [{}] finished with hash [{}]" ,currentChunk, newChunkHash);
						// Create Upload request
						createUploadRequest(chunk);
					}
					currentChunk++;

				}
				// TODO split hashing and chunk-upload

				mFile.setFileHash(HashUtil.digestToString(fileDigest.digest()));
				mFile.setLastModified(filesystemLastModified);

			} catch (NoSuchAlgorithmException e) {
				log.error("No SHA availabe", e);
			} catch (IOException | NoSuchProviderException e) {
				log.error("", e);
			}
		} else {
			log.debug("The file has not been modified [{}]", f.getName());
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
		log.debug("Creating upload request for chunk [{}]",
				chunk.getDecryptedChunkHash());
		MChunkUpload mChunkUpload = new MChunkUpload();
		mChunkUpload.setMChunk(chunk);
		mChunkUpload.setUploadCallback(new UploadCallback() {
			public void uploadCallback(MChunk mChunk, String result) {
				mChunk.setEncryptedChunkHash(result);
				mChunk.setLastSync(new Date());
				// try {
				// //TODO writeMetadata(true);
				// } catch (IOException e) {
				// log.error(
				// "Could not persist metadata. Maybe you have to toggle this process manually.",
				// e);
				// }
			}
		});
		transportProvider.addChunkUpload(mChunkUpload);
		log.debug("Added upload request to the processing queue");
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
