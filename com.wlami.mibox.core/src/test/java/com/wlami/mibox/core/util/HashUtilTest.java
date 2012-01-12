package com.wlami.mibox.core.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

	@Test
	public void testDigestFileChunks() throws IOException {
		File f = new File( ClassLoader.getSystemResource("data/1_400_000B.input").getFile() );
		FileInputStream fis = new FileInputStream(f);
		int fullFileSize = (int) f.length();
		byte[] fullFile = new byte[fullFileSize];
		fis.read(fullFile, 0, fullFileSize);
		System.out.println(HashUtil.calculateSha256(fullFile));
		fis.close();

		fis = new FileInputStream(f);
		int length = 1024 * 1024;
		for (int i = 0; i < 2; i++) {
			byte[] chunkArray = new byte[length];
			int readBytes = fis.read(chunkArray, 0, length);
			System.out.println("Chunk " + (i + 1) + " "
					+ HashUtil.calculateSha256(chunkArray, readBytes));
		}
	}
}
