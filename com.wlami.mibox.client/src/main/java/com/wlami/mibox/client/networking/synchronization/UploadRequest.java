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
package com.wlami.mibox.client.networking.synchronization;

import java.io.File;

import com.wlami.mibox.client.networking.transporter.Transportable;

/**
 * @author wladislaw
 *
 */
public abstract class UploadRequest<T extends UploadRequest<?>> implements
Comparable<T> {

	/** a file reference which contains the {@link #mChunk}. */
	protected File file;

	/**
	 * the callbackMethod which shall be executed when the upload is finished.
	 */
	protected UploadCallback uploadCallback;

	/**
	 * @return the {@link #file}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the {@link #file} to set
	 */
	public void setFile(File file) {
		this.file = file;
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

	public abstract Transportable getTransportable() throws Exception;

}
