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
package com.wlami.mibox.server.services.persistence;

import java.io.IOException;

/**
 * @author wladislaw mitzel
 * @author stefan baust
 * 
 */
public interface PersistenceProvider {

	/**
	 * Retrieve a file from a persistent storage.
	 * 
	 * @param name
	 *            Filename, which identifies the file.
	 * @return The content of the file as byte array.
	 */
	public byte[] retrieveFile(String name);

	/**
	 * Persist a file to a persistent storage.
	 * 
	 * @param name
	 *            FIlename, which identifies the file.
	 * @param content
	 *            THe content of the file as byte array.
	 * @throws IOException
	 *             Thrown on errors during the write
	 */
	public void persistFile(String name, byte[] content) throws IOException;
}
