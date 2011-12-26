package com.wlami.mibox.server.data;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class GroupsPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String groups;

	public GroupsPK() {
	}

	/**
	 * @return the username
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	protected void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the group
	 */
	protected String getGroups() {
		return groups;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	protected void setGroups(String groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		return username.hashCode() + groups.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof GroupsPK)) {
			return false;
		} else if (obj == this) {
			return true;
		} else {
			GroupsPK g = (GroupsPK) obj;
			return ((this.username.equals(g.getUsername())) && (this.groups
					.equals(g.getGroups())));
		}
	}
}
