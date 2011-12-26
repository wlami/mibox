package com.wlami.mibox.server.data;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Entity implementation class for Entity: Group
 * 
 */
@Entity
public class Groups implements Serializable {

	private static final long serialVersionUID = 1L;

	private GroupsPK primaryKey;

	public Groups() {
		super();
	}

	@EmbeddedId
	public GroupsPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(GroupsPK primaryKey) {
		this.primaryKey = primaryKey;
	}

}
