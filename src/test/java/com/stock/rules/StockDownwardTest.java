package com.stock.rules;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stock.model.StockHistory;
import com.stock.model.StockMessage;
import com.stock.repositories.StockHistoryRepository;
import com.stock.repositories.StockMessageRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockDownwardTest {

	@Autowired
	private StockHistoryRepository stockHisRepo;
	
	@Autowired
	private StockMessageRepository stockMsgRepo;
	
	@Test
	public void selectAll() {
		List<StockHistory> allHis = stockHisRepo.findAllByCode("000568");
		StockDownward stockDown = new StockDownward(allHis);
		stockDown.select();
		stockDown.print();
	}
	
	
	@Test
	public void selectBetween() throws ParseException {
		Timestamp timeStart = new Timestamp(StockDownward.smpFormat.parse("2015-12-01").getTime());
		Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
		List<StockHistory> allHis = stockHisRepo.findAllByCodeAndDateBetween("300070", timeStart, timeEnd);
		StockDownward stockDown = new StockDownward(allHis);
		stockDown.select();
		stockDown.print();
	}
	
	@Test
	public void selectLatest() throws ParseException {
		Timestamp timeStart = new Timestamp(StockDownward.smpFormat.parse("2015-12-01").getTime());
		Timestamp timeEnd = new Timestamp(System.currentTimeMillis());
//		Timestamp timeEnd = new Timestamp(StockDownward.smpFormat.parse("2015-11-01").getTime());
		
		/*Timestamp comparDate = new Timestamp(StockDownward.smpFormat.parse("2015-01-01").getTime());*/
		
		List<StockDownward> allStockDowns = new ArrayList<>();
		List<StockMessage> allStocks = stockMsgRepo.findAll();
		for (StockMessage stockMsg : allStocks) {
			if (stockMsg.getCirculation() > 10 * 10000f || stockMsg.getCirculation() < 1 * 10000f) {
				continue;
			}
			
			List<StockHistory> allHis = stockHisRepo.findAllByCodeAndDateBetween(stockMsg.getCode(), timeStart, timeEnd);
			StockDownward stockDown = new StockDownward(allHis);
			stockDown.select();
			
			if (stockDown.getPotential() < -46f /*&& stockDown.getPotential()<=2f*/ /*&& stockDown.getLowPoint().getDate().after(comparDate)*/) {
				allStockDowns.add(stockDown);
			}
		}
		
		Collections.sort(allStockDowns, new Comparator<StockDownward>() {

			@Override
			public int compare(StockDownward o1, StockDownward o2) {
				if ( o2.getPerVol() > o1.getPerVol() ) {
					return -1;
				} else {
					return 1;
				}
			}
			
		});
		
		System.out.println("总共:" + allStockDowns.size());
		
		for (StockDownward down : allStockDowns) {
			down.print();
			System.out.println("");
		}
		
//		if (!allStockDowns.isEmpty()) {
//			allStockDowns.get(0).print();
//			System.out.println("");
//			allStockDowns.get(1).print();
//			System.out.println("");
//			allStockDowns.get(2).print();
//			System.out.println("");
//		}
		
		
	}
}
