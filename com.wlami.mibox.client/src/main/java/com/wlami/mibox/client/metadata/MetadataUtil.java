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
import com.wlami.mibox.client.metadata2.EMiFile;
import com.wlami.mibox.client.metadata2.EMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMetadataObjectRepository;
import com.wlami.mibox.client.metadata2.EncryptedMiTree;
import com.wlami.mibox.client.metadata2.EncryptedMetadataInformation;
import com.wlami.mibox.client.metadata2.MetaMetaDataHolder;

/**
 * @author Wladislaw Mitzel
 * @author Stefan Baust
 */
public class MetadataUtil {

	/** internal logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(MetadataUtil.class);

	EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepository;

	EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepository;

	MetaMetaDataHolder metaMetaDataHolder;

	public MetadataUtil(
			EncryptedMetadataObjectRepository<EncryptedMiTree> encryptedMiTreeRepository,
			EncryptedMetadataObjectRepository<EncryptedMiFile> encryptedMiFileRepository,
			MetaMetaDataHolder metaMetaDataHolder) {
		this.encryptedMiTreeRepository = encryptedMiTreeRepository;
		this.encryptedMiFileRepository = encryptedMiFileRepository;
		this.metaMetaDataHolder = metaMetaDataHolder;
	}

	protected static final String UNIX_PATH_SEPARATOR = "/";

	/**
	 * Locates a {@link DecryptedMiFile} in the {@link EncryptedMiTree}
	 * structure root. If it does not exist yet, the file and all
	 * {@link EncryptedMiTree}s in the path get created.
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
	public EMiFile locateMFile(String relativePath)
			throws JsonParseException, JsonMappingException, CryptoException,
			IOException {
		// locate the decrypted MiTree which contains the file
		EMiTree emiTree = locateDecryptedMiTree(relativePath);
		DecryptedMiTree decryptedMiTree = emiTree.getEncryptableObject();
		String[] folder = relativePath.split(UNIX_PATH_SEPARATOR);
		// Now get the file information!
		EncryptedMetadataInformation fileInformation = decryptedMiTree
				.getFiles().get(folder[folder.length - 1]);
		// Now get the encrypted MiFile!
		EncryptedMiFile encryptedMiFile = encryptedMiFileRepository
				.loadEncryptedMetadata(fileInformation.getFileName());
		EMiFile file = null;
		if (encryptedMiFile == null) {
			String filename = folder[folder.length - 1];
			file = createMiFileInMiTree(emiTree, filename);
		} else {
			file = new EMiFile(fileInformation, encryptedMiFile.decrypt(fileInformation));
		}
		return file;
	}

	/**
	 * @param emiTree
	 * @param filename
	 * @return
	 */
	public EMiFile createMiFileInMiTree(EMiTree emiTree,
			String filename) {
		EncryptedMiFile encryptedMiFile;
		DecryptedMiFile file= new DecryptedMiFile();
		DecryptedMiTree decryptedMiTree = emiTree.getEncryptableObject();
		file.setName(filename);
		EncryptedMetadataInformation encInfo = EncryptedMetadataInformation
				.createRandom();
		encryptedMiFile = file.encrypt(encInfo);
		encryptedMiFileRepository.saveEncryptedMetadata(encryptedMiFile,
				encInfo.getFileName());
		decryptedMiTree.getFiles().put(file.getName(), encInfo);
		EncryptedMiTree encryptedMiTree = decryptedMiTree.encrypt(emiTree
				.getEncryptedMiTreeInformation());
		encryptedMiTreeRepository.saveEncryptedMetadata(encryptedMiTree,
				emiTree.getEncryptedMiTreeInformation().getFileName());
		return new EMiFile(encInfo, file);
	}

	public EMiTree locateDecryptedMiTree(String relativePath)
			throws JsonParseException, JsonMappingException, CryptoException,
			IOException {
		DecryptedMetaMetaData decryptedMetaMetaData = metaMetaDataHolder
				.getDecryptedMetaMetaData();
		EncryptedMiTree root = encryptedMiTreeRepository
				.loadEncryptedMetadata(decryptedMetaMetaData.getRoot()
						.getFileName());
		EncryptedMetadataInformation information = decryptedMetaMetaData
				.getRoot();
		return locateDecryptedMiTree(root, information, relativePath);
	}

	public EMiTree locateDecryptedMiTree(EncryptedMiTree root,
			EncryptedMetadataInformation information, String relativePath)
			throws JsonParseException, JsonMappingException, CryptoException,
			IOException {
		if (!relativePath.startsWith(UNIX_PATH_SEPARATOR)) {
			throw new IllegalArgumentException(
					"relativePath has to start with File.pathSeparator");
		}

		String[] folder = relativePath.split(UNIX_PATH_SEPARATOR);
		System.out.println(Arrays.toString(folder));
		DecryptedMiTree decryptedMiTree = root.decrypt(information.getKey(),
				information.getIv());
		if (folder.length == 2) {
			return new EMiTree(information, decryptedMiTree);
		} else {
			LOG.debug("we have to look in the subtrees.");
			EncryptedMiTree subTree;
			EncryptedMetadataInformation subTreeInformation = decryptedMiTree
					.getSubfolder().get(folder[1]);

			if (subTreeInformation == null) {
				LOG.debug("Create new subtree, didn't find it.");
				DecryptedMiTree decryptedSubTree = new DecryptedMiTree();
				subTreeInformation = EncryptedMetadataInformation
						.createRandom();
				subTree = decryptedSubTree
						.encrypt(subTreeInformation.getFileName(),
								subTreeInformation.getKey(),
								subTreeInformation.getIv());
				subTree.setName(subTreeInformation.getFileName());
				decryptedMiTree.getSubfolder().put(folder[1],
						subTreeInformation);
				// Save the new subtree
				encryptedMiTreeRepository.saveEncryptedMetadata(subTree,
						subTreeInformation.getFileName());
				// Save the new metadata for the current tree
				encryptedMiTreeRepository.saveEncryptedMetadata(decryptedMiTree
						.encrypt(information.getFileName(),
								information.getKey(), information.getIv()),
						information.getFileName());
			} else {
				LOG.debug("Found subtree, loading it from repository.");
				subTree = encryptedMiTreeRepository
						.loadEncryptedMetadata(subTreeInformation.getFileName());
			}

			StringBuilder sb = new StringBuilder(UNIX_PATH_SEPARATOR);
			for (int i = 2; i < folder.length; i++) {
				sb.append(folder[i]);
				if (i < folder.length - 1) {
					sb.append(UNIX_PATH_SEPARATOR);
				}
			}
			// recurse for the subtree
			return locateDecryptedMiTree(subTree, subTreeInformation,
					sb.toString());
		}

	}


}
