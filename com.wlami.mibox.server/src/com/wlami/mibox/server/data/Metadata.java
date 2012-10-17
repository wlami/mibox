package com.wlami.mibox.server.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Metadata implements Comparable<Metadata> {

	/** internal logger */
	public static final Logger log = LoggerFactory.getLogger(Metadata.class);

	@Id
	@Column(nullable = false, columnDefinition = "char(36)")
	private String name;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdated;

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

	/**
	 * @return the lastUpdated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated
	 *            the lastUpdated to set
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Get a {@link Metadata} by its name.
	 * 
	 * @param name
	 *            The search parameter.
	 * @param em
	 *            An entity manager for db access.
	 * @return The Metadata from the db or <code>null</code> if nothing is
	 *         found.
	 */
	public static Metadata getByName(String name, EntityManager em) {
		Metadata metadata = null;
		try {
			metadata = (Metadata) em
					.createQuery(
							"SELECT m FROM Metadata m WHERE m.name = :name")
							.setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			log.debug("Could not get Metadata by name for [{}]", name);
		}
		return metadata;
	}

}
