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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wlami.mibox.client.application.AppSettings;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Singleton
@Named
public final class LangUtils {

	/**
	 * Reference to the application settings.
	 */

	AppSettings appSettings;

	/**
	 * 
	 */
	@Inject
	public LangUtils(AppSettings appSettings) {
		this.appSettings = appSettings;
	}

	/**
	 * method for acquiring a Strings ResourceBundle. It contains all Strings
	 * for the graphical user interface.
	 * 
	 * @return ResourceBundle with all translations.
	 */
	public ResourceBundle getTranslationBundle() {
		Locale currentLocale;
		String language = appSettings.getLanguage();
		String country = appSettings.getCountry();
		if (language == null) {
			currentLocale = new Locale("en", "US");
		} else {
			currentLocale = new Locale(language, country);
		}
		return ResourceBundle.getBundle("strings", currentLocale);
	}

}
