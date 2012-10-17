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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONArray;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.server.data.User;
import com.wlami.mibox.server.util.HttpHeaderUtil;

/**
 * @author wladislaw
 * 
 */
@Path("/metadatanotifier/{datetime}")
public class MetadataNotifier {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	/** internal logger */
	public static final Logger log = LoggerFactory.getLogger(MetadataNotifier.class);

	/**
	 * Get an array of all metadata filenames which have been updated since the
	 * specified datetime. Used for synchronization of metadata.
	 * 
	 * @param datetimeAsString
	 *            Only names of metadata files which have been updated since
	 *            this datetime will be returned.
	 * @return An array of new updated metadata filenames.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listUpdatedMetadataSince(@PathParam("datetime") String datetimeAsString,
			@Context HttpHeaders headers) {
		// Check whether user is properly logged in
		User user = HttpHeaderUtil.getUserFromHttpHeaders(headers, em);
		if (user == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}

		DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.basicDateTime();
		DateTime datetime = null;
		try {
			datetime = dateTimeFormatter.parseDateTime(datetimeAsString);
		} catch (IllegalArgumentException e) {
			log.warn("listUpdatedMetadataSince: Date time format not recognized!", e.getMessage());
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Could not parse date")
					.build());
		}
		List<String> names = user.getByLastUpdatedSince(datetime, em);
		return new JSONArray(names);
	}
}
