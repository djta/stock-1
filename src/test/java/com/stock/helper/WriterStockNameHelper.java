package com.stock.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stock.model.StockMessage;
import com.stock.repositories.StockMessageRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class WriterStockNameHelper {

	@Autowired
	private StockMessageRepository stMsgRep;
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	@Test
	public void writeStockNameFile() throws IOException {
		String filePath = "src/main/resources/stock_name.txt";
		List<StockMessage> allStocks = stMsgRep.findAll();
		
		StringBuilder content = new StringBuilder();
		for (StockMessage stock : allStocks) {
			if (content.length() > 0) {
				content.append("\n");
			}
			content.append(String.format("%s\t%s\t%s\t%s", stock.getCode(), stock.getName(), stock.getDomain(), stock.getCirculation()));
		}
		
		FileUtils.write(new File(filePath), content.toString());
	}
}
