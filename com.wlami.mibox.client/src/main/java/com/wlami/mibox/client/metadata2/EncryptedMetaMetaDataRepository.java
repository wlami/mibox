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
package com.wlami.mibox.client.metadata2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.wlami.mibox.client.application.AppFolders;
import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.networking.adapter.RestTransporter;
import com.wlami.mibox.client.networking.adapter.TransportInfo;

/**
 * @author stefan baust
 * @author wladislaw mitzel
 * 
 */
public class EncryptedMetaMetaDataRepository {

	/** internal logger */
	public static final Logger log = LoggerFactory
			.getLogger(EncryptedMetaMetaDataRepository.class);

	/** reference to a transporter */
	private RestTransporter restTransporter;

	/**
	 * @param restTransporter
	 *            the restTransporter to set
	 */
	public void setRestTransporter(RestTransporter restTransporter) {
		this.restTransporter = restTransporter;
	}

	/**
	 * 
	 * @param appSettings
	 * @return
	 */
	protected EncryptedMetaMetaData retrieveMetaMetaData(AppSettings appSettings) {
		TransportInfo transportInfo = new TransportInfo();
		transportInfo.setResourceName("");
		EncryptedMetaMetaData encryptedMetaMetaData = null;
		try {
			byte[] result = restTransporter.download(transportInfo);
			encryptedMetaMetaData = new EncryptedMetaMetaData();
			encryptedMetaMetaData.setContent(result);
			encryptedMetaMetaData.setName(transportInfo.getResourceName());
		} catch (UniformInterfaceException e) {
			// The GET request had an error. lets check it!
			ClientResponse response = e.getResponse();
			if (Response.Status.fromStatusCode(response.getStatus()) == Response.Status.NOT_FOUND) {
				log.debug(
						"There is no meta meta data on the server for user [{}]",
						appSettings.getUsername());
				encryptedMetaMetaData = loadMetaMetaDataLocal(appSettings
						.getUsername());
			} else {
				log.warn("Could not retrieve meta meta data", e.getMessage());
			}
		}
		return encryptedMetaMetaData;
	}

	/**
	 * @param username
	 * @return
	 */
	protected EncryptedMetaMetaData loadMetaMetaDataLocal(String username) {
		log.debug("Loading meta meta data from local file system");
		File file = new File(AppFolders.getConfigFolder(), username);
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] content = new byte[(int) file.length()];
			fis.read(content);
			log.debug("Read [{}] bytes for meta meta data", content.length);
			EncryptedMetaMetaData data = new EncryptedMetaMetaData();
			data.setName(username);
			data.setContent(content);
			return data;
		} catch (IOException e) {
			log.error("Could not load local meta meta data! [{}]",
					e.getMessage());
			return null;
		}
	}

	/**
	 * Write the meta meta data to the local config folder.
	 * 
	 * @param data
	 *            The encrypted meta meta data.
	 */
	protected void persistMetaMetaDataLocal(EncryptedMetaMetaData data) {
		log.debug("persisting meta meta data locally");
		File file = new File(AppFolders.getConfigFolder(), data.getName());
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(data.getContent());
			log.debug("Written [{}] bytes of meta meta data",
					data.getContent().length);
		} catch (IOException e) {
			log.error("could not persist local meta meta data! [{}]",
					e.getMessage());
		}
	}

	/**
	 * persist the meta meta data. First writes it to the local filesystem and
	 * then uploads it to the server
	 * 
	 * @param The
	 *            encrypted meta meta data.
	 */
	public void persistMetaMetaData(EncryptedMetaMetaData data) {
		log.debug("Persisting meta meta data");
		persistMetaMetaData(data);
		try {
			restTransporter.upload(data.getName(), data.getContent());
		} catch (UniformInterfaceException exception) {
			log.error("could not persist meta meta data to server [{}]",
					exception.getMessage());
			throw exception;
		}
	}

}
