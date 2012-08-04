/**
 *  Copyright (C) 2012 wladislaw
 *     MiBox Client - folder synchronization client
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

import java.util.Map;

import com.wlami.mibox.client.metadata.MChunk;

/**
 * @author wladislaw
 *
 */
public class MChunkUploadCallback implements UploadCallback {

	MChunk uploadedChunk;

	MChunkUploadCallback(MChunk uploadedChunk) {
		// TODO uploadedChunk.getMFile().get
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.UploadCallback#
	 * uploadCallback(java.util.Map)
	 */
	@Override
	public void uploadCallback(Map<String, Object> parameter) {
		// TODO Auto-generated method stub

	}

}
