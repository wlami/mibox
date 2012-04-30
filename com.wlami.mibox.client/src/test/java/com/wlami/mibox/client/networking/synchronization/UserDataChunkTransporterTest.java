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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
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
import com.wlami.mibox.core.util.HashUtil;

/**
 * @author Wladislaw Mitzel
 * 
 */

public class UserDataChunkTransporterTest {

	AppSettings appSettings;
	UserDataChunkTransporter userDataChunkTransporter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		// Path testFile =
		// Paths.get(ClassLoader.getSystemResource("1_400_000B.input").getFile());
		// Path target = Paths.get("test/data/1_400_000B.input");
		// Files.copy(testFile, target, StandardCopyOption.REPLACE_EXISTING);
		appSettings = new PropertyAppSettings();
		appSettings
		.setServerUrl("http://localhost:8080/com.wlami.mibox.server/");
		appSettings.setWatchDirectory("src/test/resources/data/");

		AppSettingsDao appSettingsDao = mock(AppSettingsDao.class);
		when(appSettingsDao.load()).thenReturn(appSettings);
		userDataChunkTransporter = new UserDataChunkTransporter(appSettingsDao,
				null);
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
		MFile file = new MFile();
		file.setName("hallo.txt");
		MChunk chunk = new MChunk(0);
		chunk.setDecryptedChunkHash("753692ec36adb4c794c973945eb2a99c1649703ea6f76bf259abb4fb838e013e");
		chunk.setMFile(file);
		file.getChunks().add(chunk);
		File hddFile = new File("src/test/resources/data/", file.getName());
		assertEquals(
				"7cce494647e022c65d5b17db7cf3657d86f71538e7bbb9a4d1e36febee88ec8d",
				userDataChunkTransporter.encryptAndUploadChunk(file
						.getChunks().get(0), hddFile));
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.client.networking.synchronization.UserDataChunkTransporter#downloadAndDecryptChunk(com.wlami.mibox.client.metadata.MChunk)}
	 * .
	 * 
	 * @throws IOException
	 * @throws CryptoException
	 */

	@Test
	public void testDownloadAndDecryptChunk() throws IOException,
	CryptoException {
		MChunk mChunk = new MChunk(0);
		mChunk.setEncryptedChunkHash("7cce494647e022c65d5b17db7cf3657d86f71538e7bbb9a4d1e36febee88ec8d");
		mChunk.setDecryptedChunkHash("753692ec36adb4c794c973945eb2a99c1649703ea6f76bf259abb4fb838e013e");
		byte[] decrypted = userDataChunkTransporter
				.downloadAndDecryptChunk(mChunk);
		System.out.println(new String(decrypted));

	}

	@Test
	public void testEncryptAndUploadChunk1_4_MB() throws CryptoException,
	IOException {
		MFile file = new MFile();
		//new File( ClassLoader.getSystemResource("data/dateiname.txt").
		file.setName("1_400_000B.input");
		MChunk chunk1 = new MChunk(0);
		chunk1.setDecryptedChunkHash("e28f41c7c362ba70c4143082bdc24a6655686bdc9ced9050af4da9423f6279a6");
		chunk1.setMFile(file);
		file.getChunks().add(chunk1);
		MChunk chunk2 = new MChunk(1);
		chunk2.setDecryptedChunkHash("18e1cc7633d6a7936338ed908ef4eb8092204fa871264d0efff71672d9aa5b1a");
		chunk2.setMFile(file);
		file.getChunks().add(chunk2);
		File hddFile = new File("src/test/resources/data/", file.getName());
		assertEquals(
				"cc0f4a05fd6c6eafb8435ec0538a4730e4755154ca929fef45c953f6b4b133e5",
				userDataChunkTransporter.encryptAndUploadChunk(file.getChunks()
						.get(0), hddFile));
		assertEquals(
				"4e019fc9c3b39505d984749014d513637857b829ccb007f43239675260a97d46",
				userDataChunkTransporter.encryptAndUploadChunk(file.getChunks()
						.get(1), hddFile));
	}

	@Test
	public void testDownloadAndDecryptChunk1_4_MB() throws IOException,
	CryptoException {
		MChunk mChunk1 = new MChunk(0);
		mChunk1.setEncryptedChunkHash("cc0f4a05fd6c6eafb8435ec0538a4730e4755154ca929fef45c953f6b4b133e5");
		mChunk1.setDecryptedChunkHash("e28f41c7c362ba70c4143082bdc24a6655686bdc9ced9050af4da9423f6279a6");
		byte[] decrypted = userDataChunkTransporter
				.downloadAndDecryptChunk(mChunk1);
		System.out.println(new String(decrypted));
		System.out.println(HashUtil.calculateSha256(decrypted));

		MChunk mChunk2 = new MChunk(1);
		mChunk2.setEncryptedChunkHash("4e019fc9c3b39505d984749014d513637857b829ccb007f43239675260a97d46");
		mChunk2.setDecryptedChunkHash("18e1cc7633d6a7936338ed908ef4eb8092204fa871264d0efff71672d9aa5b1a");
		byte[] decrypted2 = userDataChunkTransporter
				.downloadAndDecryptChunk(mChunk2);
		// System.out.println(new String(decrypted2));
		System.out.println(HashUtil.calculateSha256(decrypted2));

		MFile outputMFile = new MFile();
		outputMFile.setName("decrypted");
		File f = new File(appSettings.getWatchDirectory(),
				outputMFile.getName() + "decrypted");

		FileOutputStream oStream = new FileOutputStream(f);
		oStream.write(decrypted);
		oStream.write(decrypted2);
		oStream.flush();
		oStream.close();
	}

}
