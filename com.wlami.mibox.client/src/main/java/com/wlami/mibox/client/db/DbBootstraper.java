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
package com.wlami.mibox.client.db;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author wladislaw
 * 
 */
public class DbBootstraper {

	private static final String CREATE_TABLE_META_SYNC = "CREATE  TABLE `meta_sync` (`uuid` VARCHAR(36) NOT NULL , `lastSync` TIMESTAMP NULL ,   PRIMARY KEY (`uuid`) );";

	private DbBootstraper() {
	}

	public static void bootstrap(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			jdbcTemplate.execute("SELECT 1 FROM meta_sync");
		} catch (DataAccessException e) {
			jdbcTemplate.execute(CREATE_TABLE_META_SYNC);
		}
	}

}
