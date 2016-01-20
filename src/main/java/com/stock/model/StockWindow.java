package com.stock.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.tool.hbm2x.StringUtils;

import lombok.Data;

@Data
public class StockWindow {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String FORMAT_LINE = "%s | %d %d %d %d %d | %d %d %d %d %d | %.2f %.2f %.2f %.2f %.2f | %.2f %.2f %.2f %.2f %.2f\n";
	
	private static final String STOCK_HEAD = "%s(%s) | 量卖五 量卖四 量卖三 量卖二 量卖一 | 量买一 量买二 量买三 量买四 量买五 | 价卖五 价卖四 价卖三 价卖二 价卖一 | 价买一 价买二 价买三 价买四 价买五\n\n";

	private String code;
	private String name;
	private Date date;
	private long vol;
	
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
	
	public StockWindow(String content) {
		if (StringUtils.isEmpty(content)) {
			return;
		}
		
		this.code = content.substring(13, 19);
		int start = content.indexOf("\"");
		int end = content.lastIndexOf("\"");
		String argStr = content.substring(start + 1, end);
		String[] contentArg = argStr.split(",");
		
		if (contentArg.length < 30) {
			return;
		}
		
		this.name = contentArg[0];
		this.vol = Long.valueOf(contentArg[8]);
		try {
			this.date = DATE_FORMAT.parse(contentArg[30] + " " + contentArg[31]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.buyNum_1 = Integer.valueOf(contentArg[10])/100;
		this.buyNum_2 = Integer.valueOf(contentArg[12])/100;
		this.buyNum_3 = Integer.valueOf(contentArg[14])/100;
		this.buyNum_4 = Integer.valueOf(contentArg[16])/100;
		this.buyNum_5 = Integer.valueOf(contentArg[18])/100;
		
		this.buyPrice_1 = Float.valueOf(contentArg[11]);
		this.buyPrice_2 = Float.valueOf(contentArg[13]);
		this.buyPrice_3 = Float.valueOf(contentArg[15]);
		this.buyPrice_4 = Float.valueOf(contentArg[17]);
		this.buyPrice_5 = Float.valueOf(contentArg[19]);
		
		this.sellNum_1 = Integer.valueOf(contentArg[20])/100;
		this.sellNum_2 = Integer.valueOf(contentArg[22])/100;
		this.sellNum_3 = Integer.valueOf(contentArg[24])/100;
		this.sellNum_4 = Integer.valueOf(contentArg[26])/100;
		this.sellNum_5 = Integer.valueOf(contentArg[28])/100;
		
		this.sellPrice_1 = Float.valueOf(contentArg[21]);
		this.sellPrice_2 = Float.valueOf(contentArg[23]);
		this.sellPrice_3 = Float.valueOf(contentArg[25]);
		this.sellPrice_4 = Float.valueOf(contentArg[27]);
		this.sellPrice_5 = Float.valueOf(contentArg[29]);
	}
	
	public boolean isValid() {
		if (vol == 0) {
			return false;
		}
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		int hour = calendar.get(Calendar.HOUR_OF_DAY);
//		int min = calendar.get(Calendar.MINUTE);
//		int currentMin = hour * 60 + min;
//		
//		return  ( currentMin >= (9 * 60 + 30) && currentMin <= (11 * 60 + 30) ) ||
//				( currentMin >= 13 * 60 && currentMin <= 15 * 60 );
		return true;
	}
	
	public String getHead() {
		return String.format(STOCK_HEAD, this.code,  this.name);
	}
	
	public String toString() {
		return String.format(FORMAT_LINE, 
				DATE_FORMAT.format(date),
				sellNum_5, sellNum_4, sellNum_3, sellNum_2, sellNum_1,
				buyNum_1, buyNum_2, buyNum_3, buyNum_4, buyNum_5,
				sellPrice_5, sellPrice_4, sellPrice_3, sellPrice_2, sellPrice_1,
				buyPrice_1, buyPrice_2, buyPrice_3, buyPrice_4, buyPrice_5);
	}
	
	public static void main(String...strings) {
		String content = "var hq_str_sh600056=\"中国医药,13.370,13.400,13.860,13.960,13.300,13.870,13.880,10240833,140200072.000,4400,13.870,36100,13.860,19700,13.850,43800,13.840,23800,13.830,17400,13.880,17300,13.890,72600,13.900,56600,13.910,12534,13.920,2016-01-19,15:00:00,00\"";
		StockWindow window = new StockWindow(content);
		System.out.println(window.getHead());
		System.out.println(window);
	}
}
