package com.wlami.mibox.server.services;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

public class MetadataNotifierTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testListUpdatedMetadataSince() {
		DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.basicDateTime();
		System.out.println(dateTimeFormatter.print((ReadableInstant) null));
	}

}
