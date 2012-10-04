/**
 *     MiBox Server - folder synchronization backend
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
package com.wlami.mibox.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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

import com.wlami.mibox.server.data.Metadata;
import com.wlami.mibox.server.data.User;
import com.wlami.mibox.server.services.persistence.PersistenceProvider;
import com.wlami.mibox.server.util.HttpHeaderUtil;
import com.wlami.mibox.server.util.PersistenceUtil;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
@Path("/metadatamanager/{name}")
public class MetadataManager {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(MetadataManager.class);

	private EntityManagerFactory emf;
	private EntityManager em;

	/** This object is responsible for reading and writing the metadata. */
	private PersistenceProvider metadataPersistenceProvider;


	/**
	 * @param metadataPersistenceProvider
	 *            the metadataPersistenceProvider to set
	 */
	public void setMetadataPersistenceProvider(
			PersistenceProvider metadataPersistenceProvider) {
		this.metadataPersistenceProvider = metadataPersistenceProvider;
	}

	@Context
	ServletContext context;

	/** Default constructor */
	public MetadataManager() {
		String pu = PersistenceUtil.getPersistenceUnitName();
		emf = Persistence.createEntityManagerFactory(pu);
		em = emf.createEntityManager();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response loadMetadata(@PathParam("name") String name,
			@Context HttpHeaders headers) {

		// Check whether user is properly logged in
		User user = HttpHeaderUtil.getUserFromHttpHeaders(headers, em);
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		Metadata metadata = Metadata.getByName(name, em);
		// TODO evtl. check access
		if (metadata == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		byte[] data = metadataPersistenceProvider.retrieveFile(name);
		if (data == null) {
			log.error("There is a metadata entry in the db which could not "
					+ "be retrieved from the persistent storage! Name: [{}]",
					name);
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().header("Content-length", data.length).entity(data)
				.build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response saveMetadata(@PathParam("name") String name,
			@Context HttpHeaders headers, final InputStream inputStream)
					throws NoSuchAlgorithmException {
		User user = HttpHeaderUtil.getUserFromHttpHeaders(headers, em);
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		byte[] input;
		try {
			input = IOUtils.toByteArray(inputStream);
		} catch (IOException exception) {
			log.error("", exception);
			em.getTransaction().rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		Metadata metadata = Metadata.getByName(name, em);

		em.getTransaction().begin();

		if (metadata == null) {
			metadata = new Metadata(name);
		}
		try {
			metadataPersistenceProvider.persistFile(name, input);
		} catch (IOException e) {
			log.error("Could not persist metadata", e);
			em.getTransaction().rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		if (!user.getMetadatas().contains(metadata)) {
			user.getMetadatas().add(metadata);
		}
		em.persist(metadata);
		em.getTransaction().commit();

		return Response.ok().build();
	}
}
