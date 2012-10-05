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
import java.io.FileInputStream;
import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.core.encryption.AesEncryption;
import com.wlami.mibox.core.util.HashUtil;

/**
 * This class is responsible for the encryption of chunk data using AES crypto.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class AesChunkEncryption implements ChunkEncryption {

	/** internal logger. */
	private static final Logger log = LoggerFactory
			.getLogger(AesChunkEncryption.class);

	/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.encryption.ChunkEncryption#encryptChunk(com.wlami.mibox.client.metadata.MChunk, java.io.File)
	 */
	@Override
	public DataChunk encryptChunk(MChunk chunk, File file)
			throws IOException, CryptoException {
		// read the chunk
		try (FileInputStream fis = new FileInputStream(file)) {
			int chunkSize = chunk.getMFile().getChunkSize();
			// encrypt it
			int fileChunkCount = chunk.getMFile().getChunks().size();
			int chunkPosition = chunk.getPosition();
			int arraySize;

			if (chunkPosition + 1 < fileChunkCount) {
				arraySize = chunk.getMFile().getChunkSize();
			} else {
				arraySize = (int) (file.length() % chunkSize);
			}
			log.debug("Encrypting chunk. Using arraySize of " + arraySize);
			byte[] plainChunkData = new byte[arraySize];
			// Skip bytes if we dont have the first chunk
			fis.skip(chunkPosition * chunkSize);
			// read the chunk data
			fis.read(plainChunkData, 0, arraySize);
			log.debug("Starting encryption");
			byte[] encryptedChunkData = AesEncryption.encrypt(plainChunkData,
					chunk.getDecryptedChunkHash(), chunkPosition);
			log.debug("Finished encryption");
			// calculate the encrypted hash
			String encryptedHash = HashUtil.calculateSha256(encryptedChunkData);
			log.debug("Calculate Encrypted Hash: [{}]", encryptedHash);
			return new DataChunk(true, encryptedHash, encryptedChunkData);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.networking.encryption.ChunkEncryption#decryptChunk
	 * (com.wlami.mibox.client.metadata.MChunk, java.io.File)
	 */
	@Override
	public DataChunk decryptChunk(MChunk mChunk, File file) throws IOException,
	CryptoException {
		try (FileInputStream fis = new FileInputStream(file)) {
			int length = (int) file.length(); // Warning: 2GB!
			log.debug("Decrypting chunk. Using arraySize of [{}]", length);
			byte[] encryptedChunkData = new byte[length];
			fis.read(encryptedChunkData);
			log.debug("Starting decryption");
			byte[] decryptedChunkData = AesEncryption.decrypt(
					encryptedChunkData, mChunk.getDecryptedChunkHash(),
					mChunk.getPosition());
			String decryptedHash = HashUtil.calculateSha256(decryptedChunkData);
			log.debug("Calculate decrypted hash: [{}]", decryptedHash);
			if (!decryptedChunkData.equals(mChunk.getDecryptedChunkHash())) {
				throw new CryptoException(
						"Hash of decrypted data does not match");
			}
			return new DataChunk(false, decryptedHash, decryptedChunkData);
		}
	}

}
