/**
 *     MiBox Server - folder synchronization backend
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
package com.wlami.mibox.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.core.util.HashUtil;
import com.wlami.mibox.server.data.Chunk;
import com.wlami.mibox.server.data.User;
import com.wlami.mibox.server.services.chunk.ChunkManagerResponseBuilder;
import com.wlami.mibox.server.services.persistence.PersistenceProvider;
import com.wlami.mibox.server.util.HttpHeaderUtil;
import com.wlami.mibox.server.util.PersistenceUtil;

@Path("/chunkmanager/{hash}")
public class ChunkManager {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(getClass().getName());

	EntityManagerFactory emf;
	EntityManager em;

	@Context
	ServletContext context;

	/** this reference is used to create the responses */
	ChunkManagerResponseBuilder chunkManagerResponseBuilder;

	/** this reference is used to persist and retrieve the chunk data */
	PersistenceProvider chunkPersistenceProvider;

	/** Default constructor */
	public ChunkManager() {
		String pu = PersistenceUtil.getPersistenceUnitName();
		emf = Persistence.createEntityManagerFactory(pu);
		em = emf.createEntityManager();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response loadChunk(@PathParam("hash") String hash,
			@Context HttpHeaders headers) {

		// Check whether user is properly logged in
		User user = HttpHeaderUtil.getUserFromHttpHeaders(headers, em);
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		// Retrieve the chunk from db
		Chunk chunk;
		try {
			chunk = (Chunk) em
					.createQuery("SELECT c from Chunk c WHERE c.hash = :hash")
					.setParameter("hash", hash).getSingleResult();
			// Check whether this chunk belongs to user
			if (!user.getChunks().contains(chunk)) {
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch (NoResultException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		// Everything seems ok. Create the response
		return chunkManagerResponseBuilder.buildGetChunkResponse(chunk
				.getHash());
	}

	@PUT
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response saveChunk(@PathParam("hash") String hash,
			@Context HttpHeaders headers, final InputStream inputStream)
					throws NoSuchAlgorithmException {
		User user = HttpHeaderUtil.getUserFromHttpHeaders(headers, em);
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		byte[] input;
		try {
			input = IOUtils.toByteArray(inputStream);
		} catch (IOException e1) {
			log.error("", e1);
			em.getTransaction().rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		String newHash = HashUtil.calculateSha256(input);

		System.out.println("\n hash : " + newHash);

		if (!hash.equals(newHash)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Chunk chunk;
		try {
			chunk = (Chunk) em
					.createQuery("SELECT c from Chunk c WHERE c.hash = :hash")
					.setParameter("hash", hash).getSingleResult();
		} catch (NoResultException e) {
			chunk = null;
		}

		em.getTransaction().begin();
		if (chunk == null) {
			chunk = new Chunk(newHash);
			try {
				chunkPersistenceProvider.persistFile(newHash, input);
			} catch (IOException e) {
				log.error("", e);
				em.getTransaction().rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			chunk.setLastAccessed(new Date());
		}
		if (!user.getChunks().contains(chunk)) {
			user.getChunks().add(chunk);
		}
		em.persist(chunk);
		em.getTransaction().commit();

		return Response.ok().build();
	}

	/**
	 * @param chunkManagerResponseBuilder
	 *            the chunkManagerResponseBuilder to set
	 */
	public void setChunkManagerResponseBuilder(
			ChunkManagerResponseBuilder chunkManagerResponseBuilder) {
		this.chunkManagerResponseBuilder = chunkManagerResponseBuilder;
	}

	/**
	 * @param chunkPersistenceProvider
	 *            the chunkPersistenceProvider to set
	 */
	public void setChunkPersistenceProvider(
			PersistenceProvider chunkPersistenceProvider) {
		this.chunkPersistenceProvider = chunkPersistenceProvider;
	}



}