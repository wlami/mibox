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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.server.data.Metadata;
import com.wlami.mibox.server.data.User;
import com.wlami.mibox.server.services.metadata.MetadataPersistenceProvider;
import com.wlami.mibox.server.util.HttpHeaderUtil;
import com.wlami.mibox.server.util.PersistenceUtil;

/**
 * @author wladislaw
 *
 */
public class MetadataManager {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(MetadataManager.class);

	private EntityManagerFactory emf;
	private EntityManager em;

	/** This object is responsible for reading and writing the metadata. */
	private MetadataPersistenceProvider metadataPersistenceProvider;

	/**
	 * @param metadataPersistenceProvider
	 *            the metadataPersistenceProvider to set
	 */
	public void setMetadataPersistenceProvider(
			MetadataPersistenceProvider metadataPersistenceProvider) {
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
		Metadata metadata = null;
		metadata = (Metadata) em.createQuery(
				"SELECT m FROM Metadata m WHERE m.name = :name").setParameter(
						"name", name);
		// TODO evtl. check access
		if (metadata == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		byte[] data = metadataPersistenceProvider.retrieveMetadata(name);
		if (data == null) {
			log.error("There is a metadata entry in the db which could not "
					+ "be retrieved from the persistent storage! Name: [{}]",
					name);
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().header("Content-length", data.length).entity(data)
				.build();
	}
}
