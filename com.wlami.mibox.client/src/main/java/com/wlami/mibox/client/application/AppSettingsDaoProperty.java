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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class AppSettingsDaoProperty implements AppSettingsDao {

	/**
	 * internal logging object.
	 */
	protected static Logger log = LoggerFactory
			.getLogger(AppSettingsDaoProperty.class.getName());

	/**
	 * Constant for accessing the path to the AppSettings properties file.
	 */
	public static final String APP_SETTINGS = "./res/settings.properties";

	/**
	 * current valid application settings.
	 */
	AppSettings appSettings;

	CopyOnWriteArrayList<NewAppSettingsListener> appSettingsListeners;

	/**
	 * default constructor.
	 */
	public AppSettingsDaoProperty() {
		appSettingsListeners = new CopyOnWriteArrayList<NewAppSettingsListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettingsDao#load()
	 */
	@Override
	public AppSettings load() {
		log.debug("loading appsettings");
		if (appSettings == null) {
			try {
				appSettings = loadFromPropertyFile();
			} catch (IOException e) {
				log.error("Error during loading of AppSettings", e);
			}
		}
		return appSettings.clone();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private AppSettings loadFromPropertyFile() throws IOException {
		log.info("Loading application settings from properties file.");
		AppSettings appSettingsTmp = new PropertyAppSettings();
		// Read our settings file
		Properties properties = new Properties();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(APP_SETTINGS));
		properties.load(bufferedInputStream);
		log.debug("Loaded AppSettings: " + properties.toString());
		bufferedInputStream.close();

		// Read the properties and fill the variables
		appSettingsTmp.setShowDesktopNotification(Boolean.parseBoolean(properties
				.getProperty(PropertyAppSettings.SHOW_DESKTOP_NOTIFICATION)));
		appSettingsTmp.setStartAtSystemStartup(Boolean.parseBoolean(properties
				.getProperty(PropertyAppSettings.START_AT_SYSTEM_STARTUP)));
		appSettingsTmp.setUsername(properties.getProperty(PropertyAppSettings.USERNAME));
		appSettingsTmp.setPassword(properties.getProperty(PropertyAppSettings.PASSWORD));
		appSettingsTmp.setServerUrl(properties.getProperty(PropertyAppSettings.SERVER_URL));
		appSettingsTmp.setWatchDirectory(properties.getProperty(PropertyAppSettings.WATCH_DIRECTORY));
		appSettingsTmp.setMonitoringActive(Boolean.parseBoolean(properties
				.getProperty(PropertyAppSettings.MONITORING_ACTIVE)));
		appSettingsTmp.setLanguage(properties.getProperty(PropertyAppSettings.LANGUAGE));
		appSettingsTmp.setCountry(properties.getProperty(PropertyAppSettings.COUNTRY));
		appSettingsTmp.setTempDirectory(properties.getProperty(PropertyAppSettings.TEMP_DIRECTORY));
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
	public void save(AppSettings appSettings) {
		log.debug("persisting application settings");
		try {
			saveToPropertyFile(appSettings);
		} catch (IOException e) {
			log.error("Error during saving of AppSettings", e);
		}
		this.appSettings = appSettings.clone();
		// notify each listener
		for (NewAppSettingsListener listener : appSettingsListeners) {
			listener.handleNewAppSettings(appSettings);
		}
	}

	/**
	 * @param appSettings2
	 * @throws IOException
	 */
	private void saveToPropertyFile(AppSettings pAppSettings)
			throws IOException {
		Properties props = new Properties();
		props.setProperty(PropertyAppSettings.SHOW_DESKTOP_NOTIFICATION, pAppSettings.getShowDesktopNotification()
				.toString());
		props.setProperty(PropertyAppSettings.START_AT_SYSTEM_STARTUP, pAppSettings.getStartAtSystemStartup()
				.toString());
		props.setProperty(PropertyAppSettings.USERNAME, pAppSettings.getUsername());
		props.setProperty(PropertyAppSettings.PASSWORD, pAppSettings.getPassword());
		props.setProperty(PropertyAppSettings.SERVER_URL, pAppSettings.getServerUrl());
		props.setProperty(PropertyAppSettings.WATCH_DIRECTORY, pAppSettings.getWatchDirectory());
		props.setProperty(PropertyAppSettings.MONITORING_ACTIVE, pAppSettings.getMonitoringActive().toString());
		props.setProperty(PropertyAppSettings.LANGUAGE, pAppSettings.getLanguage());
		props.setProperty(PropertyAppSettings.COUNTRY, pAppSettings.getCountry());
		props.setProperty(PropertyAppSettings.TEMP_DIRECTORY, pAppSettings.getTempDirectory());
		log.debug("Persisting AppSettings: " + props.toString());
		FileOutputStream fileOutputStream = new FileOutputStream(new File(
				APP_SETTINGS));
		props.store(fileOutputStream, "auto generated settings");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.application.AppSettingsDao#
	 * registerNewAppSettingsListener
	 * (com.wlami.mibox.client.application.NewAppSettingsListener)
	 */
	@Override
	public void registerNewAppSettingsListener(
			NewAppSettingsListener newAppSettingsListener) {
		appSettingsListeners.add(newAppSettingsListener);
	}

}