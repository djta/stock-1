package com.stock.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class TimeStamp implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Column(name="created_at")
	protected Timestamp createdAt;
	
	@Getter @Setter
	@Column(name="updated_at")
	@Version
	protected Timestamp updatedAt;
	
	@PrePersist
	void onCreate(){
		createdAt = new Timestamp(System.currentTimeMillis());
	}
	
	@PreUpdate
	void onUpdate(){
		if (updatedAt == null) {
			updatedAt = new Timestamp(System.currentTimeMillis());
		}
	}

}
