package com.stock.process;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.api.StockMessageHolder;
import com.stock.model.StockMessage;
import com.stock.util.MessageUtil;
import com.stock.util.ZipUploadUtil;

@Component
public class StockExchangeDetailCatchProcessing implements StockProcessing {
	
	@Value("${file.path.stock.exchange}")
	private String filePathExchange;
	
	@Value("${folder.path.stock.exchange}")
	private String folderPathExchange;

	@Autowired private StockMessageHolder stockHolder;
	@Autowired private StockApiHelper stockApi;
	@Autowired private ReloadableResourceBundleMessageSource messageResource;
	
	private String dateStr;

	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		dateStr = StockWindowCatchProcessing.DATE_FORMAT.format(new Date());
		Collection<StockMessage> allStocks = stockHolder.getAllStocks();
		for (StockMessage stock : allStocks) {
			handleOneStock(stock);
		}
		
		String folderPath = MessageUtil.getMessage("folder.path.stock.exchange", dateStr);
		ZipUploadUtil.zipUpload(folderPath);
	}
	
	private void handleOneStock(StockMessage stock) {
		String exchangeContent = stockApi.getStockExchangeDetail(stock.getCode());
		if (exchangeContent == null) {
			return;
		}
		
		String headStr = MessageUtil.getMessage("stock.exchange.head", dateStr, stock.getName(), stock.getCode());
		exchangeContent = headStr + exchangeContent.replace("\",\"", "\n").replace("\"", "").replace(",", " ");
		String filePath = MessageUtil.getMessage("file.path.stock.exchange", dateStr, stock.getCode());
		try {
			FileUtils.write(new File(filePath), exchangeContent, StockApiHelper.CODE_CN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
