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

import java.util.List;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MFile {

	/**
	 * Default size of chunks.
	 */
	protected static final long DEFAULT_CHUNK_SIZE = 1024L * 1024L;

	/**
	 * Defines the folder in which this file is contained.
	 */
	private MFolder folder;

	/**
	 * Contains a ordered list of file chunks.
	 */
	private List<MChunk> chunks;

	/**
	 * This is the hash-value of the non-encrypted file. It is used for
	 * identification of non-encrypted files.
	 */
	private String fileHash;

	/**
	 * Determines the size of the file chunks. Measured in byte.
	 */
	private long chunkSize = DEFAULT_CHUNK_SIZE;

	/**
	 * @return the folder
	 */
	public MFolder getFolder() {
		return folder;
	}

	/**
	 * @param folder
	 *            the folder to set
	 */
	public void setFolder(MFolder folder) {
		this.folder = folder;
	}

	/**
	 * @return the chunks
	 */
	public List<MChunk> getChunks() {
		return chunks;
	}

	/**
	 * @param chunks
	 *            the chunks to set
	 */
	public void setChunks(List<MChunk> chunks) {
		this.chunks = chunks;
	}

	/**
	 * @return the fileHash
	 */
	public String getFileHash() {
		return fileHash;
	}

	/**
	 * @param fileHash
	 *            the fileHash to set
	 */
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	/**
	 * @return the chunkSize
	 */
	public long getChunkSize() {
		return chunkSize;
	}

}
