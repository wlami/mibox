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

import com.wlami.mibox.client.metadata2.EncryptedMetadataObjectRepository;
import com.wlami.mibox.client.networking.transporter.Transportable;

/**
 * @author wladislaw
 *
 */
public class EncryptedMetadataUploadRequest extends
UploadRequest<EncryptedMetadataUploadRequest> {

	private final EncryptedMetadataObjectRepository encryptedMetadataRepository;

	public EncryptedMetadataUploadRequest(
			EncryptedMetadataObjectRepository encryptedMetadataRepository) {
		this.encryptedMetadataRepository = encryptedMetadataRepository;
	}

	/**
	 * Return the name of the EncryptedMiTree file.
	 * 
	 * @return Name part of the whole path.
	 */
	public String getName() {
		return file.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(EncryptedMetadataUploadRequest o) {
		return o.getName().compareTo(getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wlami.mibox.client.networking.synchronization.UploadRequest#
	 * getTransportable()
	 */
	@Override
	public Transportable getTransportable() {
		return encryptedMetadataRepository.loadEncryptedMetadata(file.getName());
	}
}
