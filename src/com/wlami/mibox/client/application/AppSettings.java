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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Wladislaw Mitzel
 *
 */
public interface AppSettings {

	/**
	 * populates the object with data from properties file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public abstract void load() throws FileNotFoundException, IOException;

	/**
	 * Writes the given AppSettings to a properties file on disk.
	 * 
	 * @throws IOException
	 *             thrown if an error occurs during writing of the file
	 */
	public abstract void save() throws IOException;

	/**
	 * @param showDesktopNotification
	 *            the showDesktopNotification to set
	 */
	public abstract void setShowDesktopNotification(
			final Boolean showDesktopNotification);

	/**
	 * @param startAtSystemStartup
	 *            the startAtSystemStartup to set
	 */
	public abstract void setStartAtSystemStartup(
			final Boolean startAtSystemStartup);

	/**
	 * @return the username
	 */
	public abstract String getUsername();

	/**
	 * @param username
	 *            the username to set
	 */
	public abstract void setUsername(final String username);

	/**
	 * @return the password
	 */
	public abstract String getPassword();

	/**
	 * @param password
	 *            the password to set
	 */
	public abstract void setPassword(final String password);

	/**
	 * @return the watchDirectory
	 */
	public abstract String getWatchDirectory();

	/**
	 * @param watchDirectory
	 *            the watchDirectory to set
	 */
	public abstract void setWatchDirectory(final String watchDirectory);

	/**
	 * @return the language
	 */
	public abstract String getLanguage();

	/**
	 * @param language
	 *            the language to set
	 */
	public abstract void setLanguage(String language);

	/**
	 * @return the country
	 */
	public abstract String getCountry();

	/**
	 * @param country
	 *            the country to set
	 */
	public abstract void setCountry(String country);

	/**
	 * @return the showDesktopNotification
	 */
	public abstract Boolean getShowDesktopNotification();

	/**
	 * @return the startAtSystemStartup
	 */
	public abstract Boolean getStartAtSystemStartup();

}