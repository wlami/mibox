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

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.exception.CryptoRuntimeException;
import com.wlami.mibox.client.networking.transporter.Transportable;
import com.wlami.mibox.core.encryption.AesEncryption;

/**
 * @author wladislaw
 *
 */
public abstract class EncryptedAbstractObject<T extends DecryptedAbstractObject<?>>
implements Decryptable<Encryptable<?>>, Transportable {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(EncryptedAbstractObject.class);

	Class<T> clazz;

	/**
	 * 
	 */
	public EncryptedAbstractObject(Class<T> clazz) {
		this.clazz = clazz;
	}

	private byte[] content;

	/**
	 * @return the content
	 */
	@Override
	public byte[] getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	private String name;

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata2.Decryptable#decrypt(byte[], byte[])
	 */
	@Override
	public T decrypt(byte[] key, byte[] iv) {
		ObjectMapper objectMapper = new ObjectMapper();
		log.debug("decrypting MiTree");
		byte[] decrypted;
		try {
			decrypted = AesEncryption.crypt(false, content, iv, key);

			log.debug("Decryption done. Trying now to read the data.");
			return objectMapper.readValue(decrypted, clazz);
		} catch (InvalidCipherTextException | IOException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}
