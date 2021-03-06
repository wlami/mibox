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
package com.wlami.mibox.client.application;

import java.io.File;

/**
 * Provides information on system paths.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class AppFolders {

	/**
	 * Gets the system dependend path to the user.home.
	 * 
	 * @return Path to user home.
	 */
	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	/**
	 * Gets the path to the MiBox config folder. It is located in user.home and
	 * gets created if non-existent.
	 * 
	 * @return Path to config folder.
	 */
	public static String getConfigFolder() {
		String configFolder = getUserHome() + "/.mibox";
		File f = new File(configFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return configFolder;
	}
}
