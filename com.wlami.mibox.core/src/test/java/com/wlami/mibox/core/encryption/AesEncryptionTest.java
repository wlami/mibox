/**
 *     MiBox Core - Common used classes
 *  Copyright (C) 2011 MiBox
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
package com.wlami.mibox.core.encryption;

import org.bouncycastle.crypto.CryptoException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wlami.mibox.core.util.HashUtil;

/**
 * 
 */
public class AesEncryptionTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws CryptoException {
		String hashKey = "f6952d6eef555ddd87aca66e56b91530222d6e318414816f3ba7cf5bf694bf0f";
		String plaintext = "AAAA";
		byte[] encrypted = AesEncryption.encrypt(plaintext.getBytes(), hashKey,
				1);
		System.out.println(HashUtil.digestToString(encrypted));
		byte[] decrypted = AesEncryption.decrypt(encrypted, hashKey, 1);
		System.out.println(new String(decrypted));
		Assert.assertEquals(plaintext, new String(decrypted));
	}

}
