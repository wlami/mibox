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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.util.HashUtil;

/**
 * This utility class can be used to encrypt and decrypt byte arrays with AES.
 * Used for chunks encryption and decryption.
 */
public class AesEncryption {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/** internal logger */
	private static final Logger log = LoggerFactory
			.getLogger(AesEncryption.class);

	/**
	 * Encrypts the plain byte array (the chunk) with the given key.
	 * 
	 * @param plain
	 * @param key
	 * @param initVector
	 * @return
	 */
	public static byte[] encrypt(byte[] plain, String keyString,
			Integer initVector) {
		SecretKeySpec key = new SecretKeySpec(
				HashUtil.stringToDigest(keyString), 0, 32, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			IvParameterSpec iv = new IvParameterSpec(
					HashUtil.intToByteArray(initVector));
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] cipherArray = new byte[cipher.getOutputSize(plain.length)];
			int outputByteCount = cipher.update(plain, 0, plain.length,
					cipherArray, 0);
			outputByteCount += cipher.doFinal(cipherArray, outputByteCount);
			return cipherArray;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | InvalidAlgorithmParameterException
				| ShortBufferException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * Decrypts the encrypted byte array (the chunk) with the given key.
	 * 
	 * @param encrypted
	 * @param key
	 * @return
	 */
	public static byte[] decrypt(byte[] encrypted, String key) {
		return null;
	}
}
