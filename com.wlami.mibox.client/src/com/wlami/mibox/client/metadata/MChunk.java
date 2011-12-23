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
package com.wlami.mibox.client.metadata;

import java.util.Date;

/**
 * This class represents the metadata of a file chunk.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public class MChunk {

	/**
	 * The file this chunk belongs to.
	 */
	private MFile file;

	/**
	 * Contains the hash of the encrypted chunk.
	 */
	private String encryptedChunkHash;

	/**
	 * Last time the chunk has been synchronized.
	 */
	private Date lastSync;

	/**
	 * Last time the chunk has been modified.
	 */
	private Date lastChange;

	/**
	 * This is the hash value of the non-encrypted chunk. It is used for
	 * encryption and decryption.
	 */
	private String decryptedChunkHash;

	/**
	 * @return the encryptedChunkHash
	 */
	public String getEncryptedChunkHash() {
		return encryptedChunkHash;
	}

	/**
	 * @param encryptedChunkHash
	 *            the encryptedChunkHash to set
	 */
	public void setEncryptedChunkHash(String encryptedChunkHash) {
		this.encryptedChunkHash = encryptedChunkHash;
	}

	/**
	 * @return the lastSync
	 */
	public Date getLastSync() {
		return lastSync;
	}

	/**
	 * @param lastSync
	 *            the lastSync to set
	 */
	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	/**
	 * @return the lastChange
	 */
	public Date getLastChange() {
		return lastChange;
	}

	/**
	 * @param lastChange
	 *            the lastChange to set
	 */
	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}

	/**
	 * @return the decryptedChunkHash
	 */
	public String getDecryptedChunkHash() {
		return decryptedChunkHash;
	}

	/**
	 * @param decryptedChunkHash
	 *            the decryptedChunkHash to set
	 */
	public void setDecryptedChunkHash(String decryptedChunkHash) {
		this.decryptedChunkHash = decryptedChunkHash;
	}

}
