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
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;

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

import com.wlami.mibox.client.application.AppSettingsDao;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class MiboxTray {

	/**
	 * Display object to enable access to the tray.
	 */
	private Display display;

	/**
	 * Shell object to enable access to the tray.
	 */
	private Shell shell;

	/**
	 * Tray object for showing our trayItem.
	 */
	private Tray tray;

	/**
	 * TrayItem which is shown in the tray.
	 */
	private TrayItem trayItem;

	/**
	 * Right click menu of the trayItem.
	 */
	private Menu menu;

	/**
	 * Ressource bundle which contains all UI strings.
	 */
	private ResourceBundle strings;

	/**
	 * Reference to singleton langUtils.
	 */
	LangUtils langUtils;

	AppSettingsDao appSettingsDao;

	/**
	 * Public constructor to create a new MiboxTray. It loads the translated
	 * strings during creation.
	 */
	@Inject
	public MiboxTray(final LangUtils langUtils, AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
		this.langUtils = langUtils;
		new Thread() {
			public void run() {
				strings = langUtils.getTranslationBundle();
				setupTray();
				show();
			}
		}.start();
	}

	/**
	 * Configures the TrayItem, so that it is shown in the tray.
	 */
	protected void setupTray() {
		display = new Display();
		shell = new Shell(display);
		tray = display.getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		Image image = new Image(display, "res/img/TrayLogo.png");
		trayItem.setImage(image);
		trayItem.setToolTipText(strings
				.getString("com.wlami.mibox.product_name")
				+ strings.getString("Tray.tooltiptext"));
		createMenu();
	}

	/**
	 * Create the right click menu for the trayItem.
	 */
	private void createMenu() {
		menu = new Menu(shell, SWT.POP_UP);
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(final Event arg0) {
				menu.setVisible(true);
			}
		});
		createSettingsItem();
		createCloseItem();

	}

	/**
	 * Create the settings menuItem for the right click menu.
	 */
	private void createSettingsItem() {
		final MenuItem menuItemSettings = new MenuItem(menu, SWT.PUSH);
		menuItemSettings.setText(strings.getString("Settings.title"));
		menuItemSettings.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event arg0) {
				SettingsShell settingsShell;
				try {
					settingsShell = SettingsShellFactory.getSettingsShell(
							langUtils, appSettingsDao);
					while (!settingsShell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
					SettingsShellFactory.invalidateShell();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the closeItem for the right click menu.
	 */
	private void createCloseItem() {
		final MenuItem menuItemClose = new MenuItem(menu, SWT.PUSH);
		menuItemClose.setText(strings.getString("Tray.menu.shutdown_client"));
		menuItemClose.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event arg0) {
				System.exit(0);
			}
		});
	}

	/**
	 * display the trayItem.
	 */
	protected void show() {
		trayItem.setVisible(true);
		while (true) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
