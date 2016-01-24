package com.stock.model.builder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hibernate.tool.hbm2x.StringUtils;
import org.springframework.stereotype.Component;

import com.stock.model.StockWindow;

@Component
public class StockWindowBuilder {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public StockWindow buildInstanceFromApi(String content) {
		if (StringUtils.isEmpty(content)) {
			return null;
		}
		
		StockWindow stWindow = new StockWindow();
		
		stWindow.setCode(content.substring(13, 19));
		int start = content.indexOf("\"");
		int end = content.lastIndexOf("\"");
		String argStr = content.substring(start + 1, end);
		String[] contentArg = argStr.split(",");
		
		if (contentArg.length < 30) {
			return null;
		}
		
		stWindow.setName(contentArg[0]);
		stWindow.setTodayOpen(Float.valueOf(contentArg[1]));
		stWindow.setYesterdayClose(Float.valueOf(contentArg[2]));
		stWindow.setCurrentPrice(Float.valueOf(contentArg[3]));
		stWindow.setVol(Long.valueOf(contentArg[8]));
		try {
			stWindow.setDate(DATE_FORMAT.parse(contentArg[30] + " " + contentArg[31]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		stWindow.setBuyNum_1(Integer.valueOf(contentArg[10])/100);
		stWindow.setBuyNum_2(Integer.valueOf(contentArg[12])/100);
		stWindow.setBuyNum_3(Integer.valueOf(contentArg[14])/100);
		stWindow.setBuyNum_4(Integer.valueOf(contentArg[16])/100);
		stWindow.setBuyNum_5(Integer.valueOf(contentArg[18])/100);
		
		stWindow.setBuyPrice_1(Float.valueOf(contentArg[11]));
		stWindow.setBuyPrice_2(Float.valueOf(contentArg[13]));
		stWindow.setBuyPrice_3(Float.valueOf(contentArg[15]));
		stWindow.setBuyPrice_4(Float.valueOf(contentArg[17]));
		stWindow.setBuyPrice_5(Float.valueOf(contentArg[19]));
		
		stWindow.setSellNum_1(Integer.valueOf(contentArg[20])/100);
		stWindow.setSellNum_2(Integer.valueOf(contentArg[22])/100);
		stWindow.setSellNum_3(Integer.valueOf(contentArg[24])/100);
		stWindow.setSellNum_4(Integer.valueOf(contentArg[26])/100);
		stWindow.setSellNum_5(Integer.valueOf(contentArg[28])/100);
		
		stWindow.setSellPrice_1(Float.valueOf(contentArg[21]));
		stWindow.setSellPrice_2(Float.valueOf(contentArg[23]));
		stWindow.setSellPrice_3(Float.valueOf(contentArg[25]));
		stWindow.setSellPrice_4(Float.valueOf(contentArg[27]));
		stWindow.setSellPrice_5(Float.valueOf(contentArg[29]));
		
		return stWindow;
	}
}
