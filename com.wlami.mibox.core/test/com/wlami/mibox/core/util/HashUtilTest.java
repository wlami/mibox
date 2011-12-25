package com.wlami.mibox.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HashUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testStringToDigestToString() {
		String input = "012345affe";
		byte[] digest = HashUtil.stringToDigest(input);
		String output = HashUtil.digestToString(digest);
		assertEquals(input, output);
	}

}
