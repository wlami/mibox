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
package com.wlami.mibox.client.metadata2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.application.AppFolders;

/**
 * @author Stefan baust
 * @author wladislaw Mitzel
 * 
 */
@Singleton
@Named
public class EncryptedMiTreeRepository {

	/** interal logger */
	private static Logger log = LoggerFactory
			.getLogger(EncryptedMiTreeRepository.class);

	/**
	 * Loads an encrypted MiTree from local file system.
	 * 
	 * @param fileName
	 *            File name of the MiTree
	 * @return <code>null</code> if there is an error<br/>
	 *         {@link EncryptedMiTree} otherwise
	 */
	public EncryptedMiTree loadEncryptedMiTree(String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] content = new byte[(int) file.length()];
			fileInputStream.read(content);
			return new EncryptedMiTree(content);
		} catch (IOException e) {
			// If there is no root file one gets created. So don't log an error.
			if (!"root".equals(fileName)) {
				log.error("Error during load of encrypted MiTree", e);
			}
			return null;
		}
	}

	/**
	 * Save an encrypted MiTree to local file system
	 * 
	 * @param encryptedMiTree
	 *            The tree which shall be saved.
	 * @param fileName
	 *            filename of the MiTree.
	 */
	public void saveEncryptedMiTree(EncryptedMiTree encryptedMiTree,
			String fileName) {
		File file = new File(AppFolders.getConfigFolder(), fileName);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(encryptedMiTree.getContent());
		} catch (IOException e) {
			log.error("Error during save of encrypted MiTree", e);
		}
	}
}
