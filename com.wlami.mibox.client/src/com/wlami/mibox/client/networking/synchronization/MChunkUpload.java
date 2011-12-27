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

import com.wlami.mibox.client.metadata.MChunk;

/**
 * This class defines a upload task which shall be executed by the
 * {@link TransportProvider}.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public class MChunkUpload {

	/**
	 * the MChunk to be uploaded,
	 */
	private MChunk mChunk;

	/**
	 * the callbackMethod which shall be executed when the upload is finished.
	 */
	private UploadCallback uploadCallback;

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

	/**
	 * @return the uploadCallback
	 */
	public UploadCallback getUploadCallback() {
		return uploadCallback;
	}

	/**
	 * @param uploadCallback
	 *            the uploadCallback to set
	 */
	public void setUploadCallback(UploadCallback uploadCallback) {
		this.uploadCallback = uploadCallback;
	}

}
