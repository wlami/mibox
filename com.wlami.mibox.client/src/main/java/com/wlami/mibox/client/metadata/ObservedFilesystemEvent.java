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

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

/**
 * This class can be used to store information on filesystem changes. <br />
 * <br />
 * Internally a set is used for storing filesystem events. This way it is not
 * possible to add the same event (full path and eventKind) to the set as long
 * as the "old event" is not processed.
 * 
 * @author Wladislaw Mitzel
 */
public class ObservedFilesystemEvent implements
Comparable<ObservedFilesystemEvent> {

	/**
	 * This is the complete path of the file or folder which has been changed.
	 */
	private String filename;

	/**
	 * This is the type of the observed event. May be:
	 * <ul>
	 * <li>{@link java.nio.file.StandardWatchEventKinds.ENTRY_CREATE}</li>
	 * <li>{@link java.nio.file.StandardWatchEventKinds.ENTRY_DELETE}</li>
	 * <li>{@link java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY}</li>
	 * </ul>
	 */
	private Kind<Path> eventKind;

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the eventKind
	 */
	public Kind<Path> getEventKind() {
		return eventKind;
	}

	/**
	 * @param eventKind
	 *            the eventKind to set
	 */
	public void setEventKind(Kind<Path> eventKind) {
		this.eventKind = eventKind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return eventKind.name() + " '" + filename + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof ObservedFilesystemEvent) {
			return (toString().equals(obj.toString()));
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ObservedFilesystemEvent o) {
		return toString().compareTo(o.toString());
	}

}
