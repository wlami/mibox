/**
 *     MiBox Core - Common used classes
 *  Copyright (C) 2012 MiBox
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

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.util.HashUtil;


/**
 * Test class for {@link PBKDF2}
 */
public class PBKDF2Test {
	
	/** internal logger */
	Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String PASSWORD = "test";
	private static final String SALT = "salt";
	private static final int ROUNDS = 100;
	private static final int ROUNDS_LONG = 200000;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.core.encryption.PBKDF2#doPbkdf2(java.lang.String, java.lang.String, int)}
	 * .
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testDoPbkdf2() throws UnsupportedEncodingException {
		byte[] result = PBKDF2.doPbkdf2(PASSWORD, SALT, ROUNDS);
		log.info("testDoPbkdf2:" + HashUtil.digestToString(result));
	}

	/**
	 * Test method for
	 * {@link com.wlami.mibox.core.encryption.PBKDF2#doPbkdf2(java.lang.String, java.lang.String, int)}
	 * .
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testDoPbkdf2LONG() throws UnsupportedEncodingException {
		byte[] result = PBKDF2.doPbkdf2(PASSWORD, SALT, ROUNDS_LONG);
		log.info("testDoPbkdf2LONG:" + HashUtil.digestToString(result));
	}
	
	/**
	 * Test method for
	 * {@link com.wlami.mibox.core.encryption.PBKDF2#getKeyFromPasswordAndSalt(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetKeyFromPasswordAndSalt() {
		byte[] result = PBKDF2.getKeyFromPasswordAndSalt(PASSWORD, SALT);
		log.info("testGetKeyFromPasswordAndSalt:" + HashUtil.digestToString(result));
	}

}
