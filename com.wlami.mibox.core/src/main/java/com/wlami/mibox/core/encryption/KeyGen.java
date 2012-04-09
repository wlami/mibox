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

import java.security.SecureRandom;

/**
 * Generated keys using the {@link SecureRandom} class.
 */
public class KeyGen {
	
	private SecureRandom secureRandom;
	
	public KeyGen() {
		secureRandom = new SecureRandom();
	}

	/**
	 * Generates a byte array of the given size.
	 * @param byteCount Size of the byte [].
	 * @return Byte array filled with random data.
	 */
	public byte[] generateRandomBytes(int byteCount) {
		byte[] result = new byte[byteCount];
		secureRandom.nextBytes(result);
		return result;
	}
}
