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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MFile {

	/**
	 * Default size of chunks.
	 */
	protected static final int DEFAULT_CHUNK_SIZE = 1024 * 1024;

	/**
	 * file name.
	 */
	private String name;

	/**
	 * Defines the folder in which this file is contained.
	 */
	@JsonBackReference
	private MFolder folder;

	/**
	 * Contains a ordered list of file chunks.
	 */
	@JsonManagedReference
	private List<MChunk> chunks = new ArrayList<MChunk>();

	/**
	 * This is the hash-value of the non-encrypted file. It is used for
	 * identification of non-encrypted files.
	 */
	private String fileHash;

	/**
	 * Determines the size of the file chunks. Measured in byte.
	 */
	private int chunkSize = DEFAULT_CHUNK_SIZE;

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
	public int getChunkSize() {
		return chunkSize;
	}

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

}
