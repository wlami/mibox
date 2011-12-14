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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.wlami.mibox.client.gui.MiboxTray;

/**
 * Main class for the MiboxClient. Serves as the coupling point between the GUI
 * and the background service.
 * 
 * @author Wladislaw Mitzel
 */
public final class MiboxClientApp {

	/**
	 * hide constructor, because this is an utility class.
	 */
	private MiboxClientApp() {
	}

	/**
	 * Constant for accessing the main properties file.
	 */
	protected static final String RES_MAIN_PROPERTIES = "res/main.properties";

	/**
	 * Properties variable of the main properties file.
	 */
	private static Properties appProperties;

	/**
	 * Main entry point for the MiboxClientApplication.
	 * 
	 * @param args
	 *            no command line arguments needed.
	 * @throws IOException
	 *             thrown, if app properties cannot be loaded.
	 */
	public static void main(final String[] args) throws IOException {
		loadAppProperties(); // TODO: Handle exception and show errorDialog
		MiboxTray miboxTray = new MiboxTray();
		miboxTray.show();
	}

	/**
	 * Loads the main properties file.
	 * 
	 * @throws IOException
	 *             thrown, if there is an error while reading the properties.
	 */
	private static void loadAppProperties() throws IOException {
		appProperties = new Properties();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(RES_MAIN_PROPERTIES));
		appProperties.load(bufferedInputStream);
		bufferedInputStream.close();
	}

	/**
	 * @return the appProperties
	 */
	public static Properties getAppProperties() {
		return appProperties;
	}

}
