/**
 *     MiBox Server - folder synchronization backend
 *  Copyright (C) 2011 Wladislaw Mitzel
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
package com.wlami.mibox.server.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wlami.mibox.server.data.User;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Path("/usermanager")
public class UserManager {

	private static final String PERSISTENCE_UNIT_NAME = "com.wlami.mibox.server";
	EntityManagerFactory emf;
	EntityManager em;

	public UserManager() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
	}

	@GET
	@Produces("application/json")
	public List<User> listUser() {
		Query q = em.createQuery("SELECT u from User u");
		return q.getResultList();
	}
}
