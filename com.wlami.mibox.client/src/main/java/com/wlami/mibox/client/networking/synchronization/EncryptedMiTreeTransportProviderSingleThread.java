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
package com.wlami.mibox.client.networking.synchronization;

import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.networking.adapter.RestTransporter;
import com.wlami.mibox.client.networking.transporter.Transporter;

/**
 * This class implements the {@link TransportProvider} and uses only one Thread
 * to work off the uploads and downloads.
 * 
 * @author Wladislaw Mitzel
 * 
 */
@Named(value = "metaTransport")
public class EncryptedMiTreeTransportProviderSingleThread implements
TransportProvider<EncryptedMiTreeUploadRequest> {

	/**
	 * 
	 */
	protected static final String SERVER_URL_SUFFIX_REST_INTERFACE = "rest/metadatamanager/";

	/** internal logger. */
	Logger log = LoggerFactory
			.getLogger(EncryptedMiTreeTransportProviderSingleThread.class);

	/** reference to the {@link AppSettingsDao} for retrieving the appSettings */
	AppSettingsDao appSettingsDao;

	/** reference to our only working thread */
	TransportWorker<EncryptedMiTreeUploadRequest, EncryptedMiTree> transportWorker;

	/**
	 * collection of {@link EncryptedMiTreeUploadRequest}s which shall be
	 * uploaded.
	 */
	ConcurrentSkipListSet<EncryptedMiTreeUploadRequest> uploads;

	ConcurrentSkipListSet<DownloadRequest> downloads;

	/** default constructor. */
	@Inject
	public EncryptedMiTreeTransportProviderSingleThread(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
		uploads = new ConcurrentSkipListSet<EncryptedMiTreeUploadRequest>();
		downloads = new ConcurrentSkipListSet<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#
	 * startProcessing()
	 */
	@Override
	public void startProcessing() {
		if (transportWorker == null) {
			AppSettings appSettings = appSettingsDao.load();
			String dataStoreUrl = appSettings.getServerUrl()
					+ SERVER_URL_SUFFIX_REST_INTERFACE;
			RestTransporter restTransporter = new RestTransporter(dataStoreUrl,
					appSettings.getUsername(), appSettings.getPassword());
			Transporter transporter = new Transporter(restTransporter);
			transportWorker = new TransportWorker<>(transporter, uploads, downloads);
			transportWorker.start();
			log.info("Starting EncryptedMiTreeTransportProviderSingleThread");
		} else {
			log.debug("EncryptedMiTreeTransportProviderSingleThread already started.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#
	 * stopProcessing()
	 */
	@Override
	public void stopProcessing() {
		if (transportWorker != null) {
			transportWorker.stopProcessing();
			try {
				transportWorker.join();
			} catch (InterruptedException e) {
			}
			transportWorker = null;
			log.info("Stopping EncryptedMiTreeTransportProviderSingleThread");
		} else {
			log.warn("Stop impossible: EncryptedMiTreeTransportProviderSingleThread is not working.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#
	 * addChunkUpload(com.wlami.mibox.client.metadata.MChunk,
	 * com.wlami.mibox.client.networking.synchronization.TransportCallback)
	 */
	@Override
	public void addChunkUpload(EncryptedMiTreeUploadRequest mChunkUpload) {
		if (!uploads.add(mChunkUpload)) {
			log.debug("Upload task not added. Alread existing.");
		}

	}
}
