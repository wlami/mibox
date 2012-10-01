package com.wlami.mibox.server.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Metadata implements Comparable<Metadata> {

	@Id
	@Column(nullable = false, columnDefinition = "char(36)")
	private String name;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;

	@Deprecated
	public Metadata() {
	}

	public Metadata(String name) {
		this.name = name;
		created = new Date();
		lastAccessed = created;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
		} else if (!(obj instanceof Metadata)) {
			return false;
		} else {
			return name.equals(((Metadata) obj).getName());
		}
	}

	@Override
	public int compareTo(Metadata o) {
		return name.compareTo(o.getName());
	}

}
