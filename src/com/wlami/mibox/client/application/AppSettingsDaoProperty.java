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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Named;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class AppSettingsDaoProperty implements AppSettingsDao {

	/**
	 * Constant for accessing the path to the AppSettings properties file.
	 */
	public static final String APP_SETTINGS = "./res/settings.properties";

	/**
	 * current valid application settings.
	 */
	AppSettings appSettings;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettingsDao#load()
	 */
	@Override
	public AppSettings load() throws IOException {
		if (appSettings == null) {
			appSettings = loadFromPropertyFile();
		}
		return appSettings.clone();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private AppSettings loadFromPropertyFile() throws IOException {
		AppSettings appSettingsTmp = new PropertyAppSettings();
		// Read our settings file
		Properties properties = new Properties();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(APP_SETTINGS));
		properties.load(bufferedInputStream);
		bufferedInputStream.close();

		// Read the properties and fill the variables
		appSettingsTmp
				.setShowDesktopNotification(Boolean.parseBoolean(properties
						.getProperty(PropertyAppSettings.SHOW_DESKTOP_NOTIFICATION)));
		appSettingsTmp.setStartAtSystemStartup(Boolean.parseBoolean(properties
				.getProperty(PropertyAppSettings.START_AT_SYSTEM_STARTUP)));
		appSettingsTmp.setUsername(properties
				.getProperty(PropertyAppSettings.USERNAME));
		appSettingsTmp.setPassword(properties
				.getProperty(PropertyAppSettings.PASSWORD));
		appSettingsTmp.setWatchDirectory(properties
				.getProperty(PropertyAppSettings.WATCH_DIRECTORY));
		appSettingsTmp.setLanguage(properties
				.getProperty(PropertyAppSettings.LANGUAGE));
		appSettingsTmp.setCountry(properties
				.getProperty(PropertyAppSettings.COUNTRY));
		return appSettingsTmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.application.AppSettingsDao#save(com.wlami.mibox
	 * .client.application.AppSettings)
	 */
	@Override
	public void save(AppSettings appSettings) throws IOException {
		saveToPropertyFile(appSettings);
		this.appSettings = appSettings;
		// TODO: trigger listener
	}

	/**
	 * @param appSettings2
	 * @throws IOException
	 */
	private void saveToPropertyFile(AppSettings pAppSettings)
			throws IOException {
		Properties props = new Properties();
		props.setProperty(PropertyAppSettings.SHOW_DESKTOP_NOTIFICATION,
				pAppSettings.getShowDesktopNotification().toString());
		props.setProperty(PropertyAppSettings.START_AT_SYSTEM_STARTUP,
				pAppSettings.getStartAtSystemStartup().toString());
		props.setProperty(PropertyAppSettings.USERNAME,
				pAppSettings.getUsername());
		props.setProperty(PropertyAppSettings.PASSWORD,
				pAppSettings.getPassword());
		props.setProperty(PropertyAppSettings.WATCH_DIRECTORY,
				pAppSettings.getWatchDirectory());
		props.setProperty(PropertyAppSettings.LANGUAGE,
				pAppSettings.getLanguage());
		props.setProperty(PropertyAppSettings.COUNTRY,
				pAppSettings.getCountry());
		FileOutputStream fileOutputStream = new FileOutputStream(new File(
				APP_SETTINGS));
		props.store(fileOutputStream, "auto generated settings");
	}

}
