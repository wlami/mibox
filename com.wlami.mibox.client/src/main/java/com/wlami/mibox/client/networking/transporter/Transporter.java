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
package com.wlami.mibox.client.networking.transporter;

import com.wlami.mibox.client.networking.adapter.LowLevelTransporter;
import com.wlami.mibox.client.networking.adapter.TransportInfo;

/**
 * @author wladislaw
 *
 */
public class Transporter {
	protected final LowLevelTransporter transporter;

	/**
	 * 
	 * @param transporter
	 */
	public Transporter(LowLevelTransporter transporter) {
		this.transporter = transporter;
	}

	/**
	 * Upload the uploadObject using the specified transporter.
	 * 
	 * @param uploadObject
	 *            The uploadObject to upload.
	 */
	public void upload(Transportable uploadObject) {
		transporter.upload(uploadObject.getName(),
				uploadObject.getContent());
	}

	/**
	 * Download the resource identified by transportInfo using the specified
	 * transporter.
	 * 
	 * @param transportInfo
	 *            information on the resource to download.
	 * @return The content of the requested resource.
	 */
	public byte[] download(TransportInfo transportInfo) {
		return transporter.download(transportInfo);
	}

}
