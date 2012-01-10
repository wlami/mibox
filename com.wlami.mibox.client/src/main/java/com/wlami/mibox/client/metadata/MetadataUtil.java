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

import java.util.Arrays;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class MetadataUtil {

	/**
	 * 
	 */
	protected static final String UNIX_PATH_SEPARATOR = "/";

	/**
	 * Locates a {@link MFile} in the {@link MFolder} structure root. If it does
	 * not exist yet, the file and all {@link MFolder}s in the path get created.
	 * 
	 * @param root
	 *            the root {@link MFolder} where the search starts.
	 * @param relativePath
	 *            the path relative to the position of the root.
	 * @return
	 */
	public static MFile locateMFile(MFolder root, String relativePath) {
		if (!relativePath.startsWith(UNIX_PATH_SEPARATOR)) {
			throw new IllegalArgumentException(
					"relativePath has to start with File.pathSeparator");
		}

		String[] folder = relativePath.split(UNIX_PATH_SEPARATOR);
		System.out.println(Arrays.toString(folder));
		if (folder.length == 2) {
			MFile file = root.getFiles().get(folder[1]);
			if (file == null) {
				file = new MFile();
				file.setName(folder[1]);
				file.setFolder(root);
				root.getFiles().put(file.getName(), file);
			}
			return file;
		} else {
			MFolder subfolder = root.getSubfolders().get(folder[1]);
			if (subfolder == null) {
				subfolder = new MFolder(root);
				subfolder.setName(folder[1]);
				root.getSubfolders().put(subfolder.getName(), subfolder);
			}
			StringBuilder sb = new StringBuilder(UNIX_PATH_SEPARATOR);
			for (int i = 2; i < folder.length; i++) {
				sb.append(folder[i]);
				if (i < folder.length - 1) {
					sb.append(UNIX_PATH_SEPARATOR);
				}
			}
			return locateMFile(subfolder, sb.toString());
		}

	}
}
