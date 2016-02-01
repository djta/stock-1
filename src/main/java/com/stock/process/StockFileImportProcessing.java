package com.stock.process;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.model.StockHistory;
import com.stock.model.StockMessage;
import com.stock.repositories.StockHistoryRepository;
import com.stock.repositories.StockMessageRepository;

@Slf4j
@Component
public class StockFileImportProcessing implements StockProcessing {
	
	private DateFormat smpFormat = new SimpleDateFormat("yyyy/MM/dd");

	@Autowired private StockHistoryRepository historyRep;
	@Autowired private StockMessageRepository messageRep;
	
	@Autowired private PathMatchingResourcePatternResolver resolver;
	
	@Override
	public int getPriority() {
		return 99;
	}
	@Override
	public void process() {
		Resource[] allFiles;
		try {
			allFiles = resolver.getResources("file:D:/new_tdx/T0002/export/*.txt");
		} catch (IOException e) {
			log.error("文件不存在");
			return;
		}
		
		if (allFiles == null || allFiles.length ==0) {
			return;
		}
		
		for (Resource resource : allFiles) {
			try {
				System.out.println(resource.getFilename());
				String fileContent = IOUtils.toString(resource.getInputStream(), "gb2312");
				List<StockHistory> allHis = parseStockHistory(fileContent);
				
				historyRep.save(allHis);
				historyRep.flush();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void importStockMessage() throws IOException {
		Resource[] resource = resolver.getResources("file:D:/new_tdx/T0002/export/沪深Ａ股*.txt");
		
		System.out.println(resource[0].getFilename());
		List<String> lines = IOUtils.readLines(resource[0].getInputStream(), "gb2312");
		for (String line : lines) {
			String[] columns = line.split("\t");
			if (columns.length == 0 || columns[0].matches("\\d+") == false) {
				System.out.println(line);
				continue;
			}
			
			StockMessage stockMsg = new StockMessage();
			stockMsg.setCode(columns[0].trim());
			stockMsg.setName(columns[1].trim());
			try {
				stockMsg.setCirculation(Float.valueOf(columns[2]));
			} catch (Exception e) {
				stockMsg.setCirculation(0.0001f);
			}
			messageRep.saveAndFlush(stockMsg);
		}
	}
	
	private List<StockHistory> parseStockHistory(String fileContent) {
		List<StockHistory> allHis = new ArrayList<>();
		
		String[] lines = fileContent.split("\n");
		int index = 0;
		String code = null, stockName = null;
		for (String line : lines) {
			String[] columns = line.split(" ");
			
			index ++;
			if (index == 1) {
				code = columns[0];
				stockName = columns[1];
				continue;
			} else if (index == 2) {
				continue;
			}
			
			if (columns.length >= 7) {
				try {
					StockHistory stockHis = new StockHistory();
					stockHis.setCode(code);
					stockHis.setName(stockName);
					stockHis.setDate(new Timestamp(smpFormat.parse(columns[0]).getTime()));
					stockHis.setOpen(Float.valueOf(columns[1]));
					stockHis.setHigh(Float.valueOf(columns[2]));
					stockHis.setLow(Float.valueOf(columns[3]));
					stockHis.setClose(Float.valueOf(columns[4]));
					stockHis.setVolume(Long.valueOf(columns[5]));
					allHis.add(stockHis);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		return allHis;
	}
}
