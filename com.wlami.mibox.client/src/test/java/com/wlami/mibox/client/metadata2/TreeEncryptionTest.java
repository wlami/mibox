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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.metadata.MFile;
import com.wlami.mibox.core.encryption.PBKDF2;
import com.wlami.mibox.core.util.HashUtil;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public class TreeEncryptionTest {

	public final static byte[] key = PBKDF2.getKeyFromPasswordAndSalt(
			"ultrasecret", "username");
	public final static byte[] iv = HashUtil.calculateMD5Bytes("username"
			.getBytes());

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * test for the encryption of the metadata2 metadata =)
	 * 
	 * @throws IOException
	 */
	@Test
	public void testEncryption() throws IOException {
		// name für root metadata MiTree ist "root"!
		final String rootName = "/";
		DecryptedMiTree decryptedMiTree = new DecryptedMiTree();
		// foldername for root is SLASH
		decryptedMiTree.setFolderName(rootName);

		MFile mFile = new MFile();
		mFile.setFileHash("asdrftgjkl");
		// mFile.setFolder("/"); This has to be the decrypted MiTree later
		// on.!!!!!!!
		mFile.setLastModified(new Date());
		mFile.setName("hallo");
		MChunk mChunk = new MChunk(0);
		mChunk.setDecryptedChunkHash("decrypted");
		mChunk.setEncryptedChunkHash("encrypted");
		mChunk.setLastChange(new Date());
		mChunk.setMFile(mFile);
		mFile.getChunks().add(mChunk);
		decryptedMiTree.getFiles().put(mFile.getName(), mFile);

		final byte[] key = PBKDF2.getKeyFromPasswordAndSalt("ultrasecret",
				"username");
		final byte[] iv = HashUtil.calculateMD5Bytes("username".getBytes());
		EncryptedMiTree encryptedMiTree = decryptedMiTree.encrypt(key, iv);

		FileOutputStream fileOutputStream = new FileOutputStream(
				"encryptedrootmetadata");
		fileOutputStream.write(encryptedMiTree.getContent());
		fileOutputStream.close();

	}

	@Test
	public void testDecryption() throws IOException, CryptoException {
		File file = new File("encryptedrootmetadata");
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] content = new byte[(int) file.length()];
		fileInputStream.read(content);
		EncryptedMiTree encryptedMiTreeLoader = new EncryptedMiTree(content);
		DecryptedMiTree decryptedMiTree = encryptedMiTreeLoader
				.decrypt(key, iv);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(decryptedMiTree));
	}
	
	@Test
	public void testJsonArray() throws JsonGenerationException, JsonMappingException, IOException {
		class WLadi {
			byte[] inhalt = new byte[]{1,2,3,'c'};
			/**
			 * @return the inhalt
			 */
			public byte[] getInhalt() {
				return inhalt;
			}

			/**
			 * @param inhalt the inhalt to set
			 */
			public void setInhalt(byte[] inhalt) {
				this.inhalt = inhalt;
			}
			
		}
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(new WLadi()));
	}



}
