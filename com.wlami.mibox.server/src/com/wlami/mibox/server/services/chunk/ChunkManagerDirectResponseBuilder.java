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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class ChunkManagerDirectResponseBuilder implements
		ChunkManagerResponseBuilder {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(getClass().getName());

	/** This path is used to store the chunks */
	String storagePath = "c:\\temp\\mibox";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.server.services.chunk.ChunkManagerResponseBuilder#
	 * buildGetChunkResponse(java.lang.String)
	 */
	@Override
	public Response buildGetChunkResponse(String hash) {
		// Get the file
		File file = new File(storagePath, hash);

		// return 404 if file doesn't exist
		if (!file.exists()) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}

		// return the file
		ResponseBuilder responseBuilder = Response.ok();
		InputStream inputStream;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			log.error("", e);
			return Response.status(Status.NOT_FOUND).build();
		}
		responseBuilder.header("Content-Length", file.length());
		return responseBuilder.entity(inputStream).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.server.services.chunk.ChunkManagerResponseBuilder#
	 * buildPutChunkResponse(java.lang.String, java.io.InputStream)
	 */
	@Override
	public Response buildPutChunkResponse(String hash, InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param storagePath
	 *            the storagePath to set
	 */
	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

}
