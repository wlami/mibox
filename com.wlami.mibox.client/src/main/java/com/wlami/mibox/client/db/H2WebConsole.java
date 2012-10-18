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

import java.sql.SQLException;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.DebugUtil;

/**
 * @author wladislaw
 *
 */
public class H2WebConsole {

	private Integer port;

	/**
	 * 
	 */
	public H2WebConsole(int port) {
		this.port = port;
	}

	/** internal logger */
	public static final Logger log = LoggerFactory.getLogger(H2WebConsole.class);

	public void init() {
		if (DebugUtil.isH2WebConsoleEnabled()) {
			log.info("Starting h2 web console");
			WebServer service = new WebServer();
			Server server;
			try {
				server = new Server(service, "-web", "-webPort", port.toString());
				service.setShutdownHandler(server);
				server.start();
			} catch (SQLException e) {
				log.error("Could not start h2 web console", e.getMessage());
			}
		}
		else {
			log.debug("H2 web console is turned off");
		}
	}
}
