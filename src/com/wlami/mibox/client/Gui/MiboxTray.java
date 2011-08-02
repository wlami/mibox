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
package com.wlami.mibox.client.Gui;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MiboxTray {
	
	private Display display;
	private Shell shell;
	private Tray tray;
	private TrayItem trayItem;
	private Menu menu;
	private ResourceBundle strings;
	

	public MiboxTray() {
		strings = LangUtils.getTranslationBundle();
		setupTray();
	}
	
	protected void setupTray() {
		display = new Display();
		shell = new Shell(display);
		tray = display.getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		Image image = new Image(display, "res/img/TrayLogo.png");
		trayItem.setImage(image);
		trayItem.setToolTipText(strings.getString("com.wlami.mibox.product_name") + strings.getString("Tray.tooltiptext"));
		createMenu();
	}
	
	private void createMenu() {
		menu = new Menu(shell, SWT.POP_UP);
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event arg0) {
				menu.setVisible(true);
			}
		});
		createSettingsItem();
		createCloseItem();
		
	}
	
	private void createSettingsItem() {
		final MenuItem menuItemSettings = new MenuItem(menu, SWT.PUSH);
		menuItemSettings.setText(strings.getString("Settings.title"));
		menuItemSettings.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				SettingsShell settingsShell = SettingsShellFactory.getSettingsShell();
				while (!settingsShell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				SettingsShellFactory.invalidateShell();
			}
		});
	}

	private void createCloseItem() {
		final MenuItem menuItemClose = new MenuItem(menu, SWT.PUSH);
		menuItemClose.setText(strings.getString("Tray.menu.shutdown_client"));
		menuItemClose.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				System.exit(0);
			}
		});
	}

	public void show() {
		trayItem.setVisible(true);
		while (true) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
