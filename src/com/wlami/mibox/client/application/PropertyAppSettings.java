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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Singleton
@Named
public final class PropertyAppSettings implements AppSettings {

	/**
	 * Constant for accessing the path to the AppSettings properties file.
	 */
	public static final String APP_SETTINGS = "./res/settings.properties";

	/**
	 * Constant for accessing bool showDesktopNotification.
	 */
	protected static final String SHOW_DESKTOP_NOTIFICATION = "behavior.show_settings_notification";
	/**
	 * Determines whether there should be tray notifications on changes.
	 */
	private Boolean showDesktopNotification;

	/**
	 * Constant for accessing bool startAtSystemStartup.
	 */
	protected static final String START_AT_SYSTEM_STARTUP = "behavior.start_at_system_startup";
	/**
	 * Determines whether this app should be startet during the boot procedure.
	 */
	private Boolean startAtSystemStartup;

	/**
	 * Constant for accessing String username.
	 */
	protected static final String USERNAME = "account.username";
	/**
	 * Username for accessing remote repository.
	 */
	private String username;

	/**
	 * Constant for accessing String password.
	 */
	protected static final String PASSWORD = "account.password";
	/**
	 * Password for accessing remote repository.
	 */
	private String password;

	/**
	 * Constant for accessing String watchDirectory.
	 */
	protected static final String WATCH_DIRECTORY = "monitoring.watch_directory";
	/**
	 * Directory which is synchronized.
	 */
	private String watchDirectory;

	/**
	 * Constant for accessing String language
	 */
	protected static final String LANGUAGE = "lang";

	/**
	 * User language.
	 */
	private String language;

	/**
	 * Constant for accessing String country
	 */
	protected static final String COUNTRY = "country";

	/**
	 * User country
	 */
	private String country;

	/**
	 * default constructor. calls the load-method
	 * 
	 * @throws IOException
	 */
	public PropertyAppSettings() throws IOException {
		load();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#load()
	 */
	@Override
	public void load() throws FileNotFoundException, IOException {
		// Read our settings file
		Properties appSettings = new Properties();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(APP_SETTINGS));
		appSettings.load(bufferedInputStream);
		bufferedInputStream.close();

		// Read the properties and fill the variables
		showDesktopNotification = Boolean.parseBoolean(appSettings
				.getProperty(SHOW_DESKTOP_NOTIFICATION));
		startAtSystemStartup = Boolean.parseBoolean(appSettings
				.getProperty(START_AT_SYSTEM_STARTUP));
		username = appSettings.getProperty(USERNAME);
		password = appSettings.getProperty(PASSWORD);
		watchDirectory = appSettings.getProperty(WATCH_DIRECTORY);
		language = appSettings.getProperty(LANGUAGE);
		country = appSettings.getProperty(COUNTRY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#save()
	 */
	@Override
	public void save() throws IOException {

		Properties props = new Properties();
		props.setProperty(SHOW_DESKTOP_NOTIFICATION,
				showDesktopNotification.toString());
		props.setProperty(START_AT_SYSTEM_STARTUP,
				startAtSystemStartup.toString());
		props.setProperty(USERNAME, username);
		props.setProperty(PASSWORD, password);
		props.setProperty(WATCH_DIRECTORY, watchDirectory);
		props.setProperty(LANGUAGE, language);
		props.setProperty(COUNTRY, country);
		FileOutputStream fileOutputStream = new FileOutputStream(new File(
				APP_SETTINGS));
		props.store(fileOutputStream, "auto generated settings");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setShowDesktopNotification
	 * (java.lang.Boolean)
	 */
	@Override
	public void setShowDesktopNotification(final Boolean showDesktopNotification) {
		this.showDesktopNotification = showDesktopNotification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setStartAtSystemStartup
	 * (java.lang.Boolean)
	 */
	@Override
	public void setStartAtSystemStartup(final Boolean startAtSystemStartup) {
		this.startAtSystemStartup = startAtSystemStartup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setUsername(java.lang
	 * .String)
	 */
	@Override
	public void setUsername(final String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setPassword(java.lang
	 * .String)
	 */
	@Override
	public void setPassword(final String password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#getWatchDirectory()
	 */
	@Override
	public String getWatchDirectory() {
		return watchDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setWatchDirectory(java
	 * .lang.String)
	 */
	@Override
	public void setWatchDirectory(final String watchDirectory) {
		this.watchDirectory = watchDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setLanguage(java.lang
	 * .String)
	 */
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettings1#getCountry()
	 */
	@Override
	public String getCountry() {
		return country;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#setCountry(java.lang.
	 * String)
	 */
	@Override
	public void setCountry(String country) {
		this.country = country;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#getShowDesktopNotification
	 * ()
	 */
	@Override
	public Boolean getShowDesktopNotification() {
		return showDesktopNotification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettings1#getStartAtSystemStartup()
	 */
	@Override
	public Boolean getStartAtSystemStartup() {
		return startAtSystemStartup;
	}

}
