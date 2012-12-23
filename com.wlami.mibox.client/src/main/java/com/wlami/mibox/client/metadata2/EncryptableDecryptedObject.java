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

/**
 * @author wladislaw
 * 
 */
public class EncryptableDecryptedObject<T extends DecryptedAbstractObject<U>, U extends EncryptedAbstractObject<T>> {

	EncryptedMiTreeInformation encryptedMiTreeInformation;

	T t;

	/**
	 * Default constructor
	 * 
	 * @param encryptedMiTreeInformation
	 */
	public EncryptableDecryptedObject(
			EncryptedMiTreeInformation encryptedMiTreeInformation, T t) {
		this.encryptedMiTreeInformation = encryptedMiTreeInformation;
		this.t = t;
	}

	public U encrypt() {
		return t.encrypt(encryptedMiTreeInformation.getFileName(),
				encryptedMiTreeInformation.getKey(),
				encryptedMiTreeInformation.getIv());
	}

	/**
	 * @return the encryptedMiTreeInformation
	 */
	public EncryptedMiTreeInformation getEncryptedMiTreeInformation() {
		return encryptedMiTreeInformation;
	}

	/**
	 * @return the decryptedMiTree
	 */
	public T getDecryptedMiTree() {
		return t;
	}

}
