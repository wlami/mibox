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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.encryption.AesEncryption;

/**
 * @author wladislaw
 *
 */
public class DecryptedAbstractObject<T extends EncryptedAbstractObject<?>>
implements Encryptable<Decryptable<?>> {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(DecryptedAbstractObject.class);

	/** This class is returned from {@link #encrypt(String, byte[], byte[])} */
	Class<T> clazz;

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}
	/**
	 * Default constructor.
	 */
	public DecryptedAbstractObject(Class<T> clazz) {
		this.clazz = clazz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.metadata2.Encryptable#encrypt(java.lang.String,
	 * byte[], byte[])
	 */
	@Override
	public T encrypt(String filename, byte[] key, byte[] iv) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			byte[] data = objectMapper.writeValueAsBytes(this);
			byte[] encrypted = AesEncryption.crypt(true, data, iv, key);
			T newObject = clazz
					.newInstance();
			newObject.setContent(encrypted);
			newObject.setName(filename);
			return newObject;
		} catch (IOException | CryptoException | InstantiationException
				| IllegalAccessException e) {
			log.error("Error during encryption of MiTree", e);
			return null;
		}
	}
}
