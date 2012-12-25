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

import static com.wlami.mibox.client.application.DebugUtil.isDecryptedDebugEnabled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EMiFile;
import com.wlami.mibox.client.metadata2.EMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMetadataInformation;
import com.wlami.mibox.client.metadata2.EncryptedMetadataObjectRepository;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;
import com.wlami.mibox.client.networking.encryption.AesChunkEncryption;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;
import com.wlami.mibox.client.networking.encryption.DataChunk;
import com.wlami.mibox.client.networking.synchronization.ChunkUploadRequest;
import com.wlami.mibox.client.networking.synchronization.DownloadRequest;
import com.wlami.mibox.client.networking.synchronization.RequestContainer;
import com.wlami.mibox.client.networking.synchronization.TransportCallback;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;
import com.wlami.mibox.core.util.HashUtil;

/**
 * This class represents the worker thread, which is controlled the
 * {@link MetadataRepositoryImpl}.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public class MetadataWorker extends Thread {

	/**
	 * 
	 */
	public static final String CALLBACK_PARAM_CONTENT = "content";

	/**
	 * 
	 */
	public static final String CALLBACK_PARAM_ENCRYPTED_CHUNK_HASH = "encryptedChunkHash";

	/** Defines the period between writes of metadata in seconds. */
	protected static final int WRITE_PERIOD_SECONDS = 60;

	/** internal logger. */
	private static final Logger log = LoggerFactory
			.getLogger(MetadataWorker.class);

	/**
	 * Defines the current state. Thread runs until active is set to false. Then
	 * the thread dies and a new Worker has to be created.
	 */
	private boolean active = true;

	/** Time period between checking the incoming set. */
	private static final long DEFAULT_SLEEP_TIME_MILLIS = 250L;

	/** reference to a loader */
	private final EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepo;

	private final EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepo;
	/**
	 * set of incoming {@link ObservedFilesystemEvent} instances which is
	 * acquired from the {@link MetadataRepository}.
	 */
	private ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents = new ConcurrentSkipListSet<ObservedFilesystemEvent>();

	/** Reference to the {@link AppSettingsDao} bean. */
	final AppSettingsDao appSettingsDao;

	/** Reference to the {@link TransportProvider} bean. */
	TransportProvider<ChunkUploadRequest> transportProvider;

	/** Reference to a encryption implementation */
	final ChunkEncryption chunkEncryption;

	/**
	 * @param active
	 *            the active to set
	 */
	protected void setActive(boolean active) {
		this.active = active;
	}

	private final MetadataUtil metadataUtil;

	private MetaMetaDataHolder metaMetaDataHolder;

	private AppSettings appSettings;

	/**
	 * Default constructor. Loads appSettings from {@link AppSettingsDao} and
	 * the metadata from disk.
	 */
	public MetadataWorker(
			AppSettingsDao appSettingsDao,
			TransportProvider<ChunkUploadRequest> transportProvider,
			ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents,
			EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepo,
			EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepo,
			MetadataUtil metadataUtil, MetaMetaDataHolder metaMetaDataHolder,
			ChunkEncryption chunkEncryption) {
		this.incomingEvents = incomingEvents;
		this.appSettingsDao = appSettingsDao;
		this.transportProvider = transportProvider;
		this.encryptedMiTreeRepo = encryptedMiTreeRepo;
		this.encryptedMiFileRepo = encryptedMiFileRepo;
		this.metadataUtil = metadataUtil;
		this.chunkEncryption = chunkEncryption;
		this.metaMetaDataHolder = metaMetaDataHolder;
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
	 * <li>add non existing {@link DecryptedMiFile}s or update them if they
	 * exist</li>
	 * </ol>
	 * </li>
	 * </ol>
	 * 
	 * @throws IOException
	 *             Thrown on io errors.
	 */
	private void synchronizeFilesystemWithLocalMetadata() throws IOException {
		log.debug("Synchronizing metadata with filesystem");
		AppSettings appSettings = appSettingsDao.load();
		String watchDir = appSettings.getWatchDirectory();

		EncryptedMetadataInformation miTreeInformation = metaMetaDataHolder
				.getDecryptedMetaMetaData().getRoot();

		EncryptedMiTree encryptedRoot = encryptedMiTreeRepo
				.loadEncryptedMetadata(miTreeInformation.getFileName());
		DecryptedMiTree root;
		if (encryptedRoot == null) {
			root = new DecryptedMiTree();
			root.setFolderName("/");
		} else {
			root = encryptedRoot.decrypt(miTreeInformation.getKey(),
					miTreeInformation.getIv());
		}
		traverseFileSystem(new File(watchDir), root, miTreeInformation);
	}

	/**
	 * 
	 * @param rootFolder
	 * @param decryptedMiTree
	 *            MUST NOT BE NULL!
	 */
	public void traverseFileSystem(File rootFolder,
			DecryptedMiTree decryptedMiTree,
			EncryptedMetadataInformation miTreeInformation) {
		if (!rootFolder.exists()) {
			log.error("folder does not exist! [{}]", rootFolder);
			return;
		}
		log.debug("Traversing file system. Processing folder [{}]",
				rootFolder.getName());
		for (File file : rootFolder.listFiles()) {
			if (file.isFile()) {
				DecryptedMiFile mFile;
				log.debug("Processing file [{}]", file.getName());
				EMiFile emiFile = null;
				if (decryptedMiTree.getFiles().containsKey(file.getName())) {
					// There is a matching MFile.
					EncryptedMetadataInformation encInfo = decryptedMiTree.getFiles().get(file.getName());
					mFile = getDecryptedMiFile(false, encInfo);
					emiFile = new EMiFile(miTreeInformation, mFile);
					log.debug("Found file in metadata. [{}]", file.getName());
				} else {
					// There is no matching file, so we create it first
					emiFile = metadataUtil.createMiFileInMiTree(
							new EMiTree(miTreeInformation, decryptedMiTree), file.getName());

					log.debug("Creating a new MFile in metadata for [{}]",
							file.getName());
				}
				synchronizeFileMetadata(file, emiFile);
			} else if (file.isDirectory()) {
				// find the right metadata
				log.debug("Search folder metadata for [{}]", file.getName());
				EncryptedMetadataInformation EncryptedMetadataInformation = decryptedMiTree
						.getSubfolder().get(file.getName());
				if (EncryptedMetadataInformation == null) {
					// Let's create a new subtree!
					DecryptedMiTree subTree = new DecryptedMiTree();
					subTree.setFolderName(file.getName());
					EncryptedMetadataInformation = EncryptedMetadataInformation
							.createRandom();
					decryptedMiTree.getSubfolder().put(file.getName(),
							EncryptedMetadataInformation);
					log.debug(
							"No information available yet. Creating new data file [{}]",
							EncryptedMetadataInformation.getFileName());
					traverseFileSystem(file, subTree,
							EncryptedMetadataInformation);
				} else {
					// load the metadata for the subtree
					log.debug("Found information for folder. Trying to load it");
					EncryptedMiTree encryptedMiTree = encryptedMiTreeRepo
							.loadEncryptedMetadata(EncryptedMetadataInformation
									.getFileName());
					log.debug("Trying to decrypt the metadata now.");
					DecryptedMiTree subTree = encryptedMiTree.decrypt(
							EncryptedMetadataInformation.getKey(),
							EncryptedMetadataInformation.getIv());
					traverseFileSystem(file, subTree,
							EncryptedMetadataInformation);
				}
			}
		}

		// Save the tree
		EncryptedMiTree encryptedMiTree = decryptedMiTree.encrypt(
				miTreeInformation.getFileName(), miTreeInformation.getKey(),
				miTreeInformation.getIv());
		encryptedMiTreeRepo.saveEncryptedMetadata(encryptedMiTree,
				miTreeInformation.getFileName());
	}

	public void synchronizeLocalMetadataWithRemoteMetadata() {
		log.info("Starting complete synchronization of mibox with remote metadata");
		EncryptedMetadataInformation rootInfo = metaMetaDataHolder
				.getDecryptedMetaMetaData().getRoot();
		EncryptedMiTree localRootEncrypted = encryptedMiTreeRepo
				.loadEncryptedMetadata(rootInfo.getFileName());
		DecryptedMiTree localRoot = null;
		if (localRootEncrypted != null) {
			log.debug("decrypting local root");
			localRoot = localRootEncrypted.decrypt(rootInfo.getKey(),
					rootInfo.getIv());
		}
		EncryptedMiTree remoteRootEncrypted = encryptedMiTreeRepo
				.loadRemoteEncryptedMetadata(rootInfo.getFileName());
		DecryptedMiTree remoteRoot = remoteRootEncrypted.decrypt(
				rootInfo.getKey(), rootInfo.getIv());
		appSettings = appSettingsDao.load();
		File file = new File(appSettings.getWatchDirectory());
		synchronizeLocalMetadataWithRemoteMetadata(file, localRoot, remoteRoot);
	}

	public void synchronizeLocalMetadataWithRemoteMetadata(File f,
			DecryptedMiTree local, DecryptedMiTree remote) {
		log.info("starting incoming synchronization of folder [{}]",
				f.getAbsolutePath());
		if (local == null) {
			log.debug("local metadata not available for folder [{}]",
					f.getAbsolutePath());
			// In this case the incoming folder is new and we need to create a
			// local folder
			local = new DecryptedMiTree();
			local.setFolderName(f.getName());
		}
		// Remember which files got processed in the first loop
		Set<String> processedFiles = new HashSet<>();
		// Get all files from the local metadata and compare them to the remote
		// metadata.
		for (String localMFileName : local.getFiles().keySet()) {
			log.debug("Comparing MFiles for [{}]", localMFileName);
			DecryptedMiFile localMFile = getDecryptedMiFile(false, local
					.getFiles().get(localMFileName));
			DecryptedMiFile remoteMFile = getDecryptedMiFile(true, remote
					.getFiles().get(localMFileName));
			// if the remote file is newer than the local file we want to update
			// it
			if (remoteMFile == null
					|| remoteMFile.getLastModified().after(
							localMFile.getLastModified())) {
				log.info(
						"remote file is newer than local file. will request download for [{}]",
						localMFileName);
				File file = new File(f, localMFileName);
				updateFileFromMetadata(file, localMFile, remoteMFile);
			}
			// we remember which files has been processed by us
			processedFiles.add(localMFileName);
		}
		// Now we want to iterate over all remote files which have not been
		// processed yet. This for we remove already processed files from the
		// remote files.
		Set<String> newRemoteFileNames = new HashSet<>(remote.getFiles()
				.keySet());
		newRemoteFileNames.removeAll(processedFiles);
		for (String remoteMFileName : newRemoteFileNames) {
			log.info("incoming new file [{}]", remoteMFileName);
			DecryptedMiFile localMFile = null;
			DecryptedMiFile remoteFile = getDecryptedMiFile(true,remote.getFiles().get(remoteMFileName));
			File file = new File(f, remoteMFileName);
			updateFileFromMetadata(file, localMFile, remoteFile);
		}
		// TODO don't forget to update the local metadata if a file got
		// updated!!!

		// And now lets compare the subfolders
		Map<String, EncryptedMetadataInformation> localSubFolders = local
				.getSubfolder();
		Map<String, EncryptedMetadataInformation> remoteSubFolders = remote
				.getSubfolder();

		Set<String> processedFolders = new HashSet<>();
		for (String localFolderName : localSubFolders.keySet()) {
			EncryptedMetadataInformation localMiTreeInfo = localSubFolders
					.get(localFolderName);
			DecryptedMiTree localMiTree = encryptedMiTreeRepo
					.loadEncryptedMetadata(localMiTreeInfo.getFileName())
					.decrypt(localMiTreeInfo.getKey(), localMiTreeInfo.getIv());
			// TODO what happens if the folder has been deleted on another
			// client
			EncryptedMiTree remoteMiTreeEncrypted = encryptedMiTreeRepo
					.loadRemoteEncryptedMetadata(localMiTreeInfo.getFileName());
			DecryptedMiTree remoteMiTree = remoteMiTreeEncrypted.decrypt(
					localMiTreeInfo.getKey(), localMiTreeInfo.getIv());
			synchronizeLocalMetadataWithRemoteMetadata(new File(f,
					localFolderName), localMiTree, remoteMiTree);
			processedFolders.add(localFolderName);
		}

		Set<String> newRemoteFolders = new HashSet<>(remoteSubFolders.keySet());
		newRemoteFolders.removeAll(processedFolders);
		for (String remoteFolderName : newRemoteFolders) {
			EncryptedMetadataInformation localMiTreeInfo = remoteSubFolders
					.get(remoteFolderName);
			DecryptedMiTree localMiTree = null;
			EncryptedMiTree remoteMiTreeEncrypted = encryptedMiTreeRepo
					.loadRemoteEncryptedMetadata(localMiTreeInfo.getFileName());
			DecryptedMiTree remoteMiTree = remoteMiTreeEncrypted.decrypt(
					localMiTreeInfo.getKey(), localMiTreeInfo.getIv());
			synchronizeLocalMetadataWithRemoteMetadata(new File(f,
					remoteFolderName), localMiTree, remoteMiTree);
		}
	}

	public DecryptedMiFile getDecryptedMiFile(boolean remote,
			EncryptedMetadataInformation encInfLocal) {
		EncryptedMiFile encMiFileLocal;
		if (remote) {
			encMiFileLocal = encryptedMiFileRepo
					.loadRemoteEncryptedMetadata(encInfLocal.getFileName());
		} else {
			encMiFileLocal = encryptedMiFileRepo
					.loadEncryptedMetadata(encInfLocal.getFileName());
		}
		return encMiFileLocal.decrypt(encInfLocal);
	}

	/**
	 * Synchronizes incoming MFile with the local metadata.
	 * 
	 * If the file already exists on the filesystem it gets updated. Otherwise
	 * the file is created.
	 * 
	 * <b>Warning: both params must not be null at the same time!</b>
	 * 
	 * @param file
	 *            File to update. May be non-existent.
	 * @param localMFile
	 *            the local metadata. may be null if there is no local file yet.
	 * @param incomingMFile
	 *            the incoming metadata. may be null if a local file shall be
	 *            deleted.
	 * 
	 * 
	 */
	public void updateFileFromMetadata(final File file,
			final DecryptedMiFile localMFile,
			final DecryptedMiFile incomingMFile) {
		if (localMFile == null && incomingMFile == null) {
			return;
		}
		if (localMFile != null) {
			// probably a change to local file
		} else {
			// probably a new file
			final AppSettings appSettings = appSettingsDao.load();
			final RequestContainer<DownloadRequest> downloadRequestContainer = new RequestContainer<>();
			for (MChunk currentMChunk : incomingMFile.getChunks()) {
				final MChunk mChunk = currentMChunk;
				final DownloadRequest request = new DownloadRequest(
						currentMChunk.getEncryptedChunkHash());
				request.setTransportCallback(createChunkDownloadCompletedCallback(
						file, appSettings, downloadRequestContainer, mChunk,
						request));
				downloadRequestContainer.add(request);
			}
			downloadRequestContainer
					.setAllChildrenCompletedCallback(createDownloadContainerFinishedCallback(
							file, incomingMFile, appSettings));
			transportProvider.addDownloadContainer(downloadRequestContainer);
		}

		log.debug("start file update from incoming metadata! [{}]",
				incomingMFile != null ? incomingMFile.getName() : "");
	}

	/**
	 * Creates a callback which is executed when all chunk downloads have been
	 * successfully processed. In this callback the downloaded and decrypted
	 * chunks (which are stored in the temp directory) are written to the target
	 * file in the right order. Afterwards the temporary chunks are delted.
	 * 
	 * @param file
	 *            The target file which is created from all temporary chunks.
	 * @param incomingMFile
	 *            Metadata containing information on the file.
	 * @param appSettings
	 *            The settings are used for the retrieval of the temp-dir path.
	 * @return A callback for finished download containers.
	 */
	public TransportCallback createDownloadContainerFinishedCallback(
			final File file, final DecryptedMiFile incomingMFile,
			final AppSettings appSettings) {
		return new TransportCallback() {
			@Override
			public void transportCallback(Map<String, Object> parameter) {
				File parent = new File(file.getParent());
				if (!parent.exists()) {
					parent.mkdirs();
				}
				try (FileOutputStream fos = new FileOutputStream(file);
						FileChannel channelDestination = fos.getChannel()) {
					long position = 0;
					for (MChunk mChunk : incomingMFile.getChunks()) {
						String pathHash = HashUtil.calculateSha256(file
								.getAbsolutePath().getBytes());
						String tmpfilename = pathHash + "."
								+ mChunk.getPosition();
						File decryptedChunkFile = new File(
								appSettings.getTempDirectory(), tmpfilename);
						try (FileInputStream fis = new FileInputStream(
								decryptedChunkFile);
								FileChannel channelSource = fis.getChannel()) {
							channelDestination.transferFrom(channelSource,
									position, decryptedChunkFile.length());
							position += decryptedChunkFile.length();
						}
					}

					for (MChunk mChunk : incomingMFile.getChunks()) {
						String pathHash = HashUtil.calculateSha256(file
								.getAbsolutePath().getBytes());
						String tmpfilename = pathHash + "."
								+ mChunk.getPosition();
						File decryptedChunkFile = new File(
								appSettings.getTempDirectory(), tmpfilename);
						decryptedChunkFile.delete();
					}

				} catch (Exception ioe) {
					throw new RuntimeException(ioe);
				}
			}
		};
	}

	/**
	 * Creates a callback for {@link DownloadRequest}s. In this callback the
	 * downloaded chunk content gets decrypted and written to the temporary
	 * folder.
	 * 
	 * @param file
	 *            this file will contain the decrypted chunk as a part of it.
	 * @param appSettings
	 *            The settings are used for the retrieval of the temp directory
	 *            path.
	 * @param downloadRequestContainer
	 *            A reference to the container the chunk belongs to. The
	 *            callback has to tell the container that the request has been
	 *            processed.
	 * @param mChunk
	 *            The mChunk which has been processed for this callback.
	 * @param request
	 *            The request which triggered this callback.
	 * @return A callback for chunk {@link DownloadRequest}s.
	 */
	public TransportCallback createChunkDownloadCompletedCallback(
			final File file, final AppSettings appSettings,
			final RequestContainer<DownloadRequest> downloadRequestContainer,
			final MChunk mChunk, final DownloadRequest request) {
		return new TransportCallback() {
			@Override
			public void transportCallback(Map<String, Object> parameter) {
				byte[] content = (byte[]) parameter.get(CALLBACK_PARAM_CONTENT);
				DataChunk decrypted = chunkEncryption.decryptChunk(mChunk,
						content);
				String pathHash = HashUtil.calculateSha256(file
						.getAbsolutePath().getBytes());
				String tmpfilename = pathHash + "." + mChunk.getPosition();
				File decryptedChunkFile = new File(
						appSettings.getTempDirectory(), tmpfilename);
				try (FileOutputStream fos = new FileOutputStream(
						decryptedChunkFile)) {
					fos.write(decrypted.getContent());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				downloadRequestContainer.oneChildCompleted(request);
			}
		};
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
	private void synchronizeFileMetadata(final File f,
			final EMiFile emiFile) {
		// Check whether the file has been modified since the last meta sync
		log.debug("Start synchronization for file [{}]", f.getAbsolutePath());
		final Date filesystemLastModified = new Date(f.lastModified());
		DecryptedMiFile mFile = emiFile.getEncryptableObject();
		if ((mFile.getLastModified() == null)
				|| (filesystemLastModified.after(mFile.getLastModified()))) {
			// The file has been modified, so we have to update metadata
			log.debug(
					"File newer than last modification date. Calculating file and chunk hashes for [{}]",
					f.getName());
			try {
				RequestContainer<ChunkUploadRequest> uploadContainer = new RequestContainer<>();
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
						chunk.setDecryptedChunkHash(newChunkHash);
						log.debug("New Chunk [{}] finished with hash [{}]",
								currentChunk, newChunkHash);
						// Create Upload request
						uploadContainer.add(createUploadRequest(chunk, f,
								uploadContainer));
					}
					currentChunk++;
				}
				mFile.setFileHash(HashUtil.digestToString(fileDigest.digest()));
				mFile.setLastModified(filesystemLastModified);
				// Define a callback which is executed when all chunks have been
				// uploaded.
				uploadContainer
						.setAllChildrenCompletedCallback(createUploadContainerFinishedCallback(
								f, emiFile));
				transportProvider.addUploadContainer(uploadContainer);
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
	 * Creates a Callback which is executed when all uploads for a file have
	 * been successfully executed. The callback persists the metadata (
	 * {@link DecryptedMiFile} ) which got updated during the upload callback of
	 * each chunk. At this point the {@link MChunk} inside the mFile contains the
	 * new encrypted chunk hash.
	 * 
	 * @param f
	 *            The current file which has been uploaded.
	 * @param mFile
	 *            The metadata of the file. This metadata gets persisted in the
	 *            callback.
	 * @return A callback for finished upload containers.
	 */
	public TransportCallback createUploadContainerFinishedCallback(
			final File f, final EMiFile mFile) {
		return new TransportCallback() {
			@Override
			public void transportCallback(Map<String, Object> parameter) {
				DecryptedMiFile decryptedMiFile = mFile.getEncryptableObject();
				EncryptedMetadataInformation encInfo = mFile.getEncryptedMiTreeInformation();
				EncryptedMiFile encryptedMiFile = decryptedMiFile.encrypt(encInfo);
				encryptedMiFileRepo.saveEncryptedMetadata(encryptedMiFile, encInfo.getFileName());
			}
		};
	}

	/**
	 * Creates a {@link ChunkUploadRequest} object and turns it over to the
	 * {@link TransportProvider}. Persists the metadata to disk.
	 * 
	 * @param chunk
	 *            the chunk which shall be uploaded.
	 */
	protected ChunkUploadRequest createUploadRequest(final MChunk chunk,
			File file,
			final RequestContainer<ChunkUploadRequest> requestContainer) {
		log.debug("Creating upload request for chunk [{}]",
				chunk.getDecryptedChunkHash());
		// TODO inject encryption provider
		final ChunkUploadRequest mChunkUpload = new ChunkUploadRequest(chunk,
				file, null, new AesChunkEncryption());
		mChunkUpload.setUploadCallback(new TransportCallback() {
			@Override
			public void transportCallback(Map<String, Object> parameter) {
				String encryptedHash = (String) parameter
						.get(CALLBACK_PARAM_ENCRYPTED_CHUNK_HASH);
				chunk.setEncryptedChunkHash(encryptedHash);
				chunk.setLastSync(new Date());
				requestContainer.oneChildCompleted(mChunkUpload);
				if (isDecryptedDebugEnabled()) {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						System.out.println(objectMapper
								.writeValueAsString(chunk));
					} catch (Exception e) {
					}
				}
			}
		});
		log.debug("returning upload request [{}]", mChunkUpload);
		return mChunkUpload;
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
			synchronizeFilesystemWithLocalMetadata();
			AppSettings appSettings = appSettingsDao.load();
			while (active) {
				ObservedFilesystemEvent ofe;
				while ((ofe = incomingEvents.pollFirst()) != null) {
					log.debug("Processing event " + ofe);
					File f = new File(ofe.getFilename());
					if (f.isFile()) {
						String filename = ofe.getFilename();
						String relativePath = getRelativePath(appSettings,
								filename);
						System.out.println(relativePath);

						EncryptedMetadataInformation miTreeInformation = metaMetaDataHolder
								.getDecryptedMetaMetaData().getRoot();
						EncryptedMiTree encryptedRoot = encryptedMiTreeRepo
								.loadEncryptedMetadata(miTreeInformation
										.getFileName());
						EMiFile mFile = metadataUtil
								.locateMFile(relativePath);
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
		} catch (CryptoException e) {
			log.error("Cannot decrypt metadata!", e);
			e.printStackTrace();
		}
	}

	/**
	 * @param appSettings
	 * @param filename
	 * @return
	 */
	public String getRelativePath(AppSettings appSettings, String filename) {
		return StringUtils.substringAfter(
				FilenameUtils.separatorsToUnix(filename),
				appSettings.getWatchDirectory());
	}
}
