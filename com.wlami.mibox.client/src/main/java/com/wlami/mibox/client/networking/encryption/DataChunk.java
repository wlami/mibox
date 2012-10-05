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
package com.wlami.mibox.client.networking.encryption;

import com.wlami.mibox.client.networking.transporter.Transportable;

/**
 * Represents an encrypted Chunk.
 * 
 * @author wladislaw mitzel
 */
public class DataChunk implements Transportable {

	/** hash of the #content */
	private String hash;
	/** contains the encrypted content of a chunk. */
	private byte[] content;

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * This returns a reference to the internal array and <b>DOES NOT</b> create
	 * a <b>copy</b>.<br/>
	 * <br/>
	 * So <b>do not modify</b> the content.
	 * 
	 * @return the content
	 */
	@Override
	public byte[] getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * Convenience constructor.
	 */
	public DataChunk(String hash, byte[] content) {
		this.hash = hash;
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wlami.mibox.client.networking.transporter.Transportable#getName()
	 */
	@Override
	public String getName() {
		return hash;
	}

}
