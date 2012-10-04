/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2012 wladislaw
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
package com.wlami.mibox.client.metadata2;

import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.core.encryption.PBKDF2;
import com.wlami.mibox.core.util.HashUtil;

/**
 * This class is responsible for the initial setup of metametadata. It contains
 * information on all contained data in an account.
 * 
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public class MetaMetaDataSetup {

	/** this object handles the persistence of the meta meta data. */
	EncryptedMetaMetaDataRepository repository;

	/** internal logger */
	public static final Logger log = LoggerFactory
			.getLogger(MetaMetaDataSetup.class);

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(EncryptedMetaMetaDataRepository repository) {
		this.repository = repository;
	}

	/**
	 * This method sets up metadata for the first time. Therefore it first tries
	 * to retrieve MetaMetadata from the Server. If this is successful the
	 * provided metadata is decrypted and used. If there is no MetaMetaData on
	 * the server it gets created.
	 * 
	 * @param appSettings
	 *            Application settings containing the username.
	 * @return
	 */
	public DecryptedMetaMetaData setupMetaMetaData(AppSettings appSettings) {
		EncryptedMetaMetaData encryptedMetaMetaData = repository
				.retrieveMetaMetaData(appSettings);
		DecryptedMetaMetaData decryptedMetaMetaData = null;
		if (encryptedMetaMetaData != null) {
			byte[] key = PBKDF2.getKeyFromPasswordAndSalt(
					appSettings.getPassword(), appSettings.getUsername());
			byte[] iv = HashUtil.calculateMD5Bytes(appSettings.getUsername()
					.getBytes());
			try {
				decryptedMetaMetaData = encryptedMetaMetaData.decrypt(key, iv);
			} catch (CryptoException e) {
				log.error("Could not decrypt meta meta data!", e);
			} catch (IOException e) {
				log.error("IO exception during decryption of meta meta data!",
						e);
			}
			if (decryptedMetaMetaData == null) {
				// TODO This is a really big problem!! maybe ask the user for
				// another password?
			}
		} else {
			decryptedMetaMetaData = new DecryptedMetaMetaData();
		}
		return decryptedMetaMetaData;
	}



}
