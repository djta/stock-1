package com.stock.process;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.model.StockHistory;
import com.stock.model.StockMessage;
import com.stock.model.StockMessage.StockDomain;
import com.stock.repositories.StockHistoryRepository;
import com.stock.repositories.StockMessageRepository;

@Component
public class StockHistoryUpdateProcessing implements StockProcessing {
	
	@Autowired private StockHistoryRepository historyRep;
	@Autowired private StockMessageRepository messageRep;
	@Autowired private StockApiHelper apiHelper;
	
	private DateFormat smpFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public int getPriority() {
		return 20;
	}

	@Override
	public void process() {
		List<StockMessage> allStocks = messageRep.findAllOrderById();
//		List<StockMessage> allStocks = Collections.singletonList(messageRep.findOne((long)127));
		int index = 0;
		for (StockMessage stock : allStocks) {
			index ++;
//			if (stock.isFinished()) {
//				continue;
//			} 
//			else if (!(index >= 2800 && index <=3000)) {
//				continue;
//			}
			try {
				List<StockHistory> allHis = getAllStockHistory(stock);
				historyRep.save(allHis);
//				stock.setFinished(true);
				messageRep.saveAndFlush(stock);
				System.out.println(stock.getCode());
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println(stock.getCode() + " " + e.getMessage());
				continue;
			}
		}
		historyRep.flush();
	}
	
	public List<StockHistory> getAllStockHistory(StockMessage stockMsg) throws ParseException {
		String code = stockMsg.getCode() + ".";
		code += stockMsg.getDomain() == StockDomain.SH ? "ss" : "sz";
		List<StockHistory> resList = new ArrayList<>();
		
		String cvsStr = apiHelper.getAllStockHistory(code);
		String[] lines = cvsStr.split("\n");
		for (int i=1; i<lines.length; i++) {
			String[] columns = lines[i].split(",");
			long volume = Long.valueOf(columns[5]);
			if (volume == 0) {
				continue;
			}
			
			StockHistory history = new StockHistory();
			history.setCode(stockMsg.getCode());
			history.setDate(new Timestamp(smpFormat.parse(columns[0]).getTime()));
			history.setOpen(Float.valueOf(columns[1]));
			history.setHigh(Float.valueOf(columns[2]));
			history.setLow(Float.valueOf(columns[3]));
			history.setClose(Float.valueOf(columns[4]));
			history.setVolume(volume);
//			history.setAdjClose(Float.valueOf(columns[6]));
			
			resList.add(history);
		}
		
		Collections.sort(resList);
		return resList;
	}

}
