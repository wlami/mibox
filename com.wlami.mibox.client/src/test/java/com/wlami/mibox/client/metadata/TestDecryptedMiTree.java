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
package com.wlami.mibox.client.metadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;

/**
 * @author wladislaw
 *
 */
public class TestDecryptedMiTree {

	DecryptedMiTree folder;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		folder = TestUtil.getSimpleMetadata();
	}

	@Test
	public void testJson() throws JsonGenerationException,
	JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		mapper.writeValue(System.out, folder);
		mapper.writeValue(bout, folder);

		DecryptedMiTree readFolder = mapper.readValue(bout.toByteArray(),
				DecryptedMiTree.class);
		System.out.println("");
		mapper.writeValue(System.out, readFolder);
		System.out.flush();
	}

	@Test
	public void testHashtable() {
		EncryptedMiTreeInformation retrievedFolder = folder.getSubfolder().get(
				"subfolder");
		assert (retrievedFolder != null);
	}


}
