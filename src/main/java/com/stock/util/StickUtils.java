package com.stock.util;

import java.util.ArrayList;
import java.util.List;

import com.stock.model.StockLineK;

public class StickUtils {

	public List<StockLineK> mergeStockLineK(List<StockLineK> source) {
		if (source == null) {
			return null;
		}
		
		List<StockLineK> list = new ArrayList<>();
		StockLineK latestK = null;
		for (StockLineK k : source) {
			if (latestK == null) {
				//第一根K线
				list.add(latestK);
				latestK = k;
				continue;
			}
			
			//存在包含K线
			if (latestK.isContain(k) || k.isContain(latestK)) {
				if (latestK.isDown()) {
					//取低低
					latestK.setHigh(Math.min(latestK.getHigh(), k.getHigh()));
					latestK.setLow(Math.min(latestK.getLow(), k.getLow()));
				} else {
					//取高高
					latestK.setHigh(Math.max(latestK.getHigh(), k.getHigh()));
					latestK.setLow(Math.max(latestK.getLow(), k.getLow()));
				}
				continue;
			}
			
			if (k.getLow() < latestK.getLow() && k.getHigh() < latestK.getHigh()) {
				k.setDown(true);
			} else if (k.getLow() > latestK.getLow() && k.getHigh() > latestK.getHigh()) {
				k.setDown(false);
			}
			
			if (latestK.isDown() != k.isDown()) {
				k.setTurningPoint(true);
			}
			
			list.add(k);
			latestK = k;
		}
		
		return list;
	}
}