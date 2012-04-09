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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.metadata.MFile;
import com.wlami.mibox.core.encryption.AesEncryption;

/**
 * This class represents a folder in the metadata.
 * 
 * @author Stefan Baust
 * @author Wladislaw Mitzel
 * 
 */
public class DecryptedMiTree {
	
	/** internal logger */
	Logger log = LoggerFactory.getLogger(DecryptedMiTree.class);

	/** name of this folder */
	private String folderName;

	List<EncryptedMiTreeInformation> subfolder = new ArrayList<>();

	List<MFile> files = new ArrayList<>();

	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * @param folderName
	 *            the folderName to set
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	

	/**
	 * @return the subfolder
	 */
	public List<EncryptedMiTreeInformation> getSubfolder() {
		return subfolder;
	}

	/**
	 * @param subfolder the subfolder to set
	 */
	public void setSubfolder(List<EncryptedMiTreeInformation> subfolder) {
		this.subfolder = subfolder;
	}

	/**
	 * @return the files
	 */
	public List<MFile> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<MFile> files) {
		this.files = files;
	}

	/**
	 * encrypt this MiTree with the given key and iv.
	 * @param key Key to use for encryption
	 * @param iv IV to use for encryption
	 * @return an {@link EncryptedMiTree} representing this MiTree
	 */
	public EncryptedMiTree encrypt(byte[] key, byte[] iv) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			byte[] data = objectMapper.writeValueAsBytes(this);
			byte[] encrypted = AesEncryption.crypt(true, data, iv, key);
			return new EncryptedMiTree(encrypted);
		} catch ( IOException | CryptoException e) {
			log.error("Error during encryption of MiTree",e);
			return null;
		}
	}

}
