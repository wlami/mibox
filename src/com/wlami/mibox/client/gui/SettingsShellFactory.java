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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;

import com.wlami.mibox.client.application.AppSettingsDao;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class SettingsShellFactory {

	static SettingsShell settingsShell;

	/**
	 * Gets an instance of the settingsShell.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static SettingsShell getSettingsShell(LangUtils langUtils,
			AppSettingsDao appSettingsDao) throws FileNotFoundException,
			IOException {
		if (settingsShell == null) {
			Display display = Display.getCurrent();
			settingsShell = new SettingsShell(display, langUtils,
					appSettingsDao);
			settingsShell.open();
			settingsShell.layout();
		}
		return settingsShell;
	}

	/**
	 * Disposes the Settings shell. Call after the shell has been closed.
	 */
	public static void invalidateShell() {
		settingsShell = null;
	}
}
