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

import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;

import com.wlami.mibox.client.metadata.MChunk;

/**
 * This interface describes classes which can be used to transport encrypted
 * data to the server and from it.
 */
public interface Transporter {

	/**
	 * Uploads a chunk to the server.
	 * 
	 * @param chunk
	 *            Chunk to be uploaded.
	 * @return the hash of the encrypted chunk
	 */
	public String encryptAndUploadChunk(MChunk chunk) throws CryptoException,
			IOException;

	/**
	 * Downloads a chunk and decrypts it afterwards.
	 * 
	 * @param chunk
	 *            Metadata of the chunk to be downloaded.
	 * @return byte array with the decrypted data.
	 */
	public byte[] downloadAndDecryptChunk(MChunk chunk) throws CryptoException,
			IOException;

}
