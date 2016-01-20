package com.stock.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="stock_history", indexes={
		@Index(name = "index_stock_history_code_date", columnList = "code, date" )
		}
)
@Data
@EqualsAndHashCode(callSuper = true)
public class StockHistory extends BaseDo implements Comparable<StockHistory> {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="code", length=6)
	private String code;
	
	@Transient
	private String name;

	private Timestamp date;
	
	private Float open;
	
	private Float high;
	
	private Float low;
	
	private Float close;
	
	private Long volume;

	@Override
	public int compareTo(StockHistory o) {
		if (date.getTime() < o.getDate().getTime()) {
			return -1;
		} else {
			return 1;
		}
	}
}
