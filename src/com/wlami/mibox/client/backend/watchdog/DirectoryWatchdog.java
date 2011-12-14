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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class DirectoryWatchdog extends Thread {

	Logger log = LoggerFactory.getLogger(DirectoryWatchdog.class.getName());

	/**
	 * contains the path to the directory that shall be observed.
	 */
	private String directory;

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
		if (directory != null) {
			log.info("watching for changes in [" + directory + "]");
		} else {
			log.warn("directory property is null!");
		}
		log.info("Stopping watchdog thread");
	}
}
