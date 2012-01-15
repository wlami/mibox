package com.wlami.mibox.server.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Chunk implements Comparable<Chunk> {

	@Id
	@Column(nullable = false, columnDefinition = "char(64)")
	private String hash;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;

	@Deprecated
	public Chunk() {
	}

	public Chunk(String hash) {
		this.hash = hash;
		this.created = new Date();
		this.lastAccessed = this.created;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the lastAccessed
	 */
	public Date getLastAccessed() {
		return lastAccessed;
	}

	/**
	 * @param lastAccessed
	 *            the lastAccessed to set
	 */
	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof Chunk)) {
			return false;
		} else {
			return hash.equals(((Chunk) obj).getHash());
		}
	}

	@Override
	public int compareTo(Chunk o) {
		return hash.compareTo(o.getHash());
	}

}
