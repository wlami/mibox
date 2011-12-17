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
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;

import com.wlami.mibox.client.application.PropertyAppSettings;
import com.wlami.mibox.client.application.MiboxClientApp;

/**
 * This Class represents the settings dialog.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public class SettingsShell extends Shell {

	/**
	 * Textfield for username.
	 */
	private Text txtUsername;

	/**
	 * Textfield for password.
	 */
	private Text txtPassword;

	/**
	 * ResourceBundle which stores all translated strings.
	 */
	private ResourceBundle strings;

	/**
	 * Bool which tells us whether there habe been made changes since the last
	 * save.
	 */
	private boolean unsavedChanges = false;

	/**
	 * Apply-Button.
	 */
	private Button btnApply;

	/**
	 * Cancel-Button.
	 */
	private Button btnCancel;

	/**
	 * Save-Button.
	 */
	private Button btnSave;

	/**
	 * Reference to the application settings.
	 */
	private PropertyAppSettings appSettings;

	/**
	 * Checkbutton for enabling desktop notifications.
	 */
	private Button btnShowDesktopNotification;

	/**
	 * Checkbutton for enabling automatic startup of mibox.
	 */
	private Button btnStartAtSystemStartup;

	/**
	 * Text-field for entering the synchronization path.
	 */
	private Text txtSyncDir;

	/**
	 * this button opens the "choose dir" dialog to choose the synchronization
	 * path.
	 */
	private Button btnChooseSyncDir;

	/**
	 * Getter for unsavedChanges.
	 * 
	 * @return unsaved changes value
	 */
	protected boolean isUnsavedChanges() {
		return unsavedChanges;
	}

	/**
	 * Setter for unsavedChanges. Modifies the state of apply-Button.
	 * 
	 * @param unsavedChanges
	 */
	protected void setUnsavedChanges(boolean unsavedChanges) {
		btnApply.setEnabled(unsavedChanges);
		this.unsavedChanges = unsavedChanges;
	}

	/**
	 * Create the shell. Auto-generated code.
	 * 
	 * @param display
	 * @throws IOException
	 */
	public SettingsShell(Display display) {
		super(display, SWT.CLOSE | SWT.TITLE);
		try {
			appSettings = PropertyAppSettings.readAppSettings(MiboxClientApp
					.getAppProperties().getProperty(PropertyAppSettings.APP_SETTINGS));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Shell shell = this;
		final PropertyAppSettings settings = appSettings;
		checkSaveBeforeClose();
		strings = LangUtils.getTranslationBundle();
		setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/icons/TrayLogo.png"));
		setLayout(new BorderLayout(0, 0));

		TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i64/applications-system.png"));
		tbtmNewItem.setText(strings.getString("Settings.tab.general"));

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_2);
		composite_2.setLayout(new FillLayout(SWT.VERTICAL));

		Group grpBehavior = new Group(composite_2, SWT.NONE);
		grpBehavior.setText(strings.getString("Settings.tab.general.behavior"));
		grpBehavior.setLayout(new GridLayout(2, false));
		new Label(grpBehavior, SWT.NONE);
		new Label(grpBehavior, SWT.NONE);
		new Label(grpBehavior, SWT.NONE);

		setupBtnShowDesktopNotification(settings, grpBehavior);
		btnShowDesktopNotification = new Button(grpBehavior, SWT.CHECK);
		btnShowDesktopNotification.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setUnsavedChanges(true);
				settings.setShowDesktopNotification(btnShowDesktopNotification
						.getSelection());
			}
		});
		btnShowDesktopNotification.setText(strings
				.getString("Settings.tab.general.show_desktop_notification"));
		new Label(grpBehavior, SWT.NONE);
		new Label(grpBehavior, SWT.NONE);

		setupBtnStartAtSystemStartup(settings, grpBehavior);
		new Label(grpBehavior, SWT.NONE);
		btnStartAtSystemStartup = new Button(grpBehavior, SWT.CHECK);
		btnStartAtSystemStartup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setUnsavedChanges(true);
				settings.setStartAtSystemStartup(btnStartAtSystemStartup
						.getSelection());
			}
		});
		btnStartAtSystemStartup.setToolTipText(strings
				.getString("com.wlami.mibox.not_implemented_yes"));
		btnStartAtSystemStartup.setEnabled(false);
		btnStartAtSystemStartup.setText(strings
				.getString("Settings.tab.general.start_at_system_startup"));

		Group grpSynchronization = new Group(composite_2, SWT.NONE);
		grpSynchronization.setText(strings
				.getString("Settings.tab.general.synchronization"));
		grpSynchronization.setLayout(new GridLayout(17, false));
		new Label(grpSynchronization, SWT.NONE); // TODO: Change this auto
													// generated code!!
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);
		new Label(grpSynchronization, SWT.NONE);

		Label lblSyncDir = new Label(grpSynchronization, SWT.NONE);
		lblSyncDir.setText(strings.getString("Settings.tab.general.sync_dir")
				+ ":");
		new Label(grpSynchronization, SWT.NONE);

		setupTxtSyncDir(grpSynchronization);
		final Text txtSyncDirectory = txtSyncDir;

		setupBtnChooseSyncDir(shell, grpSynchronization, txtSyncDirectory);

		TabItem tbtmAccount = new TabItem(tabFolder, SWT.NONE);
		tbtmAccount.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i64/im-user.png"));
		tbtmAccount.setText(strings.getString("Settings.tab.account"));

		Group grpAccountInformation = new Group(tabFolder, SWT.NONE);
		grpAccountInformation.setText(strings
				.getString("Settings.tab.account.account_information"));
		tbtmAccount.setControl(grpAccountInformation);
		GridLayout gl_grpAccountInformation = new GridLayout(4, false);
		grpAccountInformation.setLayout(gl_grpAccountInformation);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);

		setupTxtUsername(settings, grpAccountInformation);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);
		new Label(grpAccountInformation, SWT.NONE);

		setupTxtPassword(settings, grpAccountInformation);

		TabItem tbtmBandwith = new TabItem(tabFolder, SWT.NONE);
		tbtmBandwith.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i64/network-wired.png"));
		tbtmBandwith.setText(strings.getString("Settings.tab.network"));

		TabItem tbtmAdvanced = new TabItem(tabFolder, SWT.NONE);
		tbtmAdvanced.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i64/page-zoom.png"));
		tbtmAdvanced.setText(strings.getString("Settings.tab.advanced"));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		setupBtnSave(settings, composite, shell);

		setupBtnCancel(composite, shell);

		setupBtnApply(settings, composite);

		createContents();
		loadValuesFromSettings();
		setUnsavedChanges(false);
	}

	/**
	 * @param grpSynchronization
	 */
	private void setupTxtSyncDir(Group grpSynchronization) {
		txtSyncDir = new Text(grpSynchronization, SWT.BORDER);
		txtSyncDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				setUnsavedChanges(true);
				appSettings.setWatchDirectory(txtSyncDir.getText());
			}
		});
		txtSyncDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 13, 1));
	}

	/**
	 * @param shell
	 * @param grpSynchronization
	 * @param txtSyncDirectory
	 */
	private void setupBtnChooseSyncDir(final Shell shell,
			Group grpSynchronization, final Text txtSyncDirectory) {
		btnChooseSyncDir = new Button(grpSynchronization, SWT.NONE);
		btnChooseSyncDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				DirectoryDialog directoryDialog = new DirectoryDialog(shell);
				directoryDialog.setFilterPath(txtSyncDirectory.getText());
				directoryDialog.setText(strings
						.getString("Settings.tab.general.choose_dir_dialog"));
				directoryDialog.setMessage(strings
						.getString("Settings.tab.general.choose_dir_dialog"));
				String tempResult = directoryDialog.open();
				if (tempResult != null) {
					txtSyncDirectory.setText(tempResult);
				}
			}
		});
		btnChooseSyncDir.setText(strings
				.getString("Settings.tab.general.choose_dir"));
	}

	/**
	 * @param settings
	 * @param grpBehavior
	 */
	private void setupBtnShowDesktopNotification(final PropertyAppSettings settings,
			Group grpBehavior) {
	}

	/**
	 * @param settings
	 * @param grpBehavior
	 */
	private void setupBtnStartAtSystemStartup(final PropertyAppSettings settings,
			Group grpBehavior) {
	}

	/**
	 * @param settings
	 * @param grpAccountInformation
	 */
	private void setupTxtUsername(final PropertyAppSettings settings,
			Group grpAccountInformation) {
		Label lblUsername = new Label(grpAccountInformation, SWT.NONE);
		lblUsername.setText(strings.getString("Settings.tab.account.username")
				+ ":");
		new Label(grpAccountInformation, SWT.NONE);

		txtUsername = new Text(grpAccountInformation, SWT.BORDER);
		txtUsername.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				setUnsavedChanges(true);
				settings.setUsername(txtUsername.getText());
			}
		});
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
	}

	/**
	 * @param settings
	 * @param grpAccountInformation
	 */
	private void setupTxtPassword(final PropertyAppSettings settings,
			Group grpAccountInformation) {
		Label lblPassword = new Label(grpAccountInformation, SWT.NONE);
		lblPassword.setText(strings.getString("Settings.tab.account.password")
				+ ":");
		new Label(grpAccountInformation, SWT.NONE);

		txtPassword = new Text(grpAccountInformation, SWT.BORDER | SWT.PASSWORD);
		txtPassword.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				setUnsavedChanges(true);
				settings.setPassword(txtPassword.getText());
			}
		});
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
	}

	/**
	 * @param settings
	 * @param composite
	 * @param shell
	 */
	private void setupBtnSave(final PropertyAppSettings settings, Composite composite,
			final Shell shell) {
		btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					PropertyAppSettings.writeAppSettings(
							settings,
							MiboxClientApp.getAppProperties().getProperty(
									PropertyAppSettings.APP_SETTINGS));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setUnsavedChanges(false);
				shell.close();
			}
		});
		btnSave.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i32/dialog-ok.png"));
		btnSave.setText(strings.getString("Settings.buttons.save"));
	}

	/**
	 * @param composite
	 * @param shell
	 */
	private void setupBtnCancel(Composite composite, final Shell shell) {
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i32/dialog-cancel.png"));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		btnCancel.setText(strings.getString("Settings.buttons.cancel"));
	}

	/**
	 * @param settings
	 * @param composite
	 */
	private void setupBtnApply(final PropertyAppSettings settings, Composite composite) {
		btnApply = new Button(composite, SWT.NONE);
		btnApply.setEnabled(false);
		btnApply.setImage(SWTResourceManager.getImage(SettingsShell.class,
				"/i32/dialog-ok-apply.png"));
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					PropertyAppSettings.writeAppSettings(
							settings,
							MiboxClientApp.getAppProperties().getProperty(
									PropertyAppSettings.APP_SETTINGS));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setUnsavedChanges(false);
			}
		});
		btnApply.setText(strings.getString("Settings.buttons.apply"));
	}

	/**
	 * fill the UI with values from appSettings
	 */
	private void loadValuesFromSettings() {
		btnShowDesktopNotification.setSelection(appSettings
				.isShowDesktopNotification());
		btnStartAtSystemStartup.setSelection(appSettings
				.isStartAtSystemStartup());
		txtUsername.setText(appSettings.getUsername());
		txtPassword.setText(appSettings.getPassword());
		txtSyncDir.setText(appSettings.getWatchDirectory());
	}

	/**
	 * Checks whether there are changes in onClose listener. Asks whether user
	 * really wants to quit the settings dialog.
	 */
	private void checkSaveBeforeClose() {
		final SettingsShell shell = this;
		this.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				if (shell.isUnsavedChanges()) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(shell, style);
					messageBox.setText(strings
							.getString("Settings.close.title"));
					messageBox.setMessage(strings
							.getString("Settings.close.message"));
					event.doit = messageBox.open() == SWT.YES;
				}

			}
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText(strings.getString("com.wlami.mibox.product_name")
				+ strings.getString("Settings.title"));
		setSize(552, 425);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
