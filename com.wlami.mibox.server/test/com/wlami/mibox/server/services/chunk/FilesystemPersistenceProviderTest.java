/**
 *     MiBox Server - folder synchronization backend
 *  Copyright (C) 2012 Wladislaw Mitzel
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
package com.wlami.mibox.server.services.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.server.services.persistence.FileSystemPersistenceProvider;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class FilesystemPersistenceProviderTest {

	FileSystemPersistenceProvider fcpp;

	byte[] testData;

	public static final String FILENAME = "FilesystemChunkPersistenceProviderTest";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fcpp = new FileSystemPersistenceProvider();
		fcpp.setStoragePath(".");
		fcpp.setName("Chunk");
		testData = new byte[] { 't', 'e', 's', 't' };
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.server.services.chunk.FilesystemChunkPersistenceProvider#retrieveChunk(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testRetrieveChunk() throws IOException, InterruptedException {
		File file = new File(".", FILENAME);
		FileOutputStream f = new FileOutputStream(file);

		f.write(testData);
		f.flush();
		f.close();
		f = null;

		byte[] result = fcpp.retrieveFile(FILENAME);
		Assert.assertArrayEquals(testData, result);

		Assert.assertTrue(file.delete());
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.server.services.chunk.FilesystemChunkPersistenceProvider#persistChunk(java.lang.String, byte[])}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPersistChunk() throws IOException {
		File file = new File(".", FILENAME);

		if (file.exists()) {
			Assert.fail("There is a file which should not exist yet: "
					+ file.getAbsolutePath());
		}

		fcpp.persistFile(FILENAME, testData);

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] read = new byte[testData.length];
		fileInputStream.read(read);
		fileInputStream.close();
		Assert.assertArrayEquals(testData, read);
		Assert.assertTrue(file.delete());
	}

}
