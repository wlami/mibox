/**
 *     MiBox Client - folder synchronization client
 *  Copyright (C) 2011 Wladislaw Mitzel
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
package com.wlami.mibox.client.metadata;

import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppSettingsDao;
import com.wlami.mibox.client.metadata2.DecryptedMetaMetaData;
import com.wlami.mibox.client.metadata2.EncryptedMetaMetaDataRepository;
import com.wlami.mibox.client.metadata2.EncryptedMetadataObjectRepository;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;
import com.wlami.mibox.client.networking.synchronization.ChunkUploadRequest;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MetadataRepositoryImpl implements MetadataRepository {

	/** internal logging object. */
	private static final Logger log = LoggerFactory
			.getLogger(MetadataRepositoryImpl.class);

	/** Reference to the {@link AppSettingsDao} bean. */
	private final AppSettingsDao appSettingsDao;

	/** Reference to the {@link TransportProvider} bean. */
	private final TransportProvider<ChunkUploadRequest> chunkTransport;

	/** Reference to the {@link MetadataUtil} bean. */
	private final MetadataUtil metadataUtil;

	/** Reference to the {@link EncryptedMetaMetaDataRepository} bean. */
	private final EncryptedMetaMetaDataRepository encryptedMetaMetaDataRepository;

	/**
	 * reference to a {@link MetadataWorker} instance which handles the file
	 * structure.
	 */
	private MetadataWorker worker;

	/** loader reference which is needed for the worker */
	private final EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepo;

	/** loader reference which is needed for the worker */
	private final EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepo;

	DecryptedMetaMetaData decryptedMetaMetaData;

	/**
	 * set of incoming {@link ObservedFilesystemEvent} instances which shall be
	 * processed by the {@link MetadataWorker}.
	 */
	private final ConcurrentSkipListSet<ObservedFilesystemEvent> incomingEvents = new ConcurrentSkipListSet<ObservedFilesystemEvent>();

	private ChunkEncryption chunkEncryption;

	private MetaMetaDataHolder metaMetaDataHolder;

	/**
	 * default constructor.
	 */
	@Inject()
	public MetadataRepositoryImpl(AppSettingsDao appSettingsDao,
			TransportProvider<ChunkUploadRequest> chunkTransport,
			EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepository,
			EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepository,
 			MetadataUtil metadataUtil,
			EncryptedMetaMetaDataRepository encryptedMetaMetaDataRepository,
			ChunkEncryption chunkEncryption,
			MetaMetaDataHolder metaMetaDataHolder) {
		this.appSettingsDao = appSettingsDao;
		this.chunkTransport = chunkTransport;
		this.encryptedMiTreeRepo = encryptedMiTreeRepository;
		this.encryptedMiFileRepo = encryptedMiFileRepository;
		this.metadataUtil = metadataUtil;
		this.chunkEncryption = chunkEncryption;
		this.encryptedMetaMetaDataRepository = encryptedMetaMetaDataRepository;
		this.metaMetaDataHolder = metaMetaDataHolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#start()
	 */
	@Override
	public void startProcessing() {
		if (worker == null) {
			worker = new MetadataWorker(appSettingsDao, chunkTransport,
					incomingEvents, encryptedMiTreeRepo, encryptedMiFileRepo,  metadataUtil,
					metaMetaDataHolder, chunkEncryption);
			worker.start();
			log.info("Starting MetadataRepository");
		} else {
			log.debug("MetadataRepository already started.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.metadata.MetadataRepository#stop()
	 */
	@Override
	public void stopProcessing() {
		log.info("Stopping MetadataRepository...");
		worker.setActive(false);
		try {
			worker.join();
		} catch (InterruptedException e) {
			log.warn(e.getMessage());
		}
		log.info("MetadataRepository stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.metadata.MetadataRepository#addEvent(com.wlami
	 * .mibox.client.metadata.ObservedFilesystemEvent)
	 */
	@Override
	public void addEvent(ObservedFilesystemEvent observedFilesystemEvent) {
		if (!incomingEvents.add(observedFilesystemEvent)) {
			log.debug("ObservedFilesystemEvent not added - already existing");
		}
	}

}
