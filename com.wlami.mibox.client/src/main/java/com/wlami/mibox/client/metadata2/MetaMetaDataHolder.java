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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
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
public class MetaMetaDataHolder {

	/** this object handles the persistence of the meta meta data. */
	EncryptedMetaMetaDataRepository repository;

	private DecryptedMetaMetaData decryptedMetaMetaData;

	/**
	 * @return the decryptedMetaMetaData
	 */
	public DecryptedMetaMetaData getDecryptedMetaMetaData() {
		return decryptedMetaMetaData;
	}

	/**
	 * Default constructor.
	 * 
	 * @param repository
	 *            sets {@link #repository}
	 * @param appSettingsDao
	 *            sets {@link #appSettingsDao}
	 */
	public MetaMetaDataHolder(EncryptedMetaMetaDataRepository repository, AppSettingsDao appSettingsDao) {
		this.repository = repository;
		AppSettings appSettings = appSettingsDao.load();
		setupMetaMetaData(appSettings);
	}

	/** internal logger */
	public static final Logger log = LoggerFactory.getLogger(MetaMetaDataHolder.class);


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
	protected DecryptedMetaMetaData setupMetaMetaData(AppSettings appSettings) {
		if (decryptedMetaMetaData == null) {
			EncryptedMetaMetaData encryptedMetaMetaData = repository.retrieveMetaMetaData(appSettings);
			decryptedMetaMetaData = null;
			byte[] key = PBKDF2.getKeyFromPasswordAndSalt(appSettings.getPassword(), appSettings.getUsername());
			byte[] iv = HashUtil.calculateMD5Bytes(appSettings.getUsername().getBytes());
			if (encryptedMetaMetaData != null) {

				decryptedMetaMetaData = encryptedMetaMetaData.decrypt(key, iv);
				if (decryptedMetaMetaData == null) {
					// TODO This is a really big problem!! maybe ask the user
					// for
					// another password?
				}
			} else {
				decryptedMetaMetaData = new DecryptedMetaMetaData();
				decryptedMetaMetaData.setRoot(EncryptedMiTreeInformation.createRandom());
				String filename = appSettings.getUsername();
				encryptedMetaMetaData = decryptedMetaMetaData.encrypt(filename, key, iv);
				repository.persistMetaMetaData(encryptedMetaMetaData);
			}
		}
		return decryptedMetaMetaData;
	}

}
