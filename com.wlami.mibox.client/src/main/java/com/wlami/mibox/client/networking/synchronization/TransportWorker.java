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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.wlami.mibox.client.metadata.MetadataWorker;
import com.wlami.mibox.client.networking.transporter.Transportable;
import com.wlami.mibox.client.networking.transporter.Transporter;

/**
 * This interface describes classes which can be used to transport encrypted
 * data to the server and from it.
 */
public class TransportWorker<T extends UploadRequest<?>, E extends Transportable>
extends Thread {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(this.getClass());

	/** if set to false this thread stops. */
	private boolean active = true;

	/** Time period between checking the buffers. */
	protected static final long DEFAULT_SLEEP_TIME_MILLIS = 250L;

	/**
	 * set of upload tasks.
	 */
	protected ConcurrentSkipListSet<T> uploads;

	/**
	 * set of download tasks.
	 */
	protected ConcurrentSkipListSet<DownloadRequest> downloads;

	/** stops the processing. */
	public void stopProcessing() {
		this.active = false;
	}

	/** a reference to a transporter which sends and receives the data */
	Transporter transporter;

	/**
	 * 
	 * @param transporter
	 */
	public TransportWorker(Transporter transporter,
			ConcurrentSkipListSet<T> uploads,
			ConcurrentSkipListSet<DownloadRequest> downloads) {
		this.transporter = transporter;
		this.uploads = uploads;
		this.downloads = downloads;
	}

	/* (non-Javadoc) @see java.lang.Thread#run() */
	@Override
	public final void run() {
		this.log.info("Start Transporter");
		while (this.active) {
			// Process the upload requests
			for (UploadRequest<?> uploadRequest : uploads) {
				log.debug("Processing UploadRequest for file ");
				try {
					Transportable transportable = uploadRequest
							.getTransportable();
					transporter.upload(transportable);
					String encryptedChunkHash = transportable.getName();
					Map<String,Object> params = new HashMap<>();
					params.put(MetadataWorker.CALLBACK_PARAM_ENCRYPTED_CHUNK_HASH, encryptedChunkHash);
					uploadRequest.getUploadCallback().transportCallback(params);
					// String result = encryptAndUploadChunk(chunk, file);
					// mChunkUpload.getUploadCallback().uploadCallback(chunk,
					// result);
					log.debug("Processing of UploadRequest successfully.");
				} catch (CryptoException e) {
					log.error(
							"There has been en error while encrypting the chunk",
							e);
					// TODO tell the user about this problem.
				} catch (UniformInterfaceException | ClientHandlerException e) {
					log.warn("Error on putting chunk", e);
					// TODO tell the user about this problem.
				} catch (IOException e) {
					log.error("There has been an error while reading the data",
							e);
					// TODO tell the user about this problem.
				} catch (Exception e) {
					log.error(
							"There has been an error during the processing of an upload request",
							e);
				}
				uploads.remove(uploadRequest);
				// TODO implement "retry later" logic: add to retry collection
			}

			for (DownloadRequest downloadRequest : downloads) {
				log.debug("Processing downloadRequest for resource [{}]", downloadRequest.getTransportInfo());
				byte[] data = transporter.download(downloadRequest.getTransportInfo());
				downloadRequest.getTransportCallback().transportCallback(null); //TODO FIXME complete this
			}
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME_MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.log.info("Stopped Transporter");
	}

}

