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
 * This class encapsulates an encryptable object with its encryption
 * information.
 * 
 * @author wladislaw mitzel
 * @author stefan baust.
 * 
 */
public class EncryptableDecryptedObject<T extends DecryptedAbstractObject<U>, U extends EncryptedAbstractObject<T>> {

	/** Information for the encryption of the object. */
	EncryptedMetadataInformation encryptedMiTreeInformation;

	/** The actual object which shall be encrypted. */
	T encryptableObject;

	/**
	 * Default constructor
	 * 
	 * @param encryptedMiTreeInformation
	 *            .
	 * @param encryptableObject
	 *            the encryptable object.
	 */
	public EncryptableDecryptedObject(
			EncryptedMetadataInformation encryptedMiTreeInformation,
			T encryptableObject) {
		this.encryptedMiTreeInformation = encryptedMiTreeInformation;
		this.encryptableObject = encryptableObject;
	}

	/**
	 * Encrypts the underlying object using key and password from
	 * {@link #encryptedMiTreeInformation}.
	 * 
	 * @return The encrypted Object.
	 */
	public U encrypt() {
		return encryptableObject.encrypt(
				encryptedMiTreeInformation.getFileName(),
				encryptedMiTreeInformation.getKey(),
				encryptedMiTreeInformation.getIv());
	}

	/**
	 * Get the encryption information.
	 * 
	 * @return the encryptedMiTreeInformation
	 */
	public EncryptedMetadataInformation getEncryptedMiTreeInformation() {
		return encryptedMiTreeInformation;
	}

	/**
	 * @return the encryptableObject
	 */
	public T getEncryptableObject() {
		return encryptableObject;
	}

}
