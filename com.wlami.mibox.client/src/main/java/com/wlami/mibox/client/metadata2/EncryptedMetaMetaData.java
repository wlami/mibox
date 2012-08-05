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
 * 
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 * 
 */
public class EncryptedMetaMetaData extends
		EncryptedAbstractObject<DecryptedMetaMetaData> {

	/** internal logger */
	private static Logger log = LoggerFactory.getLogger(EncryptedMetaMetaData.class);

	/**
	 * Default constructor.
	 */
	public EncryptedMetaMetaData() {
		super(DecryptedMetaMetaData.class);
		log.debug("Creating new instance of EncryptedMetaMetaData");
	}

}
