package com.stock.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockApiHelper {
	
	//Get stock message 
	private static final String GET_STOCK_CODE = "http://quote.eastmoney.com/stocklist.html";
	
	private static final String GET_STOCK_HIS = "http://table.finance.yahoo.com/table.csv?s=%s";
	
	public static final String GET_STOCK_CURRENT = "http://hq.sinajs.cn/list=%s";
	
	private static final String CODE_CN = "gb2312";
	private static final String CODE_UTF_8 = "UTF-8";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	public String getAllStockMessages() {
		return getHttpResponse(GET_STOCK_CODE, CODE_CN);
	}
	
	public String getAllStockHistory(String code) {
		return getHttpResponse(String.format(GET_STOCK_HIS, code), CODE_UTF_8);
	}
	
	public String getTodayMessage(String code) {
		return getHttpResponse(String.format(GET_STOCK_CURRENT, code), CODE_CN);
	}
	
	public String getCurrentMessageAll() {
		try {
			Resource urlResource = resolver.getResource("classpath:url.txt");
			List<String> urls = IOUtils.readLines(urlResource.getInputStream());
			StringBuilder allStocks = new StringBuilder();
			for (String url : urls) {
				if (allStocks.length() > 0) {
					allStocks.append("\n");
				}
				allStocks.append(getHttpResponse(url, CODE_CN));
			}
			return allStocks.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getHttpResponse(String url, String enCoding) {
		byte[] res = restTemplate.getForObject(url, byte[].class);
		try {
			return new String(res, enCoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
