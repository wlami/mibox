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

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.application.AppSettingsDaoProperty;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class SettingsShellTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {

		AppSettingsDao appSettingsDao = new AppSettingsDaoProperty();
		Shell sh = new SettingsShell(Display.getCurrent(), new LangUtils(
				appSettingsDao), appSettingsDao);
		sh.open();
		sh.layout();
		while (!sh.isDisposed()) {
			if (!sh.getDisplay().readAndDispatch()) {
				sh.getDisplay().sleep();
			}
		}
	}

}
