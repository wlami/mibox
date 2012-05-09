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


/**
 * This interface provides transport adapters for different storage options.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust *
 */
public abstract class Transporter {

	/**
	 * Uploads an entity to the target storage.
	 * 
	 * @param transportInfo
	 *            Data object containing information on the target storage.
	 */
	public abstract void upload(String name, byte[] content);

	/**
	 * Downloads an entity from the target storage.
	 * 
	 * @param transportInfo
	 *            Data object containing information on the target storage.
	 */
	public abstract void download(TransportInfo transportInfo);

	/**
	 * This variable contains the uniform resource name of the data store.<br/>
	 * This may be the MiBox Server URL or the ftp-folder for another
	 * implemenation.<br/>
	 * <b>Has to be set for the transporter to work properly!</b>
	 */
	private String dataStoreUrl;

	/**
	 * @return the dataStoreUrl
	 */
	public String getDataStoreUrl() {
		return dataStoreUrl;
	}

	/**
	 * @param dataStoreUrl
	 *            the dataStoreUrl to set
	 */
	public void setDataStoreUrl(String dataStoreUrl) {
		this.dataStoreUrl = dataStoreUrl;
	}

}
