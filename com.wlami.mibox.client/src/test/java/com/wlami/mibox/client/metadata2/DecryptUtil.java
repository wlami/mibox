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
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.core.encryption.PBKDF2;
import com.wlami.mibox.core.util.HashUtil;

/**
 * @author wladislaw
 *
 */
public class DecryptUtil {
	
	public final static byte[] key = PBKDF2.getKeyFromPasswordAndSalt(
			"secret123", "wlami");
	public final static byte[] iv = HashUtil.calculateMD5Bytes("wlami"
			.getBytes());
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws JsonParseException, JsonMappingException, CryptoException, IOException {
		final String filename = "root";
		EncryptedMiTreeRepository encryptedMiTreeRepository = new EncryptedMiTreeRepository();
		EncryptedMiTree root =  encryptedMiTreeRepository.loadEncryptedMiTree(filename);
		DecryptedMiTree decryptedMiTreeroot = root.decrypt(key, iv);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(decryptedMiTreeroot));
		
		for( EncryptedMiTreeInformation encryptedMiTreeInformation : decryptedMiTreeroot.getSubfolder().values()) {
			EncryptedMiTree encryptedMiTree =  encryptedMiTreeRepository.loadEncryptedMiTree(encryptedMiTreeInformation.getFileName());
			DecryptedMiTree decryptedMiTree = encryptedMiTree.decrypt(encryptedMiTreeInformation.getKey(), encryptedMiTreeInformation.getIv());
			System.out.println(mapper.writeValueAsString(decryptedMiTree));
		}
	}

}
