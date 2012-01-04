/**
 *     MiBox Server - folder synchronization backend
 *  Copyright (C) 2012 Wladislaw Mitzel
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
package com.wlami.mibox.server.services.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class FilesystemChunkPersistenceProvider implements
		ChunkPersistenceProvider {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(getClass().getName());

	/** This path is used to store the chunks */
	String storagePath;

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
	 * com.wlami.mibox.server.services.chunk.ChunkPersistenceProvider#retrieveChunk
	 * (java.lang.String)
	 */
	@Override
	public byte[] retrieveChunk(String hash) {
		File file = new File(storagePath, hash);
		try {
			log.debug("reading chunk " + hash);
			FileInputStream fis = new FileInputStream(file);
			// WARNING! Can only handle chunk sizes up to 2 mb!
			int fileLength = (int) file.length();
			byte[] data = new byte[fileLength];
			fis.read(data, 0, fileLength);
			return data;
		} catch (IOException e) {
			log.error("", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.server.services.chunk.ChunkPersistenceProvider#persistChunk
	 * (java.lang.String, byte[])
	 */
	@Override
	public void persistChunk(String hash, byte[] data) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(storagePath, hash));
		fos.write(data);
		fos.close();
	}

}
