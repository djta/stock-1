package com.stock.process;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.model.StockWindow;
import com.stock.model.builder.StockWindowBuilder;

@Component
public class StockWindowCatchProcessing implements StockProcessing {
	
	private static final String STOCK_PK_FILE_PATH = "E:\\stock_data\\pan_kou\\%s\\%s.pk";
	
	@Autowired
	private StockApiHelper apiHelper;
	@Autowired
	private StockWindowBuilder stockWindowBuilder;
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	private Calendar calendar = Calendar.getInstance();
	
	private Map<String, Date> latestRecords = new HashMap<>();
	
	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		if (inValidTime() == false) {
			return;
		}
		
		String content = apiHelper.getCurrentMessageAll();
		String[] contentArg = content.split("\n");
		
		for (String line : contentArg) {
			try{
				StockWindow stockWin = stockWindowBuilder.buildInstanceFromApi(line);
				if (stockWin == null || stockWin.isValid() == false) {
					continue;
				}
				
				String filePath = String.format(STOCK_PK_FILE_PATH, getYearMonth(), stockWin.getCode());
				File stockFile = new File(filePath);
				if (stockFile.exists() == false) {
					FileUtils.write(stockFile, stockWin.getHead(), true);
				} else if (latestRecords.containsKey(stockWin.getCode()) == false) {
					Date lastDate = getLastDate(filePath);
					latestRecords.put(stockWin.getCode(), lastDate);
				}
				
				if ( stockWin.getDate().equals(latestRecords.get(stockWin.getCode())) ) {
					//reduplicate data
					continue;
				}
				
				if (isNewDate(latestRecords.get(stockWin.getCode()), stockWin.getDate())) {
					FileUtils.write(stockFile, "\n", true);
				}
				
				FileUtils.write(stockFile, stockWin.toString(), true);
				latestRecords.put(stockWin.getCode(), stockWin.getDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getYearMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		
		return year + "-" + month;
	}
	
	private boolean inValidTime() {
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int currentMin = hour * 60 + min;
		
		int startAm = 9 * 60 + 30 - 10;
		int endAm = 11 * 60 + 30 + 10;
		int startPm = 13 * 60 - 10;
		int endPm = 15 * 60 + 10;
		return  ( currentMin >= startAm && currentMin <= endAm ) ||
				( currentMin >= startPm && currentMin <= endPm );
	}
	
	private Date getLastDate(String filePath) {
		try {
			Resource res = resolver.getResource("file:" + filePath);
			List<String> lines = IOUtils.readLines(res.getInputStream());
			String latestLine = lines.get(lines.size() - 1);
			String dateStr = latestLine.substring(0, latestLine.indexOf("|")).trim();
			return StockWindowBuilder.DATE_FORMAT.parse(dateStr);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean isNewDate(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			return true;
		}
		
		Calendar cld1 = Calendar.getInstance();
		Calendar cld2 = Calendar.getInstance();
		cld1.setTime(d1);
		cld2.setTime(d2);
		return cld1.get(Calendar.DATE) != cld2.get(Calendar.DATE);
	}

}
