/**
 * 
 */
package com.wlami.mibox.server.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

/**
 * @author Wladislaw Mitzel
 * 
 */
@Entity
@XmlRootElement
@Table(name = "UUSER")
public class User {

	@Id
	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String eMail;

	@Column(nullable = false)
	private String password;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UUSER_CHUNK", joinColumns = @JoinColumn(name = "UUSER_USERNAME", referencedColumnName = "USERNAME"), inverseJoinColumns = @JoinColumn(name = "CHUNK_HASH", referencedColumnName = "HASH"))
	private Set<Chunk> chunks;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UUSER_METADATA", joinColumns = @JoinColumn(name = "UUSER_USERNAME"), inverseJoinColumns = @JoinColumn(name = "METADATA_NAME"))
	private Set<Metadata> metadatas;

	@Deprecated
	public User() {
		chunks = new HashSet<Chunk>();
		metadatas = new HashSet<Metadata>();
	}

	public User(String username, String eMail, String password) {
		this.username = username;
		this.eMail = eMail;
		this.password = password;
		chunks = new HashSet<Chunk>();
		metadatas = new HashSet<Metadata>();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the eMail
	 */
	public String geteMail() {
		return eMail;
	}

	/**
	 * @param eMail
	 *            the eMail to set
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Chunk> getChunks() {
		return chunks;
	}

	/**
	 * @return the metadatas
	 */
	public Set<Metadata> getMetadatas() {
		return metadatas;
	}

	/**
	 * @param username
	 * @return
	 */
	public static User loadUserByUsername(String username, EntityManager em) {
		return (User) em.createQuery("SELECT u from User u WHERE u.username = :username")
				.setParameter("username", username).getSingleResult();
	}

	public boolean userHasChunk(Chunk chunk, EntityManager em) {
		Long count = (Long) em
				.createNativeQuery(
						"SELECT count(CHUNK_HASH) from UUSER_CHUNK uc WHERE uc.UUSER_USERNAME = ?1 AND uc.CHUNK_HASH = ?2")
						.setParameter(1, username).setParameter(2, chunk.getHash()).getSingleResult();
		return count > 0;
	}

	@SuppressWarnings("unchecked")
	public List<String> getByLastUpdatedSince(DateTime time, EntityManager em) {
		return em
				.createNativeQuery(
						"SELECT " + " m.NAME " + "FROM " + "  UUSER_METADATA um, " + "  METADATA m " + " WHERE "
								+ "  um.UUSER_USERNAME = ?1 AND um.METADATA_NAME = m.NAME AND m.LASTUPDATED > ?2")
								.setParameter(1, getUsername()).setParameter(2, time.toDate(), TemporalType.TIMESTAMP).getResultList();
	}
}
