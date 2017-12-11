package com.stock.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JsoupUtilTest {
	
	private List<String> codes_A;
	
	@Before
	public void init() throws IOException {
		String filePath = "e:\\stock_data\\level_a.txt";
		String restr = FileUtils.readFileToString(new File(filePath));
		String[] args = restr.split("\r\n");
		codes_A = Arrays.asList(args);
	}

	@Test
	public void test_1() throws IOException {
		String urlTemp = "http://data.eastmoney.com/DataCenter_V3/gdhs/GetList.ashx?reportdate=&market=&changerate==&range==&pagesize=1000&page=%d";
		Response resp = null;
		
		List<StockHolder> resultList = new ArrayList<>();
		
		for (int i=1;;i++) {
			String url = String.format(urlTemp, i);
			resp = Jsoup.connect(url).execute();
			JSONObject obj1 = JSONObject.fromObject(resp.body());
			JSONArray jonArr = obj1.getJSONArray("data");
			if (jonArr.size() == 0) {
				break;
			}
			
			for (int index =0; index<jonArr.size(); index++) {
				JSONObject objItem = jonArr.getJSONObject(index);
				resultList.add(new StockHolder(objItem.getString("SecurityCode"), 
						objItem.getString("SecurityName"),
						objItem.getString("HolderNumChangeRate")));
			}
		}
		
		List<StockHolder> sultList = resultList.stream()
			.filter(stock -> stock.getChangeRateNum() < 0)
			.filter(stock -> codes_A.contains(stock.getCode()))
			.sorted(Comparator.comparing(StockHolder :: getChangeRateNum))
			.collect(Collectors.toList());
		
//		System.out.println(sultList.size());
		sultList.forEach(System.out :: println);
//		sultList.forEach(stock -> System.out.println(stock.getCode()));
		
	}
	
	private String fill_0(String inputStr) {
		if (inputStr.trim().length() >= 6) {
			return inputStr;
		}
		
		String newStr = "0" + inputStr;
		return fill_0(newStr);
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	public static class StockHolder {
		private String code;
		private String name;
		private String changeRate;
		
		@Override
		public String toString() {
			return String.format("Code: %s   Name: %s    Rate: %s", code, name, changeRate);
		}
		
//		@Override
//		public int hashCode(){
//			return code.hashCode();
//		}
//		
//		@Override
//		public boolean equals(Object s){
//			return code.equals(((StockHolder)s).getCode());
//		}
		
		public double getChangeRateNum() {
			try {
				return Double.parseDouble(changeRate);
			}catch(Exception e) {
				return 0;
			}
		}
		
		
	}
}
