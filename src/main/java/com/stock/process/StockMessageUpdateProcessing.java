package com.stock.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.model.StockMessage;
import com.stock.model.StockMessage.StockDomain;
import com.stock.repositories.StockMessageRepository;

@Component
public class StockMessageUpdateProcessing implements StockProcessing {
	
	@Autowired private StockMessageRepository repository;
	@Autowired private StockApiHelper stockApi;
	
	private Pattern stockPatten = Pattern.compile("<li><a target=\"_blank\" href=\"http://quote.eastmoney.com/(\\S+).html\">(\\S+)\\((\\d+)\\)</a></li>");

	public int getPriority() {
		return 10;
	}

	@Override
	public void process() {
		List<StockMessage> allStockMsgs = repository.findAll();
		Set<String> codeSet = new HashSet<>();
		for (StockMessage stock : allStockMsgs) {
			codeSet.add(stock.getCode());
		}
		
		List<StockMessage> latestStocks = getAllStockMessage();
		Iterator<StockMessage> latestIte = latestStocks.iterator();
		while (latestIte.hasNext()){
			StockMessage stock = latestIte.next();
			if ( codeSet.contains(stock.getCode()) ) {
				//已经存在了
				latestIte.remove();
			}
		}
		
		repository.save(latestStocks);
		repository.flush();
	}

	private List<StockMessage> getAllStockMessage() {
		Matcher matcher = stockPatten.matcher(stockApi.getAllStockMessages());
		List<StockMessage> latestStocks = new ArrayList<>();
		while (matcher.find()) {
			StockMessage stock = new StockMessage();
			stock.setCode(matcher.group(3));
			stock.setName(matcher.group(2));
			String domainStr = matcher.group(1);
			if (domainStr.startsWith("sh")) {
				stock.setDomain(StockDomain.SH);
			} else if (domainStr.startsWith("sz")) {
				stock.setDomain(StockDomain.SZ);
			}
			
			latestStocks.add(stock);
		}
		
		return latestStocks;
	}
}
