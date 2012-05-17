/**
 *     MiBox Client - folder synchronization client
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
package com.wlami.mibox.client.networking.adapter;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * @author wladislaw
 *
 */
public class RestTransporter extends LowLevelTransporter {

	/** internal logger */
	private static final Logger log = LoggerFactory
			.getLogger(RestTransporter.class);

	/**
	 * Creates a new RestTramsporter
	 * 
	 * @param dataStoreUrl
	 *            This is the base url for the rest interface.
	 */
	public RestTransporter(String dataStoreUrl) {
		setDataStoreUrl(dataStoreUrl);
	}

	/**
	 * creates a rest client and builds uri. Furthermore http-auth is used
	 * 
	 * @param appSettings
	 * @param resourceName
	 * @return
	 */
	private WebResource getWebResource(String resourceName) {
		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.addFilter(new HTTPBasicAuthFilter("user", "user"));// TODO
		URI uri = null;
		try {
			uri = UriBuilder.fromUri(getDataStoreUrl() + resourceName).build();
		} catch (IllegalArgumentException e) {
			log.error("", e);
		}
		WebResource webResource = client.resource(uri);
		return webResource;
	}

	/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.adapter.Transporter#upload(java.lang.String, byte[])
	 */
	@Override
	public void upload(String name, byte[] content) {
		// upload it
		WebResource webResource = getWebResource(name);
		log.debug("Execute the HTTP PUT: " + webResource.getURI().toString());
		webResource.put(content);
	}

	/* (non-Javadoc)
	 * @see com.wlami.mibox.client.networking.adapter.Transporter#download(com.wlami.mibox.client.networking.adapter.TransportInfo)
	 */
	@Override
	public void download(TransportInfo transportInfo) {
		// TODO Auto-generated method stub

	}

}
