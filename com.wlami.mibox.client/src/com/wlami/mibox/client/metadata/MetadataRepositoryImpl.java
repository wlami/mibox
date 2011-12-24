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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

		/**
		 * 
		 */
		public MetadataWorker() {
			AppSettings appSetting;
			try {
				appSetting = appSettingsDao.load();
				ObjectMapper objectMapper = new ObjectMapper();
				File metadataFile = new File(METADATA_DEFAULT_FILENAME) {
				};
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
				synchronizeMetadata();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		protected void synchronizeMetadata() throws IOException {
			AppSettings appSettings = appSettingsDao.load();
			String watchDir = appSettings.getWatchDirectory();
			Files.walkFileTree(Paths.get(watchDir),
					new SimpleFileVisitor<Path>() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * java.nio.file.SimpleFileVisitor#preVisitDirectory
						 * (java.lang.Object,
						 * java.nio.file.attribute.BasicFileAttributes)
						 */
						@Override
						public FileVisitResult preVisitDirectory(Path dir,
								BasicFileAttributes attrs) throws IOException {
							// TODO Auto-generated method stub
							return super.preVisitDirectory(dir, attrs);
						}

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * java.nio.file.SimpleFileVisitor#visitFile(java.lang
						 * .Object, java.nio.file.attribute.BasicFileAttributes)
						 */
						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							// TODO Auto-generated method stub
							return super.visitFile(file, attrs);
						}

					});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			log.debug("Starting");
			while (active) {
				try {
					for (ObservedFilesystemEvent ofe : incomingEvents) {
						log.debug("Processing event " + ofe);
						// TODO: process the event!
						incomingEvents.remove(ofe);
					}
					Thread.sleep(DEFAULT_SLEEP_TIME);
				} catch (InterruptedException e) {
				}

			}
		}

	}

}
