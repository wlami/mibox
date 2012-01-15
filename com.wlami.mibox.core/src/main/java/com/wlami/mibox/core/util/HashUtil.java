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
package com.wlami.mibox.core.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HashUtil {

	/** internal logger. */
	private static Logger log = LoggerFactory.getLogger(HashUtil.class);

	/** reference to a {@link MessageDigest} */
	private static MessageDigest messageDigest;

	/** Constant for accessing the SHA256 algorithm. */
	public static final String SHA_256_MESSAGE_DIGEST = "SHA-256";

	/** get a {@link MessageDigest} implementation */
	static {
		try {
			messageDigest = MessageDigest.getInstance(SHA_256_MESSAGE_DIGEST);
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
			// TODO Tell the user something is terribly wrong!
		}
	}

	/**
	 * calculates the SHA256 hash value of an byte array. It uses all of the
	 * bytes.
	 * 
	 * @param input
	 *            input array to be hashed.
	 * @return returns the hash as String.
	 */
	public static String calculateSha256(byte[] input) {
		return calculateHash(input, input.length, SHA_256_MESSAGE_DIGEST);
	}

	/**
	 * calculates the SHA256 hash value of the first length bytes of the array.
	 * 
	 * @param input
	 *            input array to be hashed.
	 * @param length
	 *            number of bytes to be used for calculation.
	 * @return returns the hash as String.
	 */
	public static String calculateSha256(byte[] input, int length) {
		return calculateHash(input, length, SHA_256_MESSAGE_DIGEST);
	}

	/**
	 * internal hash calulcator. can be called with differen digest algorithms.
	 * 
	 * @param input
	 *            input array to be hashed.
	 * @param digest
	 *            name of the hash algorithm.
	 * @return returns the hash as String
	 */
	private static String calculateHash(byte[] input, int length, String digest) {
		messageDigest.update(input, 0, length);
		return digestToString(messageDigest.digest());
	}

	/**
	 * Creates a String from a MessageDigest result.
	 * 
	 * @param input
	 *            Hash as byte array.
	 * @return Returns the hash as a {@link String}.
	 */
	public static String digestToString(byte[] input) {
		Formatter formatter = new Formatter();
		for (byte b : input) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	/**
	 * Creates a byte array from a hex string.
	 * 
	 * @param input
	 *            hex string
	 * @return a byte array
	 */
	public static byte[] stringToDigest(String input) {
		int length = input.length();
		byte[] digest = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			digest[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character
					.digit(input.charAt(i + 1), 16));
		}
		return digest;
	}

	/**
	 * Splits the int into 4 bytes and returns them as a <code>byte</code>
	 * array.
	 * 
	 * @param i
	 *            the int to be converted.
	 * @return the resulting byte array.
	 */
	public static byte[] intToByteArray(Integer i) {
		return ByteBuffer.allocate(16).putInt(i).array();
	}

}
