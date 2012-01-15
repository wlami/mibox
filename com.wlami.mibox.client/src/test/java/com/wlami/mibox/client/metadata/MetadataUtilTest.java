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

import org.junit.Before;
import org.junit.Test;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MetadataUtilTest {

	MFolder root;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		root = TestUtil.getSimpleMetadata();
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.client.metadata.MetadataUtil#locateMFile(com.wlami.mibox.client.metadata.MFolder, java.lang.String)}
	 * .
	 */
	@Test
	public void testLocateMFile() {
		assertNotNull(MetadataUtil.locateMFile(root, "/file1"));
	}

	@Test
	public void testLocateMFile2() {
		String filepath2 = "/subfolder/file2";
		assertNotNull(MetadataUtil.locateMFile(root, filepath2));
	}

	@Test
	public void testLocateMFile3() {
		String filepath3 = "/subfolder/subfolder2/file3";
		assertNotNull(MetadataUtil.locateMFile(root, filepath3));
	}

	@Test
	public void testLocateMFile4() {
		String filepath4 = "/subfolder/subfoldebukkr2/file3";
		assertNotNull(MetadataUtil.locateMFile(root, filepath4));
	}
}
