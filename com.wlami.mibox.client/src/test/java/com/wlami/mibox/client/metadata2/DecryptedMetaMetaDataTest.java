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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wladislaw
 *
 */
public class DecryptedMetaMetaDataTest {

	public static final byte[] KEY = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8,
		9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1 };
	public static final byte[] IV = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			0, 1, 2, 3, 4, 5 };

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEncryptionAndDecryption() throws JsonParseException,
	JsonMappingException, CryptoException, IOException {
		DecryptedMetaMetaData d = new DecryptedMetaMetaData();
		d.setRootName("foo");
		EncryptedMetaMetaData e = d.encrypt("bar", KEY, IV);
		DecryptedMetaMetaData d2 = e.decrypt(KEY, IV);
		assertEquals(d.getRootName(), d2.getRootName());
	}

}
