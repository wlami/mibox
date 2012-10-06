/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2012 Stefan Baust
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
package com.wlami.mibox.client.networking.synchronization;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata.MFile;
import com.wlami.mibox.client.metadata.MetadataUtil;
import com.wlami.mibox.client.metadata.MetadataWorker;
import com.wlami.mibox.client.metadata2.DecryptedMetaMetaData;
import com.wlami.mibox.client.metadata2.EncryptedMetaMetaDataRepository;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeRepository;
import com.wlami.mibox.client.metadata2.MetaMetaDataSetup;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;

/**
 * @author Stefan Baust
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration("/filedownloadtest.xml")
public class FileDownloadTest {
	
	@Resource
	public AppSettingsDao appSettingsDao;
	
	@Resource
	public TransportProvider<ChunkUploadRequest> chunkTransportProvider;
	
	@Autowired
	public ChunkEncryption chunkEncryption;
	
	@Resource
	public EncryptedMetaMetaDataRepository encryptedMetaMetaDataRepository;
	
	@Resource
	public MetadataUtil metadataUtil;
	
	@Autowired
	public EncryptedMiTreeRepository encryptedMiTreeRepository;
	
	/**
	 * @return the appSettingsDao
	 */
	public AppSettingsDao getAppSettingsDao() {
		return appSettingsDao;
	}

	/**
	 * @param appSettingsDao the appSettingsDao to set
	 */
	public void setAppSettingsDao(AppSettingsDao appSettingsDao) {
		this.appSettingsDao = appSettingsDao;
	}


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

	@Test
	public void test() throws JsonParseException, JsonMappingException, CryptoException, IOException {
		MetadataWorker metadataWorker = new MetadataWorker(appSettingsDao, chunkTransportProvider, null, null, null, null, chunkEncryption);
		
		MetaMetaDataSetup metaMetaDataSetup = new MetaMetaDataSetup();
		metaMetaDataSetup.setRepository(encryptedMetaMetaDataRepository);
		DecryptedMetaMetaData decryptedMetaMetaData = metaMetaDataSetup.setupMetaMetaData(appSettingsDao.load());
		EncryptedMiTree encryptedMiTree = encryptedMiTreeRepository.loadEncryptedMiTree(decryptedMetaMetaData.getRoot().getFileName());
		MFile mFile = metadataUtil.locateMFile(encryptedMiTree, decryptedMetaMetaData.getRoot(), "/Mozart_On_Crack_on_crack.mp3"); 
		System.out.println(mFile);
		//metadataWorker.updateFileFromMetadata(new File("G:/Data/mibox_test/Mozart_On_Crack_on_crack.mp3"), null, incomingMFile);
	}

}
