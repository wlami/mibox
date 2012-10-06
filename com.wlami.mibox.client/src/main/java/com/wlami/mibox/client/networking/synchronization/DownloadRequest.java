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
package com.wlami.mibox.client.networking.synchronization;

import com.wlami.mibox.client.networking.adapter.TransportInfo;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 */
public class DownloadRequest implements Comparable<DownloadRequest> {

	/** resource identifying the remote resource */
	private TransportInfo transportInfo;
	/** Callback which is executed when the request has been processed */
	private TransportCallback transportCallback;

	/**
	 * 
	 */
	public DownloadRequest(String resourceName,
			TransportCallback transportCallback) {
		transportInfo = new TransportInfo(resourceName);
		this.transportCallback = transportCallback;
	}

	/**
	 * @return the transportInfo
	 */
	public TransportInfo getTransportInfo() {
		return transportInfo;
	}

	/**
	 * @param transportInfo
	 *            the transportInfo to set
	 */
	public void setTransportInfo(TransportInfo transportInfo) {
		this.transportInfo = transportInfo;
	}

	/**
	 * @return the transportCallback
	 */
	public TransportCallback getTransportCallback() {
		return transportCallback;
	}

	/**
	 * @param transportCallback
	 *            the transportCallback to set
	 */
	public void setTransportCallback(TransportCallback transportCallback) {
		this.transportCallback = transportCallback;
	}

	@Override
	public int compareTo(DownloadRequest o) {
		return getTransportInfo().compareTo(o.getTransportInfo());
	};

}
