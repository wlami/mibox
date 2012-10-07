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
import com.wlami.mibox.client.networking.encryption.DataChunk;
import com.wlami.mibox.client.networking.transporter.Transporter;

/**
 * This class implements the {@link TransportProvider} and uses only one Thread
 * to work off the uploads and downloads.
 * 
 * @author Wladislaw Mitzel
 * 
 */
@Named(value = "chunkTransport")
public class ChunkTransportProviderSingleThread implements
TransportProvider<ChunkUploadRequest> {

	/** internal logger. */
	Logger log = LoggerFactory.getLogger(getClass());

	/** reference to the {@link AppSettingsDao} for retrieving the appSettings */
	AppSettingsDao appSettingsDao;

	/** reference to our only working thread */
	TransportWorker<ChunkUploadRequest, DataChunk> transportWorker;

	/** collection of {@link MChunk}s which shall be uploaded. */
	ConcurrentSkipListSet<ChunkUploadRequest> mChunkUploads;

	ConcurrentSkipListSet<DownloadRequest> downloadRequests;

	/** default constructor. */
	@Inject
	public ChunkTransportProviderSingleThread(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
		mChunkUploads = new ConcurrentSkipListSet<ChunkUploadRequest>();
		downloadRequests = new ConcurrentSkipListSet<DownloadRequest>();
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
			RestTransporter restTransporter = new RestTransporter(dataStoreUrl,
					appSettings.getUsername(), appSettings.getPassword());
			Transporter transporter = new Transporter(restTransporter);
			transportWorker = new TransportWorker<>(transporter, mChunkUploads, downloadRequests);
			transportWorker.start();
			log.info("Starting ChunkTransportProviderSingleThread");
		} else {
			log.debug("ChunkTransportProviderSingleThread already started.");
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
			log.info("Stopping ChunkTransportProviderSingleThread");
		} else {
			log.warn("Stop impossible: ChunkTransportProviderSingleThread is not working.");
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
	public void addChunkUpload(ChunkUploadRequest mChunkUpload) {
		if (!mChunkUploads.add(mChunkUpload)) {
			log.debug("Upload task not added. Already existing.");
		}

	}
	
	/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#addDownload(com.wlami.mibox.client.networking.synchronization.DownloadRequest)
	 */
	@Override
	public void addDownload(DownloadRequest downloadRequest) {
		if (!downloadRequests.add(downloadRequest)) {
			log.debug("Download request not added. Already existing.");
		}
	}
	
/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#addDownloadContainer(com.wlami.mibox.client.networking.synchronization.RequestContainer)
	 */
	@Override
	public void addDownloadContainer(
			RequestContainer downloadRequestContainer) {
		downloadRequests.addAll(downloadRequestContainer.getDownloadRequests());
	}
}
