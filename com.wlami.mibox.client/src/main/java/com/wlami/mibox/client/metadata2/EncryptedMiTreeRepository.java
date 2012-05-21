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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppFolders;
import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.networking.synchronization.EncryptedMiTreeUploadRequest;
import com.wlami.mibox.client.networking.synchronization.TransportProvider;
import com.wlami.mibox.client.networking.synchronization.UploadCallback;

/**
 * @author Stefan baust
 * @author wladislaw Mitzel
 * 
 */
public class EncryptedMiTreeRepository {

	/**
	 * reference to a transporter which can handle uploads of encrypted mi trees
	 */
	private final TransportProvider<EncryptedMiTreeUploadRequest> transportProvider;

	/** interal logger */
	private static Logger log = LoggerFactory
			.getLogger(EncryptedMiTreeRepository.class);

	/**
	 * Creates a new {@link EncryptedMiTreeRepository}.
	 * 
	 * @param transportProvider
	 *            A reference to a transport provider which is used for uploads
	 *            and downloads.
	 */
	public EncryptedMiTreeRepository(
			TransportProvider<EncryptedMiTreeUploadRequest> transportProvider) {
		this.transportProvider = transportProvider;
	}

	/**
	 * Loads an encrypted MiTree from local file system.
	 * 
	 * @param fileName
	 *            File name of the MiTree
	 * @return <code>null</code> if there is an error<br/>
	 *         {@link EncryptedMiTree} otherwise
	 */
	public EncryptedMiTree loadEncryptedMiTree(String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] content = new byte[(int) file.length()];
			fileInputStream.read(content);
			return new EncryptedMiTree(file.getName(), content);
		} catch (IOException e) {
			log.info("Error during load of encrypted MiTree [{}]",
					file.getName());
			return null;
		}
	}

	/**
	 * Save an encrypted MiTree to local file system
	 * 
	 * @param encryptedMiTree
	 *            The tree which shall be saved.
	 * @param fileName
	 *            filename of the MiTree.
	 */
	public void saveEncryptedMiTree(EncryptedMiTree encryptedMiTree,
			String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(encryptedMiTree.getContent());
			transportProvider.addChunkUpload(createUploadRequest(file));
		} catch (IOException e) {
			log.error("Error during save of encrypted MiTree", e);
		}
	}

	/**
	 * Creates a new upload request for an encryptedMiTree which is located
	 * 
	 * @param file
	 *            a file representing the encryptedMiTree in the filesystem.
	 * @return An upload request containing a file and a callback.
	 */
	protected EncryptedMiTreeUploadRequest createUploadRequest(File file) {
		EncryptedMiTreeUploadRequest request = new EncryptedMiTreeUploadRequest(this);
		request.setFile(file);
		request.setUploadCallback(new UploadCallback() {
			@Override
			public void uploadCallback(MChunk mChunk, String result) {
				log.debug("Execute the callback");
				// TODO
			}
		});
		return request;
	}
}
