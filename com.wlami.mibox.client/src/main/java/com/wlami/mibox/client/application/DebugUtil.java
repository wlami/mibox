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
package com.wlami.mibox.client.application;

/**
 * @author wladislaw
 *
 */
public final class DebugUtil {

	/**
	 * constant for enabling decrypted debug.
	 */
	private static final String SYSTEM_PROPERTY_DECRYPTED_DEBUG = "com.wlami.mibox.client.decryptedDebug";

	/**
	 * util class.
	 */
	private DebugUtil() {
	}

	public static boolean isDecryptedDebugEnabled() {
		return Boolean.getBoolean(SYSTEM_PROPERTY_DECRYPTED_DEBUG);
	}
}
