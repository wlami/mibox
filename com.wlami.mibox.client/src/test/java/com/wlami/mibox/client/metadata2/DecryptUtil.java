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
import java.net.URISyntaxException;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.wlami.mibox.client.metadata.DecryptedMiFile;
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

	public void test1() throws IOException, URISyntaxException {
		DecryptedMiTree t1 = new DecryptedMiTree();
		t1.setFolderName("Folder1");
		t1.getFiles().put("File1", new DecryptedMiFile());
		t1.getFiles().put("File2", new DecryptedMiFile());
		t1.getSubfolder().put("subfolder1", new EncryptedMiTreeInformation());
		t1.getSubfolder().put("subfolder2", new EncryptedMiTreeInformation());
		EncryptedMiTree e1 = t1.encrypt("Folder1", key, iv);
		Base64.encode(e1.getContent(), System.out);
		FileOutputStream fos = new FileOutputStream(
"DecryptUtil.test");
		fos.write(e1.getContent());
	}

	@Test
	public void test() throws JsonParseException, JsonMappingException, CryptoException, IOException,
	URISyntaxException {
		final String filename = "root";
		EncryptedMiTreeRepository encryptedMiTreeRepository = Mockito.mock(EncryptedMiTreeRepository.class);
		EncryptedMiTree data = new EncryptedMiTree();
		data.setName("root");
		File inputFile = new File(DecryptUtil.class.getResource("/DecryptUtil.test").getFile());
		FileInputStream fis = new FileInputStream(inputFile);
		byte[] content = new byte[(int) inputFile.length()];
		fis.read(content);
		fis.close();
		data.setContent(content);
		Mockito.when(encryptedMiTreeRepository.loadEncryptedMiTree(filename)).thenReturn(data);
		EncryptedMiTree root =  encryptedMiTreeRepository.loadEncryptedMiTree(filename);
		DecryptedMiTree decryptedMiTreeroot = root.decrypt(key, iv);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(decryptedMiTreeroot));

		for( EncryptedMiTreeInformation encryptedMiTreeInformation : decryptedMiTreeroot.getSubfolder().values()) {
			EncryptedMiTree encryptedMiTree =  encryptedMiTreeRepository.loadEncryptedMiTree(encryptedMiTreeInformation.getFileName());
			if (encryptedMiTree != null) {
				DecryptedMiTree decryptedMiTree = encryptedMiTree.decrypt(encryptedMiTreeInformation.getKey(), encryptedMiTreeInformation.getIv());
				System.out.println(mapper.writeValueAsString(decryptedMiTree));
			}
		}
	}

}
