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

import javax.ws.rs.core.Response;

/**
 * This interface defines classes which are responsible for creating an
 * appropriate Response for chunk requests.
 * 
 * @author Wladislaw Mitzel
 * 
 */
public interface ChunkManagerResponseBuilder {

	/**
	 * Builds a {@link Response} for a get request.
	 * 
	 * @param hash
	 *            Hash of the chunk.
	 * @return An HTTP-{@link Response}. The HTTP status can differ: If
	 *         successful 200 (ok) or 3xx (in case of redirection). Otherwise >=
	 *         400
	 */
	public abstract Response buildGetChunkResponse(String hash);

}
