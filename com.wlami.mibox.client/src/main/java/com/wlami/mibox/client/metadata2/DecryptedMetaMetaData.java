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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the highest root object in the metadata hierarchy. It
 * contains the name of the "normal" root metadata.
 * 
 * @see {@link EncryptedMiTree}
 * 
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public class DecryptedMetaMetaData extends
DecryptedAbstractObject<EncryptedMetaMetaData> {

	/** internal logger */
	Logger log = LoggerFactory.getLogger(DecryptedMetaMetaData.class);

	/** this object contains information on the root metadata node */
	private EncryptedMetadataInformation root;

	/**
	 * Default constructor
	 */
	public DecryptedMetaMetaData() {
		super(EncryptedMetaMetaData.class);
	}

	/**
	 * @return the root
	 */
	public EncryptedMetadataInformation getRoot() {
		return root;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(EncryptedMetadataInformation root) {
		this.root = root;
	}

}
