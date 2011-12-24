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
package com.wlami.mibox.client.backend.watchdog;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.application.NewAppSettingsListener;
import com.wlami.mibox.client.metadata.MetadataRepository;
import com.wlami.mibox.client.metadata.ObservedFilesystemEvent;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class DirectoryWatchdog extends Thread {

	Logger log = LoggerFactory.getLogger(DirectoryWatchdog.class.getName());

	/**
	 * contains the path to the directory that shall be observed.
	 */
	private String directory;

	private WatchService watchService;

	private Map<WatchKey, Path> keyMap;
	/**
	 * determines whether the watchdog shall watch currently.
	 */
	private boolean active;

	/**
	 * determines whether the directory has changed and a new watchservice is
	 * needed.
	 */
	private boolean changedDirectory;

	/**
	 * reference to the application settings data access object.
	 */
	private AppSettingsDao appSettingsDao;

	/**
	 * reference to a {@link MetadataRepository}.
	 */
	private MetadataRepository metadataRepository;

	/**
	 * default constructor.
	 */
	@Inject
	public DirectoryWatchdog(AppSettingsDao appSettingsDao,
			MetadataRepository metadataRepository) {
		this.appSettingsDao = appSettingsDao;
		this.metadataRepository = metadataRepository;
		this.keyMap = new HashMap<WatchKey, Path>();
		appSettingsDao
				.registerNewAppSettingsListener(getNewAppSettingsListener());
	}

	/**
	 * @return the directory.
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory
	 *            the directory to set.
	 */
	public void setDirectory(String directory) {
		log.debug("Setting directory to [" + directory + "]");
		this.directory = directory;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {

			while (active) {
				log.info("Activating watchdog");
				if (directory != null) {
					changedDirectory = false;
					log.info("watching for changes in [" + directory + "]");
					startObservation();
				} else {
					log.warn("directory property is null!");
					break;
				}
			}
			log.info("deactivating watchdog");
			while (!active) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Starts the directory observation. Registers it at a WatchService and
	 * checks for new Events.
	 */
	protected void startObservation() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			registerDirectory();
			log.debug("Waiting for keys");
			WatchKey wk;
			while (!changedDirectory) {
				wk = watchService.poll(250L, TimeUnit.MILLISECONDS);
				if (wk != null) {
					for (WatchEvent<?> watchEvent : wk.pollEvents()) {
						WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) watchEvent
								.kind();
						Path currentPath = keyMap.get(wk);
						ObservedFilesystemEvent ofe = new ObservedFilesystemEvent();
						ofe.setFilename(new File(currentPath.toString(),
								watchEvent.context().toString())
								.getAbsolutePath());
						ofe.setEventKind(kind);
						metadataRepository.addEvent(ofe);
						log.debug("Observed filesystem event: " + ofe);

					}
					wk.reset();
				}
			}
			watchService.close();
			keyMap.clear();
			watchService = null;
		} catch (IOException e) {
			log.error(e.toString());
		} catch (InterruptedException e) {
			log.error(e.toString());
		}

	}

	/**
	 * Traverses a directory and registers all subdirectories at the
	 * watchservice.
	 * 
	 * @throws IOException
	 */
	protected void registerDirectory() {
		Path rootDir = Paths.get(getDirectory());
		try {
			Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) {
					try {
						log.debug("Registering at watchService: "
								+ dir.toString());
						WatchKey watchKey = dir.register(watchService,
								ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
						keyMap.put(watchKey, dir.normalize());
					} catch (Exception e) {
						log.warn("Did not register " + dir.toString() + "\n"
								+ e.toString());
					}
					return FileVisitResult.CONTINUE;
				};
			});
		} catch (IOException e) {
			log.warn(e.toString());
			// TODO: Tell user something terrible happened!
		}
	}

	/**
	 * Creates a NewAppSettingsListener which is called whenever new settings
	 * are available.
	 * 
	 * @return the event handler
	 */
	private NewAppSettingsListener getNewAppSettingsListener() {
		final DirectoryWatchdog watchdog = this;
		return new NewAppSettingsListener() {
			@Override
			public void handleNewAppSettings(AppSettings appSettings) {
				log.debug("handling new settings");
				if (directory.equals(appSettings.getWatchDirectory())) {
					log.debug("watch directory did not change: " + directory);
				} else {
					directory = appSettings.getWatchDirectory();
					log.info("detected a new watch directory: " + directory);
					changedDirectory = true;
				}
				if (active != appSettings.getMonitoringActive()) {
					boolean newActive = appSettings.getMonitoringActive();
					if (newActive) {
						// Watchdog has been inactive and shall start now!
						watchdog.active = true;
					} else {
						// Deactive watchdog
						watchdog.active = false;
						watchdog.changedDirectory = true; // TODO: rename
					}
				}
			}
		};
	}
}
