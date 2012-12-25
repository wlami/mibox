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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.metadata.DecryptedMiFile;

/**
 * This class represents a folder in the metadata.
 * 
 * @author Stefan Baust
 * @author Wladislaw Mitzel
 * 
 */
public class DecryptedMiTree extends
 DecryptedAbstractObject<EncryptedMiTree> {

	/**
	 * 
	 */
	public DecryptedMiTree() {
		super(EncryptedMiTree.class);
	}

	/** internal logger */
	Logger log = LoggerFactory.getLogger(DecryptedMiTree.class);

	/** name of this folder */
	private String folderName;

	private Map<String, EncryptedMetadataInformation> subfolder = new HashMap<>();

	private Map<String, EncryptedMetadataInformation> files = new HashMap<>();

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
	public Map<String, EncryptedMetadataInformation> getSubfolder() {
		return subfolder;
	}

	/**
	 * @param subfolder the subfolder to set
	 */
	public void setSubfolder(Map<String, EncryptedMetadataInformation> subfolder) {
		this.subfolder = subfolder;
	}

	/**
	 * @return the files
	 */
	public Map<String, EncryptedMetadataInformation> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(Map<String, EncryptedMetadataInformation> files) {
		this.files = files;
	}

}
