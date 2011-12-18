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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.application.NewAppSettingsListener;

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
	 * default constructor.
	 */
	@Inject
	public DirectoryWatchdog(AppSettingsDao appSettingsDao) {
		active = true;
		changedDirectory = false;
		this.appSettingsDao = appSettingsDao;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting watchdog thread");
		while (active) {
			if (directory != null) {
				log.info("watching for changes in [" + directory + "]");
				changedDirectory = false;
				startObservation();
			} else {
				log.warn("directory property is null!");
				break;
			}
		}
		log.info("Stopping watchdog thread");
	}

	/**
	 * 
	 */
	protected void startObservation() {
		try {
			WatchService ws = FileSystems.getDefault().newWatchService();
			WatchKey wk = Paths.get(directory).register(ws, ENTRY_CREATE,
					ENTRY_DELETE, ENTRY_MODIFY);
			log.debug("Waiting for keys");
			while (!changedDirectory) {
				wk = ws.poll(1L, TimeUnit.SECONDS);
				if (wk != null) {
					for (WatchEvent<?> watchEvent : wk.pollEvents()) {
						WatchEvent.Kind<?> kind = watchEvent.kind();
						if (kind.equals(ENTRY_CREATE)) {
							log.debug("Observed an create event. ["
									+ watchEvent.context() + "]");

						} else if (kind.equals(ENTRY_MODIFY)) {
							log.debug("Observed an modify event. ["
									+ watchEvent.context() + "]");

						} else if (kind.equals(ENTRY_DELETE)) {
							log.debug("Observed an delete event. ["
									+ watchEvent.context() + "]");

						} else {

						}
					}
					wk.reset();
				}
			}
			ws.close();
		} catch (IOException e) {
			log.error(e.toString());
		} catch (InterruptedException e) {
			log.error(e.toString());
		}

	}

	/**
	 * Creates a NewAppSettingsListener which is called whenever new settings
	 * are available.
	 * 
	 * @return the event handler
	 */
	private NewAppSettingsListener getNewAppSettingsListener() {
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
			}
		};
	}
}
