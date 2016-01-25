package com.stock.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class StockWindow {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String FORMAT_LINE = "%s | %.2f | %s | %.2f(%d) %.2f(%d) %.2f(%d) %.2f(%d) %.2f(%d) | %.2f(%d) %.2f(%d) %.2f(%d) %.2f(%d) %.2f(%d) | %s | %d | %.2f | %.2f\n";
	
	private static final String STOCK_HEAD = "%s(%s) | 当前价格 | 幅度 | 卖五 卖四 卖三 卖二 卖一 | 买一 买二 买三 买四 买五  | 委比 | 委差 | 昨日收盘  | 今日开盘\n";

	private String code;
	private String name;
	private Date date;
	private long vol;
	
	private float todayOpen;
	private float yesterdayClose;
	private float currentPrice;
	
	private int buyNum_1;
	private int buyNum_2;
	private int buyNum_3;
	private int buyNum_4;
	private int buyNum_5;
	
	private int sellNum_1;
	private int sellNum_2;
	private int sellNum_3;
	private int sellNum_4;
	private int sellNum_5;
	
	private float buyPrice_1;
	private float buyPrice_2;
	private float buyPrice_3;
	private float buyPrice_4;
	private float buyPrice_5;
	
	private float sellPrice_1;
	private float sellPrice_2;
	private float sellPrice_3;
	private float sellPrice_4;
	private float sellPrice_5;
	
	public boolean isValid() {
		return vol > 0;
	}
	
	public String getHead() {
		return String.format(STOCK_HEAD, this.code,  this.name);
	}
	
	public String getPricePercent() {
		float percent = (currentPrice - yesterdayClose) * 100 / yesterdayClose;
		return String.format("%.2f%%", percent);
	}
	
	public int getWeiCha() {
		return buyNum_1 + buyNum_2 + buyNum_3 + buyNum_4 + buyNum_5 
				- sellNum_1 - sellNum_2 - sellNum_3 - sellNum_4 - sellNum_5;
	}
	
	public String getWeiBi() {
		float weiCha = getWeiCha();
		float weiBi;
		if (weiCha == 0) {
			weiBi = 0f;
		} else {
			weiBi = getWeiCha() * 100 / (buyNum_1 + buyNum_2 + buyNum_3 + buyNum_4 + buyNum_5 
					+ sellNum_1 + sellNum_2 + sellNum_3 + sellNum_4 + sellNum_5);
		}
		
		return String.format("%.2f%%", weiBi);
	}
	
	public String toString() {
		return String.format(FORMAT_LINE
				,DATE_FORMAT.format(date)
				,currentPrice
				,getPricePercent()
				,sellPrice_5, sellNum_5, sellPrice_4, sellNum_4, sellPrice_3, sellNum_3, sellPrice_2, sellNum_2, sellPrice_1, sellNum_1
				,buyPrice_1, buyNum_1, buyPrice_2, buyNum_2, buyPrice_3, buyNum_3, buyPrice_4, buyNum_4, buyPrice_5, buyNum_5
				,getWeiBi()
				,getWeiCha()
				,yesterdayClose
				,todayOpen
				);
	}
}
