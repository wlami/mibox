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
package com.wlami.mibox.client.gui;

import java.util.Locale;
import java.util.ResourceBundle;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;

/**
 * @author Wladislaw Mitzel
 * 
 */
public final class LangUtils {

	/**
	 * Reference to the application settings.
	 */

	AppSettingsDao appSettingsDao;

	/**
	 * 
	 */
	public LangUtils(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}

	/**
	 * method for acquiring a Strings ResourceBundle. It contains all Strings
	 * for the graphical user interface.
	 * 
	 * @return ResourceBundle with all translations.
	 */
	public ResourceBundle getTranslationBundle() {
		Locale currentLocale;
		String language = null;
		String country = null;
		AppSettings appSettings;
		appSettings = appSettingsDao.load();
		language = appSettings.getLanguage();
		country = appSettings.getCountry();
		if (language == null) {
			currentLocale = new Locale("en", "US");
		} else {
			currentLocale = new Locale(language, country);
		}
		return ResourceBundle.getBundle("strings", currentLocale);
	}

}
