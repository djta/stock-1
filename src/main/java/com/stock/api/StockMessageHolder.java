package com.stock.api;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.model.StockMessage;
import com.stock.model.StockMessage.StockDomain;

@Component
public class StockMessageHolder {
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;

	private Map<String, StockMessage> stockHolder = new LinkedHashMap<>();
	
	@PostConstruct
	public void initialize() throws IOException {
		Resource stockRes = resolver.getResource("classpath:stock_name.txt");
		if (stockRes == null) {
			return;
		}
		
		List<String> lines = IOUtils.readLines(stockRes.getInputStream(), StockApiHelper.CODE_UTF_8);
		
		for (String line : lines) {
			if (StringUtils.isEmpty(line)) {
				continue;
			}
			String[] columns = line.split(" ");
			StockMessage stock = new StockMessage();
			stock.setCode(columns[0]);
			stock.setName(columns[1]);
			stock.setDomain(StockDomain.valueOf(columns[2]));
			stockHolder.put(stock.getCode(), stock);
		}
	}
	
	public Collection<StockMessage> getAllStocks() {
		return stockHolder.values();
	}
	
	public StockMessage getStockMessageByCode(String code) {
		return stockHolder.get(code);
	}
}
