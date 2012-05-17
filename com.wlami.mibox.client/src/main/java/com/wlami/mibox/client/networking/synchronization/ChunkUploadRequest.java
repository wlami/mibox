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
import java.io.IOException;

import org.bouncycastle.crypto.CryptoException;

import com.wlami.mibox.client.metadata.MChunk;
import com.wlami.mibox.client.networking.encryption.ChunkEncryption;
import com.wlami.mibox.client.networking.transporter.Transportable;

/**
 * This class defines an upload task which shall be executed by the
 * {@link TransportProvider}.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class ChunkUploadRequest extends UploadRequest<ChunkUploadRequest> {

	/**
	 * the MChunk to be uploaded,
	 */
	private MChunk mChunk;

	/**
	 * reference to something which can encrypt chunks.
	 */
	private ChunkEncryption chunkEncryption;

	/**
	 * Default constructor.
	 */
	public ChunkUploadRequest() {
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
	public ChunkUploadRequest(MChunk mChunk, File file, UploadCallback uploadCallback, ChunkEncryption chunkEncryption) {
		this.mChunk = mChunk;
		this.file = file;
		this.uploadCallback = uploadCallback;
		this.chunkEncryption = chunkEncryption;
	}

	/**
	 * @return the mChunk
	 */
	public MChunk getMChunk() {
		return mChunk;
	}


	/**
	 * @param mChunk
	 *            the mChunk to set
	 */
	public void setMChunk(MChunk mChunk) {
		this.mChunk = mChunk;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ChunkUploadRequest o) {
		return mChunk.getDecryptedChunkHash().compareTo(
				o.getMChunk().getDecryptedChunkHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		ChunkUploadRequest o = (ChunkUploadRequest) obj;
		return mChunk.getEncryptedChunkHash().equals(
				o.getMChunk().getEncryptedChunkHash());
	}

	/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.synchronization.UploadRequest#getTransportable()
	 */
	@Override
	public Transportable getTransportable() throws IOException, CryptoException {
		return chunkEncryption.encryptChunk(mChunk, file);
	}

}
