package com.wlami.mibox.server.services;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChunkManagerTest.class })
public class AllServices {

	@BeforeClass
	public static void setUp() {
		System.setProperty("persistence.unit.name", "test");
	}
}
