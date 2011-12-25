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
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		private static final String METADATA_DEFAULT_FILENAME = ".mibox";

		private final Logger log = LoggerFactory
				.getLogger(MetadataWorker.class);

		private boolean active = true;

		private static final long DEFAULT_SLEEP_TIME = 250L;

		/** the internal file structure stored as an {@link MFolder} instance. */
		private MFolder rootFolder;

		/** JSON Object mapper for persistence. */
		private ObjectMapper objectMapper = new ObjectMapper();

		/** File instance of json persistence file. */
		File metadataFile = new File(METADATA_DEFAULT_FILENAME);

		/**
		 * 
		 */
		public MetadataWorker() {
			AppSettings appSetting;
			try {
				appSetting = appSettingsDao.load();
				;
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
					// TODO inspect file here!
				} else {
					// What do we have over here, not a file and not a dir?!
					log.error("wtf? " + f.getName());
				}
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
