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

import java.util.UUID;

import com.wlami.mibox.core.encryption.KeyGen;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public class EncryptedMiTreeInformation {

	private String fileName;

	private byte[] key;

	private byte[] iv;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the key
	 */
	public byte[] getKey() {
		return this.key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(byte[] key) {
		this.key = key;
	}

	/**
	 * @return the iv
	 */
	public byte[] getIv() {
		return this.iv;
	}

	/**
	 * @param iv
	 *            the iv to set
	 */
	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	/**
	 * Creates a new {@link EncryptedMiTreeInformation} with random key, iv and
	 * filename.
	 * 
	 * @return A new instance containing the random data.
	 */
	public static EncryptedMiTreeInformation createRandom() {
		EncryptedMiTreeInformation encryptedMiTreeInformation;
		encryptedMiTreeInformation = new EncryptedMiTreeInformation();
		KeyGen keyGen = new KeyGen();
		encryptedMiTreeInformation.setIv(keyGen.generateRandomBytes(16));
		encryptedMiTreeInformation.setKey(keyGen.generateRandomBytes(32));
		encryptedMiTreeInformation.setFileName(UUID.randomUUID().toString());
		return encryptedMiTreeInformation;
	}

}
