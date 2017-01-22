package com.stock.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.api.StockMessageHolder;
import com.stock.model.StockHolder;
import com.stock.model.StockHolderItem;
import com.stock.model.StockMessage;
import com.stock.util.MessageParseUtil;

@Component
public class StockHoldersProcessing implements StockProcessing {
	
	@Autowired
	private StockApiHelper apiHelper;
	@Autowired
	private StockMessageHolder stockMsgHolder;

	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		List<StockHolder> allHolders = new ArrayList<>();
		
		int i=0;
		for (StockMessage stockMsg : stockMsgHolder.getAllStocks() ) {
			allHolders.add(generateHolder(stockMsg));
//			i++;
//			if (i == 100) {
//				break;
//			}
		}
		
		Collections.sort(allHolders, new Comparator<StockHolder>(){
			@Override
			public int compare(StockHolder o1, StockHolder o2) {
				return (int)((o1.getDif(2) - o2.getDif(2)) * 1000);
			}
			
		});
		
		for (StockHolder holder : allHolders) {
			System.out.println(holder);
		}
	}
	
	private StockHolder generateHolder(StockMessage stock) {
		StockHolder holder = new StockHolder();
		
		holder.setStockMessage(stock);
		
		System.out.println(stock.getStockCode());
		String content = apiHelper.getStockHolders(stock.getStockCode());
		String tableStr = content.substring(content.indexOf("<table id=\"Table0\">"));
		tableStr = tableStr.substring(0, tableStr.indexOf("</table>")) + "</table>";
		
		List<StockHolderItem> holders = null;
		try {
			holders = MessageParseUtil.parseStockHolders(tableStr);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		holder.setHolderHis(holders);
		
		return holder;
	}

}
