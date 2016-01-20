package com.stock.process;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.model.StockWindow;

@Component
public class StockWindowCatchProcessing implements StockProcessing {
	
	private static final String STOCK_PK_FILE_PATH = "E:\\stock_data\\pan_kou\\%s\\%s.pk";
	
	@Autowired
	private StockApiHelper apiHelper;
	
	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		String content = apiHelper.getCurrentMessageAll();
		String[] contentArg = content.split("\n");
		
		for (String line : contentArg) {
			try{
				StockWindow stockWin = new StockWindow(line);
				if (stockWin.isValid() == false) {
					continue;
				}
				
				String filePath = String.format(STOCK_PK_FILE_PATH, getYearMonth(), stockWin.getCode());
				File stockPk = new File(filePath);
				if (stockPk.exists() == false) {
					FileUtils.write(stockPk, stockWin.getHead(), true);
				}
				
				FileUtils.write(stockPk, stockWin.toString(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getYearMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		
		return year + "-" + month;
	}

}
