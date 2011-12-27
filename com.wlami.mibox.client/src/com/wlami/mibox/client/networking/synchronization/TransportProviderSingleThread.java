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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata.MChunk;

/**
 * This class implements the {@link TransportProvider} and uses only one Thread
 * to work off the uploads and downloads.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public class TransportProviderSingleThread implements TransportProvider {

	/** internal logger. */
	Logger log = LoggerFactory.getLogger(getClass());

	/** reference to the {@link AppSettingsDao} for retrieving the appSettings */
	AppSettingsDao appSettingsDao;

	/** reference to our only working thread */
	Transporter transporter;

	/** collection of {@link MChunk}s which shall be uploaded. */
	ConcurrentSkipListSet<MChunkUpload> mChunkUploads;

	/** default constructor. */
	@Inject
	public TransportProviderSingleThread(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
		mChunkUploads = new ConcurrentSkipListSet<MChunkUpload>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.TransportProvider#
	 * startProcessing()
	 */
	@Override
	public void startProcessing() {
		if (transporter == null) {
			transporter = new UserDataChunkTransporter(appSettingsDao,
					mChunkUploads);
			transporter.start();
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
		if (transporter != null) {
			transporter.stopProcessing();
			try {
				transporter.join();
			} catch (InterruptedException e) {
			}
			transporter = null;
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
	public void addChunkUpload(MChunkUpload mChunkUpload) {
		if (mChunkUploads.add(mChunkUpload)) {
			log.debug("Upload task not added. Alread existing.");
		}

	}
}
