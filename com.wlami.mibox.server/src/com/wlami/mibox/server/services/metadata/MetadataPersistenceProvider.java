/**
 *     MiBox Server - folder synchronization backend
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
package com.wlami.mibox.server.services.metadata;

import java.io.IOException;

/**
 * @author wladislaw
 *
 */
public interface MetadataPersistenceProvider {

	/**
	 * Retrieve metadata from the persistent storage.
	 * 
	 * @param name
	 *            Name which is used for identification of the metadata
	 * @return Returns a {@link Byte}-Array which contains the metadata.
	 */
	public abstract byte[] retrieveMetadata(String name);

	/**
	 * Persist a metadata to the storage.
	 * 
	 * @param name
	 *            Name which is used for identification of the metadata.
	 * @param data
	 *            A {@link Byte}-Array which contains the metadata.
	 */
	public void persistMetadata(String name, byte[] data) throws IOException;

}
