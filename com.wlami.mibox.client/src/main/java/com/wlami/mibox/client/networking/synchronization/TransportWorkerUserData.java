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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.networking.adapter.RestTransporter;
import com.wlami.mibox.client.networking.encryption.AesChunkEncryption;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;
import com.wlami.mibox.client.networking.encryption.EncryptedChunk;
import com.wlami.mibox.client.networking.transporter.Transporter;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class TransportWorkerUserData extends TransportWorker<MChunkUpload> {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(this.getClass());

	/** reference to a chunk encryption instance. */
	ChunkEncryption chunkEncryption = new AesChunkEncryption(); // TODO Inject a

	Transporter<EncryptedChunk> encryptedChunkTransporter;

	/**
	 * {@link AppSettingsDao} instance for retrieving the watchDirectory.
	 */
	AppSettingsDao appSettingsDao;

	/**
	 * default constructor.
	 * 
	 * @param appSettingsDao
	 *            instance of {@link AppSettingsDao} for retrieval of current
	 *            settings.
	 * @param uploads
	 *            reference to a {@link ConcurrentSkipListSet} with
	 *            {@link MChunk}s to be uploaded.
	 */
	public TransportWorkerUserData(AppSettingsDao appSettingsDao,
			ConcurrentSkipListSet<MChunkUpload> uploads) {
		this.uploads = uploads;
		this.appSettingsDao = appSettingsDao;
		AppSettings appSettings = appSettingsDao.load();
		String dataStoreUrl = appSettings.getServerUrl() + "rest/chunkmanager/";
		encryptedChunkTransporter = new Transporter<EncryptedChunk>(
				new RestTransporter(dataStoreUrl)); // TODO: mehrere Transporter
		// möglich
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.Transporter#
	 * encryptAndUploadChunk(com.wlami.mibox.client.metadata.MChunk)
	 */
	@Override
	public String encryptAndUploadChunk(MChunk chunk, File file)
			throws CryptoException,
			IOException {

		EncryptedChunk encryptedChunk = chunkEncryption.encryptChunk(chunk,
				file);
		encryptedChunkTransporter.upload(encryptedChunk);
		return encryptedChunk.getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.Transporter#
	 * downloadAndDecryptChunk(com.wlami.mibox.client.metadata.MChunk)
	 */
	@Override
	public byte[] downloadAndDecryptChunk(MChunk chunk) throws IOException,
	CryptoException {
		// // get appsetting to retrieve the current watch dir AND MORE
		// AppSettings appSettings = appSettingsDao.load();
		// WebResource webResource = getWebResource(appSettings,
		// chunk.getEncryptedChunkHash());
		// byte[] encryptedChunk = webResource.get(byte[].class);
		// return AesEncryption.decrypt(encryptedChunk,
		// chunk.getDecryptedChunkHash(), chunk.getPosition());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.Transporter#
	 * threadMainMethod()
	 */
	@Override
	public void threadMainMethod() {
		// Process the upload requests
		for (MChunkUpload mChunkUpload : uploads) {
			log.debug("Processing MChunkUpload for file "
					+ mChunkUpload.getFile().getName());
			try {
				MChunk chunk = mChunkUpload.getMChunk();
				File file = mChunkUpload.getFile();
				String result = encryptAndUploadChunk(chunk, file);
				mChunkUpload.getUploadCallback().uploadCallback(chunk, result);
				log.debug("Processing of MChunkUpload successfully.");
			} catch (CryptoException e) {
				log.error(
						"There has been en error while encrypting the chunk",
						e);
				// TODO tell the user about this problem.
			} catch (UniformInterfaceException | ClientHandlerException e) {
				log.warn("Error on putting chunk", e);
				// TODO tell the user about this problem.
			} catch (IOException e) {
				log.error(
						"There has been an error while reading the chunk", e);
				// TODO tell the user about this problem.
			}
			uploads.remove(mChunkUpload);
			// TODO implement "retry later" logic: add to retry collection
		}

		// TODO Process the download requests

		// Sleep before next iteration
		try {
			Thread.sleep(DEFAULT_SLEEP_TIME_MILLIS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
