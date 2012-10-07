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
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;
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

	@Resource
	public MetaMetaDataHolder metaMetaDataHolder;

	/**
	 * @return the appSettingsDao
	 */
	public AppSettingsDao getAppSettingsDao() {
		return appSettingsDao;
	}

	/**
	 * @param appSettingsDao
	 *            the appSettingsDao to set
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
		MetadataWorker metadataWorker = new MetadataWorker(appSettingsDao, chunkTransportProvider, null, null, null,
				null, chunkEncryption);

		DecryptedMetaMetaData decryptedMetaMetaData = metaMetaDataHolder.getDecryptedMetaMetaData();
		EncryptedMiTree encryptedMiTree = encryptedMiTreeRepository.loadEncryptedMiTree(decryptedMetaMetaData.getRoot()
				.getFileName());

		MFile mFile = metadataUtil.locateMFile("/Mozart_On_Crack_on_crack.mp3");

		String[] encryptedChunkHashes = new String[] {
				"5e864f6b3e89276b84280cb4153daf17bb85a8019a331739e7b96167df87a68b",
				"7701d7fa9508ef1acbcc75680e5724aadc913f1d1982d403a9425420784d4493",
				"805bb0075b1ee4137f4a18bf89ffc9b009984515f876e82f9fc4fd6b9a4ae489",
				"4918907597afdde5945fce08d6a2e33bad247195745bd8402addf0dff3cadf26",
				"e91a969202ff496f735bec393128dcd3fbe9b0bb5134f02298a06b41aabbcaca",
		"130bd52878cc8dbf02e0f95cb721c78d9a975668d93833c4d9a998dd6a78383f" };
		for (int i = 0; i < mFile.getChunks().size(); i++) {
			mFile.getChunks().get(i).setEncryptedChunkHash(encryptedChunkHashes[i]);
		}
		chunkTransportProvider.startProcessing();
		System.out.println(mFile);
		metadataWorker.updateFileFromMetadata(new File("/home/wladislaw/mibox/Mozart_On_Crack_on_crack.mp3"), null,
				mFile);
		while (true) {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
