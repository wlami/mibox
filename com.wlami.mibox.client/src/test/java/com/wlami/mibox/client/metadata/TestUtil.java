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

import org.junit.Ignore;

import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Ignore
public class TestUtil {

	public static EncryptedMiTreeInformation getTreeCrypto() {
		EncryptedMiTreeInformation result = new EncryptedMiTreeInformation();
		result.setFileName("tree");
		result.setKey(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4,
				5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1 });
		result.setIv(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4,
				5 });
		return result;
	}

	@Ignore
	public static DecryptedMiTree getSimpleMetadata() {
		DecryptedMiTree folder = new DecryptedMiTree();
		DecryptedMiTree subfolder = new DecryptedMiTree();
		EncryptedMiTreeInformation subfolderInfo = getTreeCrypto();
		folder.getSubfolder().put("subfolder", subfolderInfo);
		MFile file1 = new MFile();
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
		file2.setName("file2");
		file2.setFileHash("ffffaaaacc");
		subfolder.getFiles().put(file2.getName(), file2);

		DecryptedMiTree subfolder2 = new DecryptedMiTree();
		subfolder.getSubfolder().put("subfolder2", subfolderInfo);

		MFile mFile3 = new MFile();
		mFile3.setName("file3");
		subfolder2.getFiles().put(mFile3.getName(), mFile3);

		return folder;
	}

}
