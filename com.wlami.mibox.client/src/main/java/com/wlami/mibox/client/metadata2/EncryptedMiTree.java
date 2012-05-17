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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.encryption.AesEncryption;

/**
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 * 
 */
public class EncryptedMiTree {

	/** internal logger */
	private static Logger log = LoggerFactory.getLogger(EncryptedMiTree.class);

	/** JSON Object mapper for persistence. */
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * EncryptedChunkTransporter Create a new encrypted for a encrypted byte
	 * array.
	 * 
	 * @param content
	 *            The encrypted data.
	 */
	public EncryptedMiTree(byte[] content) {
		this.content = content;
	}

	/** encrypted content */
	private final byte[] content;

	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content; //TODO Check whether a deep copy is required at this point.
	}

	/**
	 * Decrypts the MiTree content
	 * 
	 * @throws CryptoException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public DecryptedMiTree decrypt(byte[] key, byte[] iv)
			throws CryptoException, JsonParseException, JsonMappingException,
			IOException {
		log.debug("decrypting MiTree");
		byte[] decrypted = AesEncryption.crypt(false, content,iv, key);
		log.debug("Decryption done. Trying now to read the data.");
		return objectMapper.readValue(decrypted, DecryptedMiTree.class);
	}

}
