package com.stock.model.builder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.tool.hbm2x.StringUtils;
import org.springframework.stereotype.Component;

import com.stock.model.StockWindow;

@Component
public class StockWindowBuilder {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private List<StockWindowParser> allParsers = new ArrayList<>();
	
	@PostConstruct
	public void register() {
		allParsers.add(new StockWindowParserSina());
		allParsers.add(new StockWindowParserOld());
		allParsers.add(new StockWindowParserCompact());
	}
	
	
	public StockWindow buildInstance(String content) {
		if (StringUtils.isEmpty(content)) {
			return null;
		}
		
		for (StockWindowParser parser : allParsers) {
			if (parser.isMatch(content)) {
				return parser.parse(content);
			}
		}
		
		return null;
	}
	
	private interface StockWindowParser {
		boolean isMatch(String content);
		StockWindow parse(String content);
	}
	
	private class StockWindowParserSina implements StockWindowParser {

		@Override
		public boolean isMatch(String content) {
			return content.startsWith("var");
		}

		@Override
		public StockWindow parse(String content) {
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
	
	private class StockWindowParserCompact implements StockWindowParser {

		@Override
		public boolean isMatch(String content) {
			return content.indexOf("|") == 20 && content.split("\\|").length == 9;
		}

		@Override
		public StockWindow parse(String content) {
			String[] columns = content.split("\\|");
			for (int i=0; i<columns.length; i++) {
				columns[i] = columns[i].trim();
			}
			
			StockWindow stWindow = new StockWindow();
			
			try {
				stWindow.setDate(DATE_FORMAT.parse(columns[0]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			stWindow.setCurrentPrice(Float.valueOf(columns[1]));
			stWindow.setYesterdayClose(Float.valueOf(columns[7]));
			stWindow.setTodayOpen(Float.valueOf(columns[8]));
			
			String[] params;
			String[] sellInfos = columns[3].split(" ");
			params = parsePriceAndCount(sellInfos[0]);
			stWindow.setSellPrice_5(Float.valueOf(params[0]));
			stWindow.setSellNum_5(Integer.valueOf(params[1]));
			params = parsePriceAndCount(sellInfos[1]);
			stWindow.setSellPrice_4(Float.valueOf(params[0]));
			stWindow.setSellNum_4(Integer.valueOf(params[1]));
			params = parsePriceAndCount(sellInfos[2]);
			stWindow.setSellPrice_3(Float.valueOf(params[0]));
			stWindow.setSellNum_3(Integer.valueOf(params[1]));
			params = parsePriceAndCount(sellInfos[3]);
			stWindow.setSellPrice_2(Float.valueOf(params[0]));
			stWindow.setSellNum_2(Integer.valueOf(params[1]));
			params = parsePriceAndCount(sellInfos[4]);
			stWindow.setSellPrice_1(Float.valueOf(params[0]));
			stWindow.setSellNum_1(Integer.valueOf(params[1]));
			
			String[] buyInfos = columns[4].split(" ");
			params = parsePriceAndCount(buyInfos[0]);
			stWindow.setBuyPrice_1(Float.valueOf(params[0]));
			stWindow.setBuyNum_1(Integer.valueOf(params[1]));
			params = parsePriceAndCount(buyInfos[1]);
			stWindow.setBuyPrice_2(Float.valueOf(params[0]));
			stWindow.setBuyNum_2(Integer.valueOf(params[1]));
			params = parsePriceAndCount(buyInfos[2]);
			stWindow.setBuyPrice_3(Float.valueOf(params[0]));
			stWindow.setBuyNum_3(Integer.valueOf(params[1]));
			params = parsePriceAndCount(buyInfos[3]);
			stWindow.setBuyPrice_4(Float.valueOf(params[0]));
			stWindow.setBuyNum_4(Integer.valueOf(params[1]));
			params = parsePriceAndCount(buyInfos[4]);
			stWindow.setBuyPrice_5(Float.valueOf(params[0]));
			stWindow.setBuyNum_5(Integer.valueOf(params[1]));
			
			return stWindow;
		}
		
		private String[] parsePriceAndCount(String infoStr) {
			String[] infoRes = new String[2];
			int index = infoStr.indexOf("(");
			infoRes[0] = infoStr.substring(0, index);
			infoRes[1] = infoStr.substring(index + 1, infoStr.length() - 1);
			return infoRes;
		}
		
	}
	
	private class StockWindowParserOld implements StockWindowParser {

		@Override
		public boolean isMatch(String content) {
			return content.indexOf("|") == 20 && content.split("\\|").length == 5;
		}

		@Override
		public StockWindow parse(String content) {
			String[] columns = content.split("\\|");
			for (int i=0; i<columns.length; i++) {
				columns[i] = columns[i].trim();
			}
			
			StockWindow stWindow = new StockWindow();
			
			try {
				stWindow.setDate(DATE_FORMAT.parse(columns[0]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String[] sellNums = columns[1].split(" ");
			stWindow.setSellNum_5(Integer.valueOf(sellNums[0]));
			stWindow.setSellNum_4(Integer.valueOf(sellNums[1]));
			stWindow.setSellNum_3(Integer.valueOf(sellNums[2]));
			stWindow.setSellNum_2(Integer.valueOf(sellNums[3]));
			stWindow.setSellNum_1(Integer.valueOf(sellNums[4]));
			
			String[] buyNums = columns[2].split(" ");
			stWindow.setBuyNum_1(Integer.valueOf(buyNums[0]));
			stWindow.setBuyNum_2(Integer.valueOf(buyNums[1]));
			stWindow.setBuyNum_3(Integer.valueOf(buyNums[2]));
			stWindow.setBuyNum_4(Integer.valueOf(buyNums[3]));
			stWindow.setBuyNum_5(Integer.valueOf(buyNums[4]));
			
			String[] sellPrices = columns[3].split(" ");
			stWindow.setSellPrice_5(Float.valueOf(sellPrices[0]));
			stWindow.setSellPrice_4(Float.valueOf(sellPrices[1]));
			stWindow.setSellPrice_3(Float.valueOf(sellPrices[2]));
			stWindow.setSellPrice_2(Float.valueOf(sellPrices[3]));
			stWindow.setSellPrice_1(Float.valueOf(sellPrices[4]));
			
			String[] buyPrices = columns[4].split(" ");
			stWindow.setBuyPrice_1(Float.valueOf(buyPrices[0]));
			stWindow.setBuyPrice_2(Float.valueOf(buyPrices[1]));
			stWindow.setBuyPrice_3(Float.valueOf(buyPrices[2]));
			stWindow.setBuyPrice_4(Float.valueOf(buyPrices[3]));
			stWindow.setBuyPrice_5(Float.valueOf(buyPrices[4]));
			
			return stWindow;
		}
		
	}
}
