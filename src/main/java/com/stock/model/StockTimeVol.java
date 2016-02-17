package com.stock.model;

import lombok.Data;

@Data
public class StockTimeVol {
	
	private String time;
	private float price;
	private int vol;
	
	public static StockTimeVol createInstance(String lineStr) {
		if (lineStr.indexOf(":") != 2) {
			return null;
		}
		String[] columns = lineStr.split("\\s+");
		
		StockTimeVol stock = new StockTimeVol();
		stock.setTime(columns[0]);
		stock.setPrice(Float.valueOf(columns[1]));
		stock.setVol(Integer.valueOf(columns[2]));
		return stock;
	}
}
