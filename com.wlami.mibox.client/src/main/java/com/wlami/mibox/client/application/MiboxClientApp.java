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
import java.security.Security;

import javax.inject.Inject;
import javax.inject.Named;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wlami.mibox.client.backend.watchdog.DirectoryWatchdog;
import com.wlami.mibox.client.gui.MiboxTray;
import com.wlami.mibox.client.metadata.MetadataRepository;
import com.wlami.mibox.client.networking.synchronization.ChunkUploadRequest;
import com.wlami.mibox.client.networking.synchronization.EncryptedMiTreeUploadRequest;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;

/**
 * Main class for the MiboxClient. Serves as the coupling point between the GUI
 * and the background service.
 * 
 * @author Wladislaw Mitzel
 */
@Named
public final class MiboxClientApp {

	/**
	 * internal logging object.
	 */
	protected static Logger log = LoggerFactory.getLogger(MiboxClientApp.class
			.getName());

	private final ClassPathXmlApplicationContext ctx;

	/**
	 * hide constructor, because this is an utility class.
	 */
	@Inject
	private MiboxClientApp(ClassPathXmlApplicationContext ctx) {
		this.ctx = ctx;
	}

	private MiboxTray miboxTray;

	/**
	 * @param miboxTray
	 *            the miboxTray to set
	 */
	public void setMiboxTray(MiboxTray miboxTray) {
		this.miboxTray = miboxTray;
	}

	MetadataRepository metadataRepository;

	/**
	 * @param metadataRepository
	 *            the metadataRepository to set
	 */
	public void setMetadataRepository(MetadataRepository metadataRepository) {
		this.metadataRepository = metadataRepository;
	}

	private AppSettingsDao appSettingsDao;

	/**
	 * @param appSettingsDao
	 *            the appSettingsDao to set
	 */
	public void setAppSettingsDao(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}

	private TransportProvider<ChunkUploadRequest> chunkTransportProvider;

	/**
	 * @param chunkTransportProvider
	 *            the chunkTransportProvider to set
	 */
	public void setChunkTransportProvider(
			TransportProvider<ChunkUploadRequest> chunkTransportProvider) {
		this.chunkTransportProvider = chunkTransportProvider;
	}

	TransportProvider<EncryptedMiTreeUploadRequest> encryptedMiTreeTransportProvider;

	/**
	 * @param encryptedMiTreeTransportProvider
	 *            the encryptedMiTreeTransportProvider to set
	 */
	public void setEncryptedMiTreeTransportProvider(
			TransportProvider<EncryptedMiTreeUploadRequest> encryptedMiTreeTransportProvider) {
		this.encryptedMiTreeTransportProvider = encryptedMiTreeTransportProvider;
	}

	DirectoryWatchdog directoryWatchdog;

	/**
	 * @param directoryWatchdog
	 *            the directoryWatchdog to set
	 */
	public void setDirectoryWatchdog(DirectoryWatchdog directoryWatchdog) {
		this.directoryWatchdog = directoryWatchdog;
	}

	/**
	 * Main entry point for the MiboxClientApplication.
	 * 
	 * @param args
	 *            no command line arguments needed.
	 * @throws IOException
	 *             thrown, if app properties cannot be loaded.
	 */
	public static void main(final String[] args) throws IOException {
		log.info("Startup mibox client.");
		Security.addProvider(new BouncyCastleProvider());
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring.xml");
		MiboxClientApp miboxClientApp = ctx.getBean("miboxClientApp",
				MiboxClientApp.class);
		miboxClientApp.run();
	}

	private void run() {

		// start the metadatarepo
		metadataRepository.startProcessing();

		// start TransportProvider
		chunkTransportProvider.startProcessing();
		encryptedMiTreeTransportProvider.startProcessing();

		// start the watchdog
		startWatchdog();
	}

	/**
	 * 
	 */
	protected void startWatchdog() {
		AppSettings appSettings = appSettingsDao.load();
		directoryWatchdog.setDirectory(appSettings.getWatchDirectory());
		directoryWatchdog.setActive(appSettings.getMonitoringActive());
		directoryWatchdog.start();
	}
}
