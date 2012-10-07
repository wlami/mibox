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

import java.io.IOException;
import java.util.Arrays;

import org.bouncycastle.crypto.CryptoException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlami.mibox.client.metadata2.DecryptedMetaMetaData;
import com.wlami.mibox.client.metadata2.DecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptableDecryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeInformation;
import com.wlami.mibox.client.metadata2.EncryptedMiTreeRepository;
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;

/**
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class MetadataUtil {

	/** internal logger. */
	private static final Logger LOG = LoggerFactory.getLogger(MetadataUtil.class);

	EncryptedMiTreeRepository encryptedMiTreeRepository;

	MetaMetaDataHolder metaMetaDataHolder;

	public MetadataUtil(EncryptedMiTreeRepository encryptedMiTreeRepository, MetaMetaDataHolder metaMetaDataHolder) {
		this.encryptedMiTreeRepository = encryptedMiTreeRepository;
		this.metaMetaDataHolder
		= metaMetaDataHolder;
	}

	protected static final String UNIX_PATH_SEPARATOR = "/";

	/**
	 * Locates a {@link MFile} in the {@link EncryptedMiTree} structure root. If
	 * it does not exist yet, the file and all {@link EncryptedMiTree}s in the
	 * path get created.
	 * 
	 * @param root
	 *            the root {@link EncryptedMiTree} where the search starts.
	 * @param relativePath
	 *            the path relative to the position of the root.
	 * @return
	 * @throws IOException
	 * @throws CryptoException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public MFile locateMFile(String relativePath)
			throws JsonParseException, JsonMappingException, CryptoException, IOException {
		DecryptedMiTree decryptedMiTree = locateDecryptedMiTree(relativePath).getDecryptedMiTree();
		String[] folder = relativePath.split(UNIX_PATH_SEPARATOR);

		MFile file = decryptedMiTree.getFiles().get(folder[folder.length - 1]);
		if (file == null) {
			file = new MFile();
			file.setName(folder[folder.length - 1]);
			decryptedMiTree.getFiles().put(file.getName(), file);
		}
		return file;
	}

	public EncryptableDecryptedMiTree locateDecryptedMiTree(String relativePath) throws JsonParseException,
	JsonMappingException,
	CryptoException, IOException {
		DecryptedMetaMetaData decryptedMetaMetaData = metaMetaDataHolder.getDecryptedMetaMetaData();
		EncryptedMiTree root = encryptedMiTreeRepository.loadEncryptedMiTree(decryptedMetaMetaData.getRoot()
				.getFileName());
		EncryptedMiTreeInformation information = decryptedMetaMetaData.getRoot();
		return locateDecryptedMiTree(root, information, relativePath);
	}

	public EncryptableDecryptedMiTree locateDecryptedMiTree(EncryptedMiTree root,
			EncryptedMiTreeInformation information,
			String relativePath) throws JsonParseException, JsonMappingException, CryptoException, IOException {
		if (!relativePath.startsWith(UNIX_PATH_SEPARATOR)) {
			throw new IllegalArgumentException("relativePath has to start with File.pathSeparator");
		}

		String[] folder = relativePath.split(UNIX_PATH_SEPARATOR);
		System.out.println(Arrays.toString(folder));
		DecryptedMiTree decryptedMiTree = root.decrypt(information.getKey(), information.getIv());
		if (folder.length == 2) {
			return new EncryptableDecryptedMiTree(information, decryptedMiTree);
		} else {
			LOG.debug("we have to look in the subtrees.");
			EncryptedMiTree subTree;
			EncryptedMiTreeInformation subTreeInformation = decryptedMiTree.getSubfolder().get(folder[1]);

			if (subTreeInformation == null) {
				LOG.debug("Create new subtree, didn't find it.");
				DecryptedMiTree decryptedSubTree = new DecryptedMiTree();
				subTreeInformation = EncryptedMiTreeInformation.createRandom();
				subTree = decryptedSubTree.encrypt(subTreeInformation.getFileName(), subTreeInformation.getKey(),
						subTreeInformation.getIv());
				subTree.setName(subTreeInformation.getFileName());
				decryptedMiTree.getSubfolder().put(folder[1], subTreeInformation);
				// Save the new subtree
				encryptedMiTreeRepository.saveEncryptedMiTree(subTree, subTreeInformation.getFileName());
				// Save the new metadata for the current tree
				encryptedMiTreeRepository.saveEncryptedMiTree(
						decryptedMiTree.encrypt(information.getFileName(), information.getKey(), information.getIv()),
						information.getFileName());
			} else {
				LOG.debug("Found subtree, loading it from repository.");
				subTree = encryptedMiTreeRepository.loadEncryptedMiTree(subTreeInformation.getFileName());
			}

			StringBuilder sb = new StringBuilder(UNIX_PATH_SEPARATOR);
			for (int i = 2; i < folder.length; i++) {
				sb.append(folder[i]);
				if (i < folder.length - 1) {
					sb.append(UNIX_PATH_SEPARATOR);
				}
			}
			// recurse for the subtree
			return locateDecryptedMiTree(subTree, subTreeInformation, sb.toString());
		}

	}
}
