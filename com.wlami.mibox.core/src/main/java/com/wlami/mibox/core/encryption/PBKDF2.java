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

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * This class provides methods for key derivation from passwords.
 */
public class PBKDF2 {

	/** Default key length in bit */
	private static final int DEFAULT_KEY_LENGTH_BIT = 256;
	
	/** PBKDF2 uses this number of rounds for key derivation */
	private static final int DEFAULT_KEY_TRANSFORMATION_ROUNDS = 10000;
	
	/** hidden constructor because this is an utility class */
	private PBKDF2() {
	}

	protected static byte[] doPbkdf2(String password, String salt, int rounds) {
		PBEParametersGenerator pbeParametersGenerator = new PKCS5S2ParametersGenerator();
		pbeParametersGenerator
				.init(PBEParametersGenerator.PKCS5PasswordToBytes(password
						.toCharArray()), PBEParametersGenerator
						.PKCS5PasswordToBytes(salt.toCharArray()), rounds);
		KeyParameter keyParameter = (KeyParameter) pbeParametersGenerator
				.generateDerivedParameters(DEFAULT_KEY_LENGTH_BIT);
		return keyParameter.getKey();
	}

	/**
	 * Get an 256bit key from a given password. This key can be used for aes
	 * encryption.
	 * 
	 * @param password
	 *            The password from which the key is derived
	 * @param salt
	 *            salt value
	 * @return returns an byte array with length 32;
	 */
	public static byte[] getKeyFromPasswordAndSalt(String password, String salt) {
		return doPbkdf2(password, salt, DEFAULT_KEY_TRANSFORMATION_ROUNDS);
	}

}
