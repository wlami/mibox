/**
 *     MiBox Server - folder synchronization backend
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
package com.wlami.mibox.server.services.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public class FileSystemPersistenceProvider implements PersistenceProvider {

	/** internal logger */
	public static final Logger log = LoggerFactory
			.getLogger(FileSystemPersistenceProvider.class);

	/**
	 * this name is used for logging. It is possible to instantiate several
	 * {@link FileSystemPersistenceProvider}. This name can be used to
	 * distinguish them in the logs.
	 */
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the storagePath
	 */
	public String getStoragePath() {
		return storagePath;
	}

	/** This path is used to store the chunks */
	private String storagePath;

	/**
	 * @param storagePath
	 *            the storagePath to set
	 */
	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.server.services.persistence.PersistenceProvider#retrieveFile
	 * (java.lang.String)
	 */
	@Override
	public byte[] retrieveFile(String name) {
		File file = new File(storagePath, name);
		try {
			log.debug("reading chunk " + name);
			FileInputStream fis = new FileInputStream(file);
			// WARNING! Can only handle chunk sizes up to 2 mb!
			int fileLength = (int) file.length();
			byte[] data = new byte[fileLength];
			fis.read(data, 0, fileLength);
			fis.close();
			return data;
		} catch (IOException e) {
			log.error("", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wlami.mibox.server.services.persistence.PersistenceProvider#persistFile(java.lang.String, byte[])
	 */
	@Override
	public void persistFile(String name, byte[] content) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(storagePath, name));
		fos.write(content);
		fos.close();
	}

}
