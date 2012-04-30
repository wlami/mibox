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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MetadataUtilTest {

	DecryptedMiTree root;
	EncryptedMiTree encRoot;
	EncryptedMiTreeInformation encryptedMiTreeInformation;

	// TODO: Inject the EncryptedMiTreeRepo so that it can be substituted during
	// tests.

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		root = TestUtil.getSimpleMetadata();
		encryptedMiTreeInformation = TestUtil.getTreeCrypto();
		encRoot = root.encrypt(encryptedMiTreeInformation.getKey(),
				encryptedMiTreeInformation.getIv());
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.client.metadata.MetadataUtil#locateMFile(com.wlami.mibox.client.metadata.MFolder, java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws CryptoException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testLocateMFile() throws JsonParseException,
	JsonMappingException, CryptoException, IOException {
		assertNotNull(MetadataUtil.locateMFile(encRoot,
				encryptedMiTreeInformation, "/file1"));
	}

	@Test
	@Ignore
	public void testLocateMFile2() throws JsonParseException,
	JsonMappingException, CryptoException, IOException {
		String filepath2 = "/subfolder/file2";
		assertNotNull(MetadataUtil.locateMFile(encRoot,
				encryptedMiTreeInformation, filepath2));
	}

	@Test
	@Ignore
	public void testLocateMFile3() throws JsonParseException,
	JsonMappingException, CryptoException, IOException {
		String filepath3 = "/subfolder/subfolder2/file3";
		assertNotNull(MetadataUtil.locateMFile(encRoot,
				encryptedMiTreeInformation, filepath3));
	}

	@Test
	@Ignore
	public void testLocateMFile4() throws JsonParseException,
	JsonMappingException, CryptoException, IOException {
		String filepath4 = "/subfolder/subfoldebukkr2/file3";
		assertNotNull(MetadataUtil.locateMFile(encRoot,
				encryptedMiTreeInformation, filepath4));
	}
}
