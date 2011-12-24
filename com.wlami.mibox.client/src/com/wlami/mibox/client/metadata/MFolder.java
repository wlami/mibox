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

import java.util.Hashtable;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MFolder {

	/**
	 * Folder name.
	 */
	private String name;

	/**
	 * Defines the folder in which this folder is contained. <code>null</code>
	 * if this is the root folder.
	 */
	@JsonBackReference
	private MFolder parentFolder;

	/**
	 * Contains a set of subfolders.
	 */
	@JsonManagedReference
	private Hashtable<String, MFolder> subfolders;

	/**
	 * Contains a set of all files in this folder.
	 */
	@JsonManagedReference
	private Hashtable<String, MFile> files;

	/**
	 * 
	 */
	public MFolder() {
		this(null);
	}

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 *            the parent folder. Use <code>null</code> if this is the root
	 *            folder.
	 */
	public MFolder(MFolder parent) {
		this.parentFolder = parent;
		files = new Hashtable<String, MFile>();
		subfolders = new Hashtable<String, MFolder>();
	}

	/**
	 * @return the subfolder
	 */
	public Hashtable<String, MFolder> getSubfolders() {
		return subfolders;
	}

	/**
	 * @return the files
	 */
	public Hashtable<String, MFile> getFiles() {
		return files;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
