package com.stock.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.stock.util.MessageUtil;

@Component
public class StockMessageHolder2 {
	
	public static final boolean getLatest = false;
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	@Autowired
	private StockApiHelper apiHelper;

	private static Map<String, StockMessage> stockHolder = new LinkedHashMap<>();
	
	@PostConstruct
	public void initialize() throws Exception {
		if (getLatest) {
			loadLatestStockMessage();
		} else {
			loadLocalStockMessage();
		}
	}
	
	public static Collection<StockMessage> getAllStocks() {
		return stockHolder.values();
	}
	
	public static StockMessage getStockMessageByCode(String code) {
		return stockHolder.get(code);
	}
	
	private void loadLocalStockMessage() throws IOException {
		Resource stockRes = resolver.getResource("classpath:resource/所有A股.txt");
		if (stockRes == null) {
			return;
		}
		
		List<String> lines = IOUtils.readLines(stockRes.getInputStream(), StockApiHelper.CODE_UTF_8);
		
		for (String line : lines) {
			if (StringUtils.isEmpty(line)) {
				continue;
			}
			String[] columns = line.split("\t");
			StockMessage stock = new StockMessage();
			stock.setCode(columns[0]);
			stock.setName(columns[1]);
			stockHolder.put(stock.getCode(), stock);
		}
	}
	
	private void loadLatestStockMessage() throws Exception {
		List<StockMessage> allStocks = new ArrayList<>();
		int pageIndex = 1;
		for (;;) {
			List<StockMessage> resList = loadLatestStockMessage(pageIndex);
			if (resList.isEmpty()) {
				break;
			} else {
				allStocks.addAll(resList);
			}
			pageIndex ++;
		}
		
		StringBuilder stockBuilder = new StringBuilder();
		for (StockMessage stock : allStocks) {
			stockHolder.put(stock.getCode(), stock);
			
			if (stockBuilder.length() > 0) {
				stockBuilder.append("\n");
			} 
			stockBuilder.append(stock.getCode() + "\t" + stock.getName());
		}
		
		Resource stockRes = resolver.getResource("classpath:resource/所有A股.txt");
		IOUtils.write(stockBuilder.toString().getBytes(), new FileOutputStream(stockRes.getFile()));
		IOUtils.write(stockBuilder.toString().getBytes(), new FileOutputStream(new File("src\\main\\resources\\resource\\所有A股.txt")));
	}
	
	private List<StockMessage> loadLatestStockMessage(int pageIndex) throws Exception {
		List<StockMessage> resultList = new ArrayList<>();
		
		String content = apiHelper.getAllStock(pageIndex);
		int startIndex = content.indexOf("\"items\":[[");
		int endIndex = content.indexOf("]]}])");
		if (startIndex > 0 && endIndex > 0 && endIndex > startIndex) {
			content = content.substring(startIndex + 10, endIndex);
			String[] items = content.split("\\],\\[");
			for (String item : items) {
				String[] fields = item.split(",");
				StockMessage stockMsg = new StockMessage();
				stockMsg.setCode(fields[1].substring(1, fields[1].length()-1));
				
				String name = fields[2].substring(1, fields[2].length()-1);
				name = MessageUtil.unicode2String(name);
				stockMsg.setName(name);
				resultList.add(stockMsg);
			}
		}
		
		return resultList;
	}
}
