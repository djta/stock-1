package com.stock.rules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.commons.collections.CollectionUtils;

import com.stock.api.StockMessageHolder;
import com.stock.model.StockHistory;

@NoArgsConstructor
@Getter
public class StockDownward implements SelectionRul {
	
	public static DateFormat smpFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private List<StockHistory> stocks;
	
	private StockHistory hightPoint;
	
	private StockHistory lowPoint;
	
	//上涨空间
	private float potential;
	
	//没跌1%的成交量
	private float perVol;
	
	private int cycle = 0;
	
	private float downResistance;
	
	private String stockName;
	
	public StockDownward(List<StockHistory> stocks) {
		this.stocks = stocks;
	}
	
	public void destroy() {
		stocks = null;
	}

	@Override
	public void select() {
		if (CollectionUtils.isEmpty(stocks)) {
			return;
		}
		
		Collections.sort(stocks);
		hightPoint = stocks.get(0);
		lowPoint = stocks.get(0);
		
		//计算最高点和最低点
		for (StockHistory stock : stocks) {
			if (stock.getClose() > hightPoint.getClose()) {
				hightPoint = stock;
			} else if (stock.getClose() < lowPoint.getClose()) {
				lowPoint = stock;
			}
			
			if (lowPoint.getDate().getTime() < hightPoint.getDate().getTime()) {
				lowPoint = hightPoint;
			}
		}
		
		//计算上涨潜力，上涨空间
		potential = (lowPoint.getClose() - hightPoint.getClose()) * 100 / hightPoint.getClose();
		
		boolean started = false;
		float downValue = 0f, downVol = 0f;
		StockHistory lastStock = hightPoint;
		for (StockHistory stock : stocks) {
			if (started == false) {
				if (!stock.getId().equals(hightPoint.getId()) ) {
					continue;
				} else {
					started = true;
				}
			}
			
			downVol += stock.getVolume() / 10000;
			
			if (lastStock.getClose() > stock.getClose()) {
				downValue += lastStock.getClose() - stock.getClose();
			}
			
			lastStock = stock;
			cycle ++;
			if ( stock.getId().equals(lowPoint.getId()) ) {
				break;
			}
		}
		
		downResistance = downValue / (hightPoint.getClose() - lowPoint.getClose());
		
		float huanShou = downVol * 100 / StockMessageHolder.getStockMessageByCode(lowPoint.getCode()).getCirculation();
		perVol = huanShou / Math.abs(potential);
		
		destroy();
	}
	
	public void print() {
		System.out.println(String.format("股票名字: %s(%s)", StockMessageHolder.getStockMessageByCode(hightPoint.getCode()).getName(), 
				hightPoint.getCode()));
		System.out.println(String.format("最高价格: %s  %.2f", smpFormat.format(hightPoint.getDate()), hightPoint.getClose()));
		System.out.println(String.format("最低价格: %s  %.2f", smpFormat.format(lowPoint.getDate()), lowPoint.getClose()));
		System.out.println(String.format("下跌天数: %d", cycle));
		System.out.println(String.format("下跌阻力: %.2f", downResistance));
		System.out.println(String.format("上涨空间: %.2f", potential));
		System.out.println(String.format("下跌成交 : %.2f", perVol));
	}

}
