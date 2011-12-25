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

import java.io.IOException;

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
	 * Constant for accessing monitoringActive
	 */
	protected static final String MONITORING_ACTIVE = "monitoring.active";

	/**
	 * indicates whether monitoring is switched on now.
	 */
	private Boolean monitoringActive;

	/**
	 * default constructor. calls the load-method
	 * 
	 * @throws IOException
	 */
	public PropertyAppSettings() {
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

	/**
	 * @return the monitoringActive
	 */
	public Boolean getMonitoringActive() {
		return monitoringActive;
	}

	/**
	 * @param monitoringActive
	 *            the monitoringActive to set
	 */
	public void setMonitoringActive(boolean monitoringActive) {
		this.monitoringActive = monitoringActive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AppSettings clone() {
		try {
			return (AppSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			// This shouldn't be possible!
			e.printStackTrace();
		}
		return null;
	}

	// TODO: if app settings are not available then persist default settings

}
