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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.security.Security;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.application.PropertyAppSettings;
import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.metadata.MFile;
import com.wlami.mibox.client.metadata.MFolder;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class UserDataChunkTransporterTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.client.networking.synchronization.UserDataChunkTransporter#encryptAndUploadChunk(com.wlami.mibox.client.metadata.MChunk)}
	 * .
	 * 
	 * @throws IOException
	 * @throws CryptoException
	 */
	@Test
	public void testEncryptAndUploadChunk() throws CryptoException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		AppSettings appSettings = new PropertyAppSettings();
		appSettings
				.setServerUrl("http://localhost:8080/com.wlami.mibox.server/");
		appSettings.setWatchDirectory("test/data");

		MFolder root = new MFolder(null);
		root.setName("/");
		MFile file = new MFile();
		file.setFolder(root);
		file.setName("hallo.txt");
		MChunk chunk = new MChunk(0);
		chunk.setDecryptedChunkHash("753692ec36adb4c794c973945eb2a99c1649703ea6f76bf259abb4fb838e013e");
		chunk.setMFile(file);
		file.getChunks().add(chunk);

		AppSettingsDao appSettingsDao = mock(AppSettingsDao.class);
		when(appSettingsDao.load()).thenReturn(appSettings);
		UserDataChunkTransporter u = new UserDataChunkTransporter(
				appSettingsDao);
		assertEquals(
				"11b4161b312680802418c39160235921e5bfff3d36626915dac271f520192c7c",
				u.encryptAndUploadChunk(file.getChunks().get(0)));
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.client.networking.synchronization.UserDataChunkTransporter#downloadAndDecryptChunk(com.wlami.mibox.client.metadata.MChunk)}
	 * .
	 */
	@Test
	public void testDownloadAndDecryptChunk() {
		fail("Not yet implemented");
	}

}
