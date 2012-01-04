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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class ChunkManagerProxyResponseBuilder implements
		ChunkManagerResponseBuilder {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(getClass().getName());

	ChunkPersistenceProvider chunkPersistenceProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.server.services.chunk.ChunkManagerResponseBuilder#
	 * buildGetChunkResponse(java.lang.String)
	 */
	@Override
	public Response buildGetChunkResponse(String hash) {

		byte[] data = chunkPersistenceProvider.retrieveChunk(hash);

		if (data == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			return Response.ok().header("Content-Length", data.length)
					.entity(data).build();
		}
	}

	/**
	 * @param chunkPersistenceProvider
	 *            the chunkPersistenceProvider to set
	 */
	public void setChunkPersistenceProvider(
			ChunkPersistenceProvider chunkPersistenceProvider) {
		this.chunkPersistenceProvider = chunkPersistenceProvider;
	}

}
