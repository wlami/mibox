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

import java.util.Set;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MFolder {

	/**
	 * Defines the folder in which this folder is contained. <code>null</code>
	 * if this is the root folder.
	 */
	private MFolder parentFolder;

	/**
	 * Contains a set of subfolders.
	 */
	private Set<MFolder> subfolder;

	/**
	 * Contains a set of all files in this folder.
	 */
	private Set<MFile> files;

	/**
	 * @return the subfolder
	 */
	public Set<MFolder> getSubfolder() {
		return subfolder;
	}

	/**
	 * @param subfolder
	 *            the subfolder to set
	 */
	public void setSubfolder(Set<MFolder> subfolder) {
		this.subfolder = subfolder;
	}

	/**
	 * @return the files
	 */
	public Set<MFile> getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(Set<MFile> files) {
		this.files = files;
	}

	/**
	 * @return the parentFolder
	 */
	public MFolder getParentFolder() {
		return parentFolder;
	}

	/**
	 * @param parentFolder
	 *            the parentFolder to set
	 */
	public void setParentFolder(MFolder parentFolder) {
		this.parentFolder = parentFolder;
	}

}
