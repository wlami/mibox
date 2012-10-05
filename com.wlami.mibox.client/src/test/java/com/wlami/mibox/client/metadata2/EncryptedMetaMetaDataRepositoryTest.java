/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2012 wladislaw
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
package com.wlami.mibox.client.metadata2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.application.PropertyAppSettings;
import com.wlami.mibox.client.networking.adapter.RestTransporter;

/**
 * @author wladislaw
 *
 */
public class EncryptedMetaMetaDataRepositoryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.wlami.mibox.client.metadata2.EncryptedMetaMetaDataRepository#retrieveMetaMetaData(com.wlami.mibox.client.application.AppSettings)}.
	 */
	@Test
	public void testRetrieveMetaMetaData() {
		PropertyAppSettings settings = new PropertyAppSettings();
		settings.setUsername("horst");
		AppSettingsDao appSettingsDao = Mockito.mock(AppSettingsDao.class);
		Mockito.when(appSettingsDao.load()).thenReturn(settings);

		EncryptedMetaMetaDataRepository repository = new EncryptedMetaMetaDataRepository(
				appSettingsDao);
		RestTransporter transporter = new RestTransporter(
				"http://localhost:8080/com.wlami.mibox.server/rest/metametadatamanager/");
		repository.setRestTransporter(transporter);

		EncryptedMetaMetaData data = repository.retrieveMetaMetaData(settings);
		Assert.assertNotNull(data);
	}

}
