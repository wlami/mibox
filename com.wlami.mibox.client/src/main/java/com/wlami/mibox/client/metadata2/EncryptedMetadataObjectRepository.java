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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.wlami.mibox.client.application.AppFolders;
import com.wlami.mibox.client.networking.adapter.LowLevelTransporter;
import com.wlami.mibox.client.networking.adapter.TransportInfo;
import com.wlami.mibox.client.networking.synchronization.EncryptedMetadataUploadRequest;
import com.wlami.mibox.client.networking.synchronization.TransportCallback;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;

/**
 * @author Stefan baust
 * @author wladislaw Mitzel
 * 
 */
public class EncryptedMetadataObjectRepository<U extends EncryptedAbstractObject<?>> {

	/**
	 * reference to a transporter which can handle uploads of encrypted mi trees
	 */
	private final TransportProvider<EncryptedMetadataUploadRequest> transportProvider;

	/**
	 * the retrieval operations will return objects of this class.
	 */
	Class<U> metadataObjectClass;

	/**
	 * reference to a lowLevel transporter. it is used for synchronous retrieval
	 * of remote files.
	 */
	private final LowLevelTransporter lowLevelTransporter;

	/** interal logger */
	private static Logger log = LoggerFactory
			.getLogger(EncryptedMetadataObjectRepository.class);

	/**
	 * Creates a new {@link EncryptedMetadataObjectRepository}.
	 * 
	 * @param transportProvider
	 *            A reference to a transport provider which is used for uploads
	 *            and downloads.
	 * @param lowLevelTransporter
	 *            sets {@link #lowLevelTransporter}
	 */
	public EncryptedMetadataObjectRepository(Class<U> metadataObjectClass,
			TransportProvider<EncryptedMetadataUploadRequest> transportProvider,
			LowLevelTransporter lowLevelTransporter) {
		this.metadataObjectClass = metadataObjectClass;
		this.transportProvider = transportProvider;
		this.lowLevelTransporter = lowLevelTransporter;
	}

	public U loadRemoteEncryptedMetadata(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("filename must not be null");
		}
		log.debug("loading remote encrypted metadata [{}]", fileName);
		TransportInfo transportInfo = new TransportInfo(fileName);
		U encryptedMetadata = null;
		try {
			byte[] result = lowLevelTransporter.download(transportInfo);
			log.debug("loading done. Creating new instance of [{}] now",
					metadataObjectClass);
			encryptedMetadata = metadataObjectClass.newInstance();
			encryptedMetadata.setContent(result);
			encryptedMetadata.setName(transportInfo.getResourceName());
			log.debug("Setting name [{}] and content [{}] of metadata",
					encryptedMetadata.getName(), encryptedMetadata.getContent());
		} catch (UniformInterfaceException | InstantiationException
				| IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return encryptedMetadata;
	}

	/**
	 * Loads an encrypted MiTree from local file system.
	 * 
	 * @param fileName
	 *            File name of the MiTree
	 * @return <code>null</code> if there is an error<br/>
	 *         {@link EncryptedMiTree} otherwise
	 */
	public U loadEncryptedMetadata(String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] content = new byte[(int) file.length()];
			fileInputStream.read(content);
			U encryptedMetadata = this.metadataObjectClass.newInstance();
			encryptedMetadata.setContent(content);
			encryptedMetadata.setName(fileName);
			return encryptedMetadata;
		} catch (IOException | InstantiationException | IllegalAccessException e) {
			log.info("Error during load of encrypted metadata [{}]",
					file.getName());
			return null;
		}
	}

	/**
	 * Save encrypted metadata to local file system
	 * 
	 * @param encryptedMetadata
	 *            The metadata which shall be saved.
	 * @param fileName
	 *            filename of the metadata.
	 */
	public void saveEncryptedMetadata(U encryptedMetadata,
			String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(encryptedMetadata.getContent());
			transportProvider.addChunkUpload(createUploadRequest(file));
		} catch (IOException e) {
			log.error("Error during save of encrypted MiTree", e);
		}
	}

	/**
	 * Creates a new upload request for encrypted metadata
	 * 
	 * @param file
	 *            a file representing the encrypted metadata in the filesystem.
	 * @return An upload request containing a file and a callback.
	 */
	protected EncryptedMetadataUploadRequest createUploadRequest(File file) {
		EncryptedMetadataUploadRequest request = new EncryptedMetadataUploadRequest(
				this);
		request.setFile(file);
		request.setUploadCallback(new TransportCallback() {
			@Override
			public void transportCallback(Map<String, Object> parameter) {
				// TODO Auto-generated method stub

			}
		});
		return request;
	}
}
