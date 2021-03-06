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


/**
 * @author Wladislaw Mitzel
 * 
 */
public interface AppSettingsDao {

	/**
	 * loads the current available application settings.
	 * 
	 * @return current Settings
	 */
	public AppSettings load();

	/**
	 * persists the given Settings.
	 * 
	 * @param appSettings
	 *            settings to persist.
	 */
	public void save(AppSettings appSettings);

	/**
	 * allows to register a NewAppSettingsListener which gets notified as soon
	 * as a new AppSetting is available.
	 * 
	 * @param newAppSettingsListener
	 */
	public void registerNewAppSettingsListener(
			NewAppSettingsListener newAppSettingsListener);
}
