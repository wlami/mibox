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
 * This callback interfaces defines a processing method which is called when the
 * upload process is finished.
 * 
 * @author Wladislaw Mitzel
 */
public interface UploadCallback {

	/**
	 * This method gets called when the upload method has finished.
	 * 
	 * @param mChunk
	 *            the chunk which got uploaded.
	 * @param result
	 *            the hash result from encrypted chunk.
	 */
	public void uploadCallback(MChunk mChunk, String result);

}
