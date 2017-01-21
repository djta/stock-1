package com.stock.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.stock.model.StockHolderItem;

public class MessageParseUtil {
	
	private static final DateFormat SimpDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final SAXReader READER = new SAXReader();
	
	@SuppressWarnings("unchecked")
	public static List<StockHolderItem> parseStockHolders(String data) throws Exception {
		Document document = READER.read(new StringInputStream(data));
		List<Element> allTrs = document.getRootElement().elements();
		
		List<StockHolderItem> allItems = new ArrayList<>();
		
		//Parse date
		List<String> dateList = getTrContent(allTrs.get(0));
		for (int i=1; i<dateList.size(); i++) {
			StockHolderItem item = new StockHolderItem();
			String str = "20" + dateList.get(i);
			item.setReleaseDate(SimpDateFormat.parse(str));
			allItems.add(item);
		}
		
		//Parse stock holder num
		List<String> holderList = getTrContent(allTrs.get(1));
		for (int i=1; i<holderList.size(); i++) {
			StockHolderItem item = allItems.get(i-1);
			String content = holderList.get(i);
			double num;
			if (content.endsWith("万")) {
				num = Double.parseDouble(content.replace("万", "")) * 10000;
			} else {
				num = Double.parseDouble(content);
			}
			item.setPeopleNum(num);
		}
		
		//Parse stock holder num
		List<String> stockNum = getTrContent(allTrs.get(3));
		for (int i=1; i<stockNum.size(); i++) {
			StockHolderItem item = allItems.get(i-1);
			String content = stockNum.get(i);
			double num;
			if (content.endsWith("万")) {
				num = Double.parseDouble(content.replace("万", "")) * 10000;
			} else {
				num = Double.parseDouble(content);
			}
			item.setStockPerPeople(num);
		}
		
		return allItems;
	}
	
	private static List<String> getTrContent(Element element) {
		List<String> res = new ArrayList<>();
		
		for (Object obj : element.elements()) {
			Element ele = (Element)obj;
			res.add(ele.getText());
		}
		return res;
	}

}
