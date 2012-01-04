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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

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

import com.wlami.mibox.core.util.HashUtil;
import com.wlami.mibox.server.data.Chunk;
import com.wlami.mibox.server.data.User;
import com.wlami.mibox.server.services.chunk.ChunkManagerResponseBuilder;
import com.wlami.mibox.server.util.HttpHeaderUtil;
import com.wlami.mibox.server.util.PersistenceUtil;

@Path("/chunkmanager/{hash}")
public class ChunkManager {

	EntityManagerFactory emf;
	EntityManager em;

	@Context
	ServletContext context;

	/** this reference is used to create the responses */
	ChunkManagerResponseBuilder chunkManagerResponseBuilder;

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
		User user = getUserFromHttpHeaders(headers);
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
			throws IOException, NoSuchAlgorithmException {
		User user = getUserFromHttpHeaders(headers);
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(user.getUsername());
		// Get path for output file
		final String storagePath = context
				.getInitParameter("chunk-storage-path");
		UUID uuid = UUID.randomUUID();
		File outputFile = new File(storagePath, uuid.toString());
		FileOutputStream out = new FileOutputStream(outputFile);
		int receivedByte;
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		// receive the data and compute the hash
		while ((receivedByte = inputStream.read()) != -1) {
			out.write(receivedByte);
			messageDigest.update((byte) receivedByte);
		}
		out.flush();
		out.close();

		String newHash = HashUtil.digestToString(messageDigest.digest());
		System.out.println(out.toString() + "\n hash : " + newHash);

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
			outputFile.renameTo(new File(storagePath, newHash));
			// Associate chunk with user

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
	 * @param headers
	 * @return
	 */
	protected User getUserFromHttpHeaders(HttpHeaders headers) {
		try {
			return (User) em
					.createQuery(
							"SELECT u from User u WHERE u.username = :username")
					.setParameter("username",
							HttpHeaderUtil.getAuthorization(headers)[0])
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}