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
 * This class contains data which is needed for the download of a
 * chunk/metadata.
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public abstract class TransportInfo {

	/**
	 * This field describes the name of the resource.<br/>
	 * <b>Examples:</b><br/>
	 * <ul>
	 * <li>hash for a data chunk</li>
	 * <li>uuid for a metadata file</li>
	 * <li>...</li>
	 * </ul>
	 */
	private String resourceName;

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName
	 *            the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}
