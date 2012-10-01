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
package com.wlami.mibox.server.util;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.core.util.Base64;
import com.wlami.mibox.server.data.User;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class HttpHeaderUtil {

	/**
	 * @param headers
	 * @return
	 */
	public static User getUserFromHttpHeaders(HttpHeaders headers,
			EntityManager em) {
		try {
			String username = HttpHeaderUtil.getAuthorization(headers)[0];
			return User.loadUserByUsername(username, em);
		} catch (NoResultException e) {
			return null;
		}
	}

	public static String[] getAuthorization(HttpHeaders httpHeaders) {
		String authorization = httpHeaders.getRequestHeader("authorization")
				.get(0);
		// Remove the "BASIC " prefix
		authorization = authorization.substring(6);
		authorization = Base64.base64Decode(authorization);
		return authorization.split(":");
	}
}
