package com.stock.model;

import lombok.Data;

@Data
public class StockDataParams {

	private String symbol;
	private String name;
	private Object[][] data;
}
