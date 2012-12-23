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
package com.wlami.mibox.client.metadata;

import org.slf4j.Logger;

import com.wlami.mibox.client.metadata2.EncryptedAbstractObject;

/**
 * @author wladislaw
 * 
 */
public class EncryptedMiFile extends EncryptedAbstractObject<DecryptedMiFile> {

	/** internal logger */
	public static final Logger log = org.slf4j.LoggerFactory
			.getLogger(EncryptedMiFile.class);

	/**
	 * Default constructor.
	 */
	public EncryptedMiFile() {
		super(DecryptedMiFile.class);
		log.debug("Creating new instance of EncryptedMiFile");
	}
}
