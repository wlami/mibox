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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.util.HashUtil;

/**
 * Generated keys using the {@link SecureRandom} class.
 */
public class KeyGen {

	private final SecureRandom secureRandom;

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

	/**
	 * This method generates a key pair for asymmetric encryption. It uses a
	 * password for the generation of pseudo-random numbers.
	 * 
	 * @param password
	 *            The password on which the keys are based.
	 * @param keySize
	 *            The key size in bits.
	 * @return A key pair based on the provided password. The key has a lenght
	 *         of keySize bits.
	 */
	public static AsymmetricCipherKeyPair generateRsaKeyPair(String password,
			int keySize) {

		/**
		 * PasswordBasedRandom uses a string input as the seed value for the
		 * generation of pseudo random bytes.
		 */
		final class PasswordBasedRandom extends SecureRandom {

			/** internal logger */
			private final Logger log = LoggerFactory
					.getLogger(PasswordBasedRandom.class);

			/** default serialVersionUID. */
			private static final long serialVersionUID = 1L;

			/**
			 * this message digest is used for the generation of pseudo random
			 * numbers. On creation it is seeded with the provided password.
			 */
			private MessageDigest messageDigest;

			/** internal data buffer */
			private byte[] data;

			/** offset in current data */
			private int offset;

			/**
			 * Creates a new object which provides pseudo random numbers based
			 * on the provided password.
			 * 
			 * @param password
			 *            The password which shall be used as seed.
			 */
			public PasswordBasedRandom(final String password) {
				try {
					messageDigest = MessageDigest
							.getInstance(HashUtil.SHA_256_MESSAGE_DIGEST);
					data = messageDigest.digest(password.getBytes("UTF-8"));
				} catch (NoSuchAlgorithmException e) {
					log.error("Terrible failure! We have no SHA256 available",
							e);
					// TODO tell the user
				} catch (UnsupportedEncodingException e) {
					log.error("We need UTF-8 for proper working", e);
				}
			}

			@Override
			public synchronized void nextBytes(byte[] bytes) {
				// fill each byte in the given byte array
				for (int i = 0; i < bytes.length; i++) {
					// if we used all bytes in our data buffer we create new
					// ones
					if (offset > data.length - 1) {
						data = messageDigest.digest(data);
						offset = 0;
					}
					// copy a byte from our data
					bytes[i] = data[offset];
					// increment our offset
					offset++;
				}
			}
		}
		// Create a new key generator
		RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
		// Define the key generation parameters. We use our password based
		// random number generator!
		KeyGenerationParameters keyGenerationParameters = new KeyGenerationParameters(new PasswordBasedRandom(password), keySize);
		// initialize the generator
		rsaKeyPairGenerator.init(keyGenerationParameters);
		// now return the generated keys!
		return rsaKeyPairGenerator.generateKeyPair();
	}
}
