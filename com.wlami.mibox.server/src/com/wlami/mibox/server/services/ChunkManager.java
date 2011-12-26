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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.wlami.mibox.server.data.ChunkHddMapping;

@Path("/chunkmanager/{id}")
public class ChunkManager {

	private static final String PERSISTENCE_UNIT_NAME = "com.wlami.mibox.server";
	EntityManagerFactory emf;
	EntityManager em;

	@Context
	ServletContext context;

	public ChunkManager() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response loadChunk(@PathParam("id") String id)
			throws FileNotFoundException {
		// Get the filename from db
		ChunkHddMapping chunkHddMapping;
		try {
			// Get the filename from db
			chunkHddMapping = (ChunkHddMapping) em
					.createQuery(
							"SELECT c from ChunkHddMapping c WHERE c.id = :id")
					.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return Response.status(Status.NOT_FOUND).build();
		}

		// Get the file
		final String storagePath = context
				.getInitParameter("chunk-storage-path");
		File file = new File(storagePath, chunkHddMapping.getValue());

		// return 404 if file doesn't exist
		if (!file.exists()) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		System.out.println(file.getAbsolutePath());

		// return the file
		ResponseBuilder responseBuilder = Response.ok();
		InputStream inputStream = new BufferedInputStream(new FileInputStream(
				file));
		responseBuilder.header("Content-Length", file.length());
		return responseBuilder.entity(inputStream).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response saveChunk(final InputStream inputStream,
			@HeaderParam(HttpHeaders.CONTENT_LENGTH) final long contentLength)
			throws IOException, NoSuchAlgorithmException {
		System.out.print("New Message. Length [" + contentLength + "] => ");

		// Get path for output file
		final String storagePath = context
				.getInitParameter("chunk-storage-path");
		UUID uuid = UUID.randomUUID();
		FileOutputStream out = new FileOutputStream(new File(storagePath,
				uuid.toString()));
		int receivedByte;
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		// receive the data and compute the hash
		while ((receivedByte = inputStream.read()) != -1) {
			out.write(receivedByte);
			messageDigest.update((byte) receivedByte);
		}
		out.flush();
		out.close();

		// get the file hash
		byte[] digest = messageDigest.digest();
		String hash = digestToString(digest);
		System.out.println(out.toString() + "\n hash : " + hash);

		// persist the chunk-mapping to db
		em.getTransaction().begin();
		ChunkHddMapping chunkHddMapping = new ChunkHddMapping();
		chunkHddMapping.setId(hash);
		chunkHddMapping.setValue(uuid.toString());
		em.persist(chunkHddMapping);
		em.getTransaction().commit();

		// respond with the hash of the created file
		ResponseBuilder responseBuilder = Response.ok();
		return responseBuilder.entity(hash).build();
	}

	/**
	 * Creates a String from a MessageDigest result.
	 * 
	 * @param input
	 * @return
	 */
	private static String digestToString(byte[] input) {
		Formatter formatter = new Formatter();
		for (byte b : input) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}