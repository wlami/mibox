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
import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.networking.adapter.RestTransporter;
import com.wlami.mibox.client.networking.encryption.EncryptedChunk;
import com.wlami.mibox.client.networking.transporter.Transporter;

/**
 * This class implements the {@link TransportProvider} and uses only one Thread
 * to work off the uploads and downloads.
 * 
 * @author Wladislaw Mitzel
 * 
 */
@Named
public class TransportProviderSingleThread implements TransportProvider {

	/** internal logger. */
	Logger log = LoggerFactory.getLogger(getClass());

	/** reference to the {@link AppSettingsDao} for retrieving the appSettings */
	AppSettingsDao appSettingsDao;

	/** reference to our only working thread */
	TransportWorker<ChunkUploadRequest, EncryptedChunk> transportWorker;

	/** collection of {@link MChunk}s which shall be uploaded. */
	ConcurrentSkipListSet<ChunkUploadRequest> mChunkUploads;

	/** default constructor. */
	@Inject
	public TransportProviderSingleThread(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
		mChunkUploads = new ConcurrentSkipListSet<ChunkUploadRequest>();
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
					+ "rest/chunkmanager/";
			RestTransporter restTransporter = new RestTransporter(dataStoreUrl);
			Transporter transporter = new Transporter(restTransporter);
			transportWorker = new TransportWorker<>(transporter, mChunkUploads);
			transportWorker.start();
			log.info("Starting TransportProvider");
		} else {
			log.debug("TransportProvider already started.");
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
			log.info("Stopping TransportProvider");
		} else {
			log.warn("Stop impossible: TransportProvider is not working.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#
	 * addChunkUpload(com.wlami.mibox.client.metadata.MChunk,
	 * com.wlami.mibox.client.networking.synchronization.UploadCallback)
	 */
	@Override
	public void addChunkUpload(ChunkUploadRequest mChunkUpload) {
		if (!mChunkUploads.add(mChunkUpload)) {
			log.debug("Upload task not added. Alread existing.");
		}

	}
}
