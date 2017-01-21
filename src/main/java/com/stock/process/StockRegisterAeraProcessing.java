package com.stock.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.api.StockMessageHolder2;
import com.stock.model.StockMessage;

@Component
public class StockRegisterAeraProcessing implements StockProcessing {
	
	@Autowired
	private StockApiHelper apiHelper;
	@Autowired
	private StockMessageHolder2 stockMsgHolder;
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	private List<String[]> allArea = new ArrayList<>();;

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 99;
	}

	@SuppressWarnings("static-access")
	@Override
	public void process() {
		try {
			Resource urlResource = resolver.getResource("classpath:resource/贫困县.txt");
			List<String> tmpRes = IOUtils.readLines(urlResource.getInputStream());
			for (String tmp : tmpRes) {
				allArea.add(tmp.split("-"));
			}
				
			for (StockMessage stockMsg : stockMsgHolder.getAllStocks()) {
				try {
//					System.out.println("Send stock: " + stockMsg.getCode());
					String content = apiHelper.getStockBase(stockMsg.getCode());
					int startInx = content.indexOf("注册地址：");
					int endInx = content.indexOf("办公地址：");
					content = content.substring(startInx, endInx);
					String aeraStr = getArea(content);
					if (aeraStr != null) {
//						results.add(stockMsg);
						System.out.println(stockMsg.getStockCode() + "\t" + stockMsg.getName() + "\t" + aeraStr);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
//			System.out.println("Print result:");
//			for (StockMessage stockMsg : results) {
//				System.out.println(stockMsg.getStockCode() + "\t" + stockMsg.getName());
//			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	private String getArea(String content) {
		for (String[] aera : allArea) {
			if (content.contains(aera[0]) && content.contains(aera[1])) {
				return aera[0] + "-" + aera[1];
			}
		}
		return null;
	}

}
