/**
 *     MiBox Server - folder synchronization backend
 *  Copyright (C) 2012 Wladislaw Mitzel
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
package com.wlami.mibox.server.services.chunk;

import java.io.IOException;

/**
 * This interface defines classes which are responsible for persisting of chunk
 * data. Possible implementation could use local or cloud storage.
 */
public interface ChunkPersistenceProvider {

	/**
	 * Retrieve a chunk from the persistent storage.
	 * 
	 * @param hash
	 *            Hash which is used for identification of the chunk
	 * @return Returns a {@link Byte}-Array which contains the chunk data.
	 */
	public abstract byte[] retrieveChunk(String hash);

	/**
	 * Persist a chunk to the storage.
	 * 
	 * @param hash
	 *            Hash which is used for identification of the chunk.
	 * @param data
	 *            A {@link Byte}-Array which contains the chunk data.
	 */
	public void persistChunk(String hash, byte[] data) throws IOException;

}
