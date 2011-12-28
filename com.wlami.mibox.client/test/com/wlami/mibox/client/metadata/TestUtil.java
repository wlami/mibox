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

import java.util.Date;

/**
 * @author Wladislaw Mitzel
 * 
 */
public class TestUtil {

	public static MFolder getSimpleMetadata() {
		MFolder folder = new MFolder(null);
		folder.setName("/");
		MFolder subfolder = new MFolder(folder);
		subfolder.setName("subfolder");
		folder.getSubfolders().put(subfolder.getName(), subfolder);
		MFile file1 = new MFile();
		file1.setFolder(folder);
		file1.setName("file1");
		file1.setFileHash("affecaffebabe");
		MChunk chunk1 = new MChunk(0);
		chunk1.setDecryptedChunkHash("decryptedHASH");
		chunk1.setEncryptedChunkHash("encryptedHaSH");
		chunk1.setLastChange(new Date());
		chunk1.setLastSync(new Date());
		file1.getChunks().add(chunk1);
		file1.getChunks().add(chunk1);
		folder.getFiles().put(file1.getName(), file1);

		MFile file2 = new MFile();
		file2.setFolder(subfolder);
		file2.setName("file2");
		file2.setFileHash("ffffaaaacc");
		subfolder.getFiles().put(file2.getName(), file2);

		MFolder subfolder2 = new MFolder(subfolder);
		subfolder2.setName("subfolder2");
		subfolder.getSubfolders().put(subfolder2.getName(), subfolder2);

		MFile mFile3 = new MFile();
		mFile3.setFolder(subfolder2);
		mFile3.setName("file3");
		subfolder2.getFiles().put(mFile3.getName(), mFile3);

		return folder;
	}

}
