package com.wlami.mibox.core.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.junit.Before;
import org.junit.Test;

public class KeyGenTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGenerateRsaKeyPair() {
		BigInteger.probablePrime(1024, new SecureRandom());
		final String password ="secure_password";
		final int keySize = 2048;
		AsymmetricCipherKeyPair keyPair = KeyGen.generateRsaKeyPair(password,
				keySize);
	}

}
