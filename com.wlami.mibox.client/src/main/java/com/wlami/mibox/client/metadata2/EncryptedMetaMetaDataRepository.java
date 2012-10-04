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

import com.wlami.mibox.client.application.AppSettings;
import com.wlami.mibox.client.networking.adapter.RestTransporter;
import com.wlami.mibox.client.networking.adapter.TransportInfo;

/**
 * @author stefan baust
 * @author wladislaw mitzel
 * 
 */
public class EncryptedMetaMetaDataRepository {

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
		transportInfo.setResourceName(appSettings.getUsername());
		byte[] result = restTransporter.download(transportInfo);
		EncryptedMetaMetaData encryptedMetaMetaData = new EncryptedMetaMetaData();
		encryptedMetaMetaData.setContent(result);
		encryptedMetaMetaData.setName(transportInfo.getResourceName());
		return encryptedMetaMetaData;
	}

}
