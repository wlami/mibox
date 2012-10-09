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
package com.wlami.mibox.client.networking.synchronization;

import java.io.IOException;

import javax.annotation.Resource;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata.MetadataUtil;
import com.wlami.mibox.client.metadata.MetadataWorker;
import com.wlami.mibox.client.metadata2.EncryptedMetaMetaDataRepository;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeRepository;
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;

/**
 * Tests the initial download of the mibox content. <br/>
 * <br/>
 * <b>Prerequisite</b>: One has to be in posession of local meta meta data with
 * {@link EncryptedMiTreeInformation} for the root.
 * 
 * @author wladislaw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/filedownloadtest.xml")
public class InitialSynchronizationOfExistingAccount {

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

	@Test
	public void synchronizeExistingRemoteMiboxToFilesystem() throws JsonParseException, JsonMappingException,
	CryptoException, IOException {

		chunkTransportProvider.startProcessing();

		MetadataWorker metadataWorker = new MetadataWorker(appSettingsDao, chunkTransportProvider, null,
				encryptedMiTreeRepository, metadataUtil, metaMetaDataHolder, chunkEncryption);

		metadataWorker.synchronizeLocalMetadataWithRemoteMetadata();

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
