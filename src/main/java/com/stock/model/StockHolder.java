package com.stock.model;

import java.util.List;

import lombok.Data;

@Data
public class StockHolder {
	
	private StockMessage stockMessage;
	
	private List<StockHolderItem> holderHis;
	
	public double getDif(int count) {
		if (holderHis == null || holderHis.size() -1 < count) {
			return 0;
		}
		
		double curValue = holderHis.get(0).getPeopleNum();
		double perValue = holderHis.get(count).getPeopleNum();
		
		return (curValue - perValue)/perValue;
	}
	
	@Override
	public String toString(){
		return stockMessage.getCode() + "\t" + stockMessage.getName() + "\t" + getDif(2);
	}
}
