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
package com.wlami.mibox.client.networking.encryption;

import java.io.File;
import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;

import com.wlami.mibox.client.metadata.MChunk;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 */
public interface ChunkEncryption {

	/**
	 * Encrypts the content of a {@link MChunk} inside a {@link File}.
	 * 
	 * @param mChunk
	 *            The chunk to encrypt.
	 * @param file
	 *            The file which containing the chunk.
	 * @throws IOException
	 *             If file is not found or cannot be read.
	 * @throws CryptoException
	 *             If anything goes wrong during encryption.
	 * @return An {@link DataChunk} which contains the ciphertext.
	 */
	public DataChunk encryptChunk(MChunk mChunk, File file)
			throws IOException, CryptoException;

	/**
	 * Decrypts the content of an encrypted chunk (file). The mChunk contains
	 * the key and iv for decryption.
	 * 
	 * @param mChunk
	 *            Metadata con the chunk used for decryption
	 * @param file
	 *            the encrypted chunk file which shall be decrypted.
	 * @return A Datachunk with decrypted data.
	 * @throws IOException
	 *             If file is not found or cannot be read.
	 * @throws CryptoException
	 *             If anything goes wrong during decryption.
	 */
	public DataChunk decryptChunk(MChunk mChunk, File file) throws IOException,
	CryptoException;

	/**
	 * Decrypts the content of an encrypted chunk (file). The mChunk contains
	 * the key and iv for decryption.
	 * 
	 * @param mChunk
	 *            Metadata con the chunk used for decryption
	 * @param cipherText
	 *            the ciphertext which shall be processed
	 * @return A Datachunk with decrypted data.
	 * @throws IOException
	 *             If file is not found or cannot be read.
	 * @throws CryptoException
	 *             If anything goes wrong during decryption.
	 */
	public DataChunk decryptChunk(MChunk mChunk, byte[] cipherText);
}
