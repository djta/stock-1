package com.stock.api;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stock.model.StockMessage;
import com.stock.model.StockMessage.StockDomain;
import com.stock.model.StockWindow;
import com.stock.repositories.StockMessageRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockApiHelperTest {

	@Autowired
	private StockApiHelper apiHelper;
	
	@Autowired
	private StockMessageRepository stockRep;
	
	@Test
	public void getAllStockMessagesTest() {
		String response = apiHelper.getAllStockMessages();
		System.out.println(response);
	}
	
	@Test
	public void getAllStockHistoryTest() {
		String response = apiHelper.getAllStockHistory("300399.sz");
		System.out.println(response);
	}
	
	@Test
	public void removeInvalidStock() {
		List<StockMessage> allStocks = stockRep.findAll();
		for (StockMessage stock : allStocks) {
			String stockCode = stock.getDomain() == StockDomain.SH ? "sh" : "sz";
			stockCode += stock.getCode();
			String response = apiHelper.getTodayMessage(stockCode);
			if (response.length() < 100 ) {
				stockRep.delete(stock.getId());
			}
		}
		
	}
	
	@Test
	public void getAllStockUrl() {
		List<StockMessage> allStock = stockRep.findAll();
		StringBuilder sbuilder = new StringBuilder();
		int index = 0;
		System.out.println(allStock.size());
		for (StockMessage stock : allStock) {
			if (index > 0) {
				sbuilder.append(",");
			}
			sbuilder.append(stock.getStockCode());
			
			if (index++ == 800) {
				System.out.println(String.format(StockApiHelper.GET_STOCK_CURRENT, sbuilder.toString()));
				index = 0;
				sbuilder.setLength(0);
			}
		}
		
		System.out.println(String.format(StockApiHelper.GET_STOCK_CURRENT, sbuilder.toString()));
	}
	
	@Test
	public void getCurrentMessageAll() throws FileNotFoundException, IOException{
		String content = apiHelper.getCurrentMessageAll();
		IOUtils.write(content, new FileOutputStream("d:\\stock.pk"), "gb2312");
		String[] contentArg = content.split("\n");
		System.out.println(contentArg.length);
	}
	
	@Test
	public void getAllXipan() throws FileNotFoundException, IOException{
		String content = apiHelper.getCurrentMessageAll();
		String[] contentArg = content.split("\n");
		for (String line : contentArg) {
			try {
				StockWindow stockWin = new StockWindow(line);
				if (stockWin.isValid() == false) {
					continue;
				}
				
				if (stockWin.getBuyNum_2() + stockWin.getBuyNum_3() + stockWin.getBuyNum_4() + stockWin.getBuyNum_5() > 10000 && 
						stockWin.getSellNum_2() + stockWin.getSellNum_3() + stockWin.getSellNum_4() + stockWin.getSellNum_5() > 10000 ) {
					System.out.println(stockWin.getHead());
					System.out.println(stockWin);
					System.out.println("");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(line);
				break;
			}
			
		}
	}
}
