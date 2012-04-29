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
package com.wlami.mibox.client.networking.synchronization;

import java.io.File;

import com.wlami.mibox.client.metadata.MChunk;

/**
 * This class defines an upload task which shall be executed by the
 * {@link TransportProvider}.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class MChunkUpload implements Comparable<MChunkUpload> {

	/**
	 * the MChunk to be uploaded,
	 */
	private MChunk mChunk;

	/** a file reference which contains the {@link #mChunk}. */
	private File file;

	/**
	 * the callbackMethod which shall be executed when the upload is finished.
	 */
	private UploadCallback uploadCallback;

	/**
	 * Default constructor.
	 */
	public MChunkUpload() {
	}

	/**
	 * Convenience constructor which sets the attributes.
	 * 
	 * @param mChunk
	 *            The value {@link #mChunk} shall be set to.
	 * @param file
	 *            The value {@link #file} shall be set to.
	 * @param uploadCallback
	 *            THe value {@link #uploadCallback} shall be set to.
	 */
	public MChunkUpload(MChunk mChunk, File file, UploadCallback uploadCallback) {
		this.mChunk = mChunk;
		this.file = file;
		this.uploadCallback = uploadCallback;
	}

	/**
	 * @return the mChunk
	 */
	public MChunk getMChunk() {
		return this.mChunk;
	}

	/**
	 * @return the {@link #file}
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @param file
	 *            the {@link #file} to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @param mChunk
	 *            the mChunk to set
	 */
	public void setMChunk(MChunk mChunk) {
		this.mChunk = mChunk;
	}

	/**
	 * @return the uploadCallback
	 */
	public UploadCallback getUploadCallback() {
		return this.uploadCallback;
	}

	/**
	 * @param uploadCallback
	 *            the uploadCallback to set
	 */
	public void setUploadCallback(UploadCallback uploadCallback) {
		this.uploadCallback = uploadCallback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MChunkUpload o) {
		return this.mChunk.getDecryptedChunkHash().compareTo(
				o.getMChunk().getDecryptedChunkHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		MChunkUpload o = (MChunkUpload) obj;
		return this.mChunk.getEncryptedChunkHash().equals(
				o.getMChunk().getEncryptedChunkHash());
	}

}
