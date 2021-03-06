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

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.util.HashUtil;

/**
 * This utility class can be used to encrypt and decrypt byte arrays with AES.
 * Used for chunks encryption and decryption.
 */
public class AesEncryption {

	/** internal logger */
	private static final Logger log = LoggerFactory
			.getLogger(AesEncryption.class);

	/**
	 * Encrypts the plain byte array (the chunk) with the given key.
	 * Bouncycastle specific implementation.
	 * 
	 * @param plain
	 *            plain text to be encrypted.
	 * @param keyString
	 *            The key to use for encryption
	 * @param initVector
	 *            IV for encryption
	 * @return Returns the enrypted byte array.
	 */
	public static byte[] encrypt(byte[] plain, String keyString,
			Integer initVector) throws CryptoException {
		return crypt(true, plain, keyString,
				HashUtil.intToByteArray(initVector));
	}

	/**
	 * Encrypts the plain byte array (the chunk) with the given key.
	 * Bouncycastle specific implementation.
	 * 
	 * @param plain
	 *            plain text to be encrypted.
	 * @param keyString
	 *            The key to use for encryption
	 * @param initVector
	 *            IV for encryption
	 * @return Returns the enrypted byte array.
	 */
	public static byte[] encrypt(byte[] plain, String keyString,
			byte[] initVector) throws CryptoException {
		return crypt(true, plain, keyString, initVector);
	}

	/**
	 * Decrypts the plain byte array (the chunk) with the given key.
	 * Bouncycastle specific implementation.
	 * 
	 * @param ciphertext
	 *            cipher text to be decrypted.
	 * @param keyString
	 *            The key to use for decryption
	 * @param initVector
	 *            IV for encryption
	 * @return Returns the decrypted byte array.
	 */
	public static byte[] decrypt(byte[] ciphertext, String keyString,
			Integer initVector) throws CryptoException {
		return crypt(false, ciphertext, keyString,
				HashUtil.intToByteArray(initVector));
	}

	/**
	 * Decrypts the plain byte array (the chunk) with the given key.
	 * Bouncycastle specific implementation.
	 * 
	 * @param ciphertext
	 *            cipher text to be decrypted.
	 * @param keyString
	 *            The key to use for decryption
	 * @param initVector
	 *            IV for encryption
	 * @return Returns the decrypted byte array.
	 */
	public static byte[] decrypt(byte[] ciphertext, String keyString,
			byte[] initVector) throws CryptoException {
		return crypt(false, ciphertext, keyString, initVector);
	}

	/**
	 * Encrypts and decrypts a byte array.
	 * 
	 * @param encrypt
	 *            <code>true</code> for encryption <br/>
	 *            <code>false</code> for decryption
	 * @param ciphertext
	 *            the data which shall be decrypted or encrypted
	 * @param keyString
	 *            the key
	 * @param initVector
	 *            the iv
	 * @return an encrypted /decrypted byte array
	 * @throws CryptoException
	 */
	public static byte[] crypt(boolean encrypt, byte[] ciphertext,
			String keyString, byte[] initVector) throws CryptoException {
		byte[] key = HashUtil.stringToDigest(keyString);
		return crypt(encrypt, ciphertext, initVector, key);
	}

	/**
	 * Encrypts and decrypts a byte array.
	 * 
	 * @param encrypt
	 *            <code>true</code> for encryption <br/>
	 *            <code>false</code> for decryption
	 * @param ciphertext
	 *            the data which shall be decrypted or encrypted
	 * @param initVector
	 *            the iv
	 * @param key
	 *            the key
	 * @return an encrypted /decrypted byte array
	 * @throws CryptoException
	 */
	public static byte[] crypt(boolean encrypt, byte[] ciphertext,
			byte[] initVector, byte[] key) throws InvalidCipherTextException {
		log.debug("starting " + (encrypt ? "encryption" : "decryption"));
		BlockCipher engine = new AESEngine();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(engine));
		log.debug("retrieved an AESEngine");
		cipher.init(encrypt, new ParametersWithIV(new KeyParameter(key),
				initVector));
		byte[] cipherArray = new byte[cipher.getOutputSize(ciphertext.length)];
		log.debug("creatied cipherArray with size " + cipherArray.length
				+ "\n encryption...");
		int outputByteCount = cipher.processBytes(ciphertext, 0,
				ciphertext.length, cipherArray, 0);
		log.debug("finalizing cipher");
		outputByteCount += cipher.doFinal(cipherArray, outputByteCount);
		byte[] result = new byte[outputByteCount];
		System.arraycopy(cipherArray, 0, result, 0, outputByteCount);
		return result;
	}

}
