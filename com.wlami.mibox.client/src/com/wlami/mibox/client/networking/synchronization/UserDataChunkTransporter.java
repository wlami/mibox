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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.core.encryption.AesEncryption;
import com.wlami.mibox.core.util.HashUtil;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class UserDataChunkTransporter implements Transporter {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * {@link AppSettingsDao} instance for retrieving the watchDirectory.
	 */
	AppSettingsDao appSettingsDao;

	/** default constructor. */
	@Inject
	public UserDataChunkTransporter(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.Transporter#
	 * encryptAndUploadChunk(com.wlami.mibox.client.metadata.MChunk)
	 */
	@Override
	public String encryptAndUploadChunk(MChunk chunk) throws CryptoException,
			IOException {
		// get appsetting to retrieve the current watch dir
		AppSettings appSettings;
		appSettings = appSettingsDao.load();
		// read the chunk
		File file = chunk.getMFile().getFile(appSettings);
		FileInputStream fis = new FileInputStream(file);
		int chunkSize = chunk.getMFile().getChunkSize();
		// encrypt it
		byte[] plainChunkData = new byte[chunkSize];
		fis.read(plainChunkData, chunk.getPosition() * chunkSize, chunkSize);
		byte[] encryptedChunkData = AesEncryption.encrypt(plainChunkData,
				chunk.getDecryptedChunkHash(), chunk.getPosition());
		// calculate the encrypted hash
		String encryptedHash = HashUtil.calculateSha256(encryptedChunkData);
		// upload it
		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.addFilter(new HTTPBasicAuthFilter("user", "user"));
		URI uri = null;
		try {
			uri = UriBuilder.fromUri(
					appSettings.getServerUrl() + "rest/chunkmanager/"
							+ encryptedHash).build();
		} catch (IllegalArgumentException e) {
			log.error("", e);
		}
		WebResource webResource = client.resource(uri);
		webResource.put(encryptedChunkData);
		return encryptedHash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.Transporter#
	 * downloadAndDecryptChunk(com.wlami.mibox.client.metadata.MChunk)
	 */
	@Override
	public byte[] downloadAndDecryptChunk(MChunk chunk) {
		// TODO Auto-generated method stub
		return null;
	}

}
