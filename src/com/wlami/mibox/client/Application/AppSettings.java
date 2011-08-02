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
package com.wlami.mibox.client.Application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Wladislaw Mitzel
 *
 */
public class AppSettings {
	
	/**
	 * Private constructor, so that readAppSettings is the only way to get an instance of this class
	 */
	private AppSettings() {}
	
	/**
	 * Constant for accessing bool showDesktopNotification
	 */
	protected final static String SHOW_DESKTOP_NOTIFICATION = "behavior.show_settings_notification";
	/**
	 * Determines whether there should be tray notifications on changes.
	 */
	private Boolean showDesktopNotification;
	
	
	/**
	 * Constant for accessing bool startAtSystemStartup
	 */
	protected final static String START_AT_SYSTEM_STARTUP = "behavior.start_at_system_startup";
	/**
	 * Determines whether this app should be startet during the boot procedure.
	 */
	private Boolean startAtSystemStartup;
	
	
	/**
	 * Constant for accessing String username
	 */
	protected final static String USERNAME = "account.username";
	/**
	 * Username for accessing remote repository
	 */
	private String username;
	
	
	/**
	 * Constant for accessing String password
	 */
	protected final static String PASSWORD = "account.password";
	/**
	 * Password for accessing remote repository
	 */
	private String password;

	/**
	 * Read the AppSettings properties file and create a new AppSettings instance
	 * @param settingsFile path to the properties file
	 * @return returns an Instance of AppSettings
	 * @throws IOException thrown, if the given path doesn't point to the right file
	 */
	public static AppSettings readAppSettings(String settingsFile) throws IOException  {
		
		//Read our settings file
		Properties appSettings = new Properties();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(settingsFile));
		appSettings.load(bufferedInputStream);
		bufferedInputStream.close();
		
		//Create a new instance of AppSettings
		AppSettings a = new AppSettings();
		
		//Read the properties and fill the variables
		a.showDesktopNotification = Boolean.parseBoolean( appSettings.getProperty(SHOW_DESKTOP_NOTIFICATION) );
		a.startAtSystemStartup = Boolean.parseBoolean( appSettings.getProperty(START_AT_SYSTEM_STARTUP) );
		a.username = appSettings.getProperty(USERNAME);
		a.password = appSettings.getProperty(PASSWORD);
		
		return a;
	}
	
	/**
	 * Writes the given AppSettings to a properties file on disk
	 * @param appSettings 
	 * @param settingsFile
	 * @throws IOException
	 */
	public static void writeAppSettings(AppSettings appSettings, String settingsFile) throws IOException {
		
		Properties props = new Properties();
		props.setProperty(SHOW_DESKTOP_NOTIFICATION, appSettings.isShowDesktopNotification().toString());
		props.setProperty(START_AT_SYSTEM_STARTUP, appSettings.isStartAtSystemStartup().toString());
		props.setProperty(USERNAME, appSettings.getUsername());
		props.setProperty(PASSWORD, appSettings.getPassword());
		
		FileOutputStream fileOutputStream = new FileOutputStream(new File(settingsFile));
		props.store(fileOutputStream, "auto generated settings");
	}

	/**
	 * @return the showDesktopNotification
	 */
	public Boolean isShowDesktopNotification() {
		return showDesktopNotification;
	}

	/**
	 * @param showDesktopNotification the showDesktopNotification to set
	 */
	public void setShowDesktopNotification(Boolean showDesktopNotification) {
		this.showDesktopNotification = showDesktopNotification;
	}

	/**
	 * @return the startAtSystemStartup
	 */
	public Boolean isStartAtSystemStartup() {
		return startAtSystemStartup;
	}

	/**
	 * @param startAtSystemStartup the startAtSystemStartup to set
	 */
	public void setStartAtSystemStartup(Boolean startAtSystemStartup) {
		this.startAtSystemStartup = startAtSystemStartup;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
