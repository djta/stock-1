package com.stock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="stock_message", indexes={
		@Index(name = "index_stock_message_code", columnList = "code" )
		}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class StockMessage extends BaseDo {
	
	private static final long serialVersionUID = 1L;
	
	public enum StockDomain {
		SH, SZ
	}

	@Column(name="code", length=6)
	private String code;
	
	@Column(name="name", length=12)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name="domain", length=2)
	private StockDomain domain;
	
	public String getStockCode() {
		return domain.name().toLowerCase() + code;
	}
}
