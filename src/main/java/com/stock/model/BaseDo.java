package com.stock.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EqualsAndHashCode(callSuper = false, of = {})
public abstract class BaseDo extends TimeStamp implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 4303381541949870950L;
	
	@Id
	@Getter @Setter
	@GeneratedValue
	protected Long id;
}
