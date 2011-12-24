/**
 *     MiBox Client - folder synchronization client
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
package com.wlami.mibox.client.metadata;

/**
 * Defines a metadata respository which is responsible for persistency of
 * metadata:
 * <ul>
 * <li>folder structure</li>
 * <li>files</li>
 * <li>file chunks</li>
 * </ul>
 * 
 * @author Wladislaw Mitzel
 * 
 */
public interface MetadataRepository {

	/**
	 * Start the repository. It runs in a separate thread.
	 */
	public abstract void startProcessing();

	/**
	 * Stop the repository.
	 */
	public abstract void stopProcessing();

	/**
	 * Notify the repository of changes in the filesystem.
	 * 
	 * @param observedFilesystemEvent
	 *            the observed event.
	 */
	public abstract void addEvent(
			ObservedFilesystemEvent observedFilesystemEvent);

}