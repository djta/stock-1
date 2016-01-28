package com.stock.process;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.api.StockMessageHolder;
import com.stock.model.StockMessage;

@Component
public class StockExchangeDetailCatchProcessing implements StockProcessing {

	@Autowired private StockMessageHolder stockHolder;
	@Autowired private StockApiHelper stockApi;

	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		Collection<StockMessage> allStocks = stockHolder.getAllStocks();
		for (StockMessage stock : allStocks) {
			handleOneStock(stock);
		}
	}
	
	private void handleOneStock(StockMessage stock) {
		String exchangeContent = stockApi.getStockExchangeDetail(stock.getCode());
		if (exchangeContent == null) {
			return;
		}
		
		exchangeContent = exchangeContent.replace("\",\"", "\n").replace("\"", "");
		
	}
}
