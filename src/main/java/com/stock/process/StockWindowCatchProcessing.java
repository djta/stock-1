package com.stock.process;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.fileServer.qiniu.FileServerQiNiu;
import com.stock.model.StockWindow;
import com.stock.model.builder.StockWindowBuilder;
import com.stock.util.ZipUploadUtil;

@Component
public class StockWindowCatchProcessing implements StockProcessing {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	@Value("${file.path.stock.pankou}")
	private String filePathPanKou;
	
	@Value("${folder.path.stock.pankou}")
	private String folderPathPanKou;
	
	@Autowired
	private StockApiHelper apiHelper;
	@Autowired
	private StockWindowBuilder stockWindowBuilder;
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	@Autowired
	private FileServerQiNiu qiNiu;
	@Autowired
	private ReloadableResourceBundleMessageSource messageResource;
	
	private Calendar calendar = Calendar.getInstance();
	
	private Map<String, Date> latestRecords = new HashMap<>();
	
	private boolean needUpload = false;
	
	@Override
	public int getPriority() {
		return 99;
	}

	@Override
	public void process() {
		
		if (inOpenTime() == false) {
			if (needUpload && inUploadTime() == true) {
				//把数据打包上传到 文件服务器
				String folderName = DATE_FORMAT.format(new Date());
				String folderPath = String.format(folderPathPanKou, folderName);
				ZipUploadUtil.zipUpload(folderPath);
				needUpload = false;
			}
			return;
		}
		
		String content = apiHelper.getCurrentMessageAll();
		String[] contentArg = content.split("\n");
		for (String line : contentArg) {
			try{
				StockWindow stockWin = stockWindowBuilder.buildInstance(line);
				if (stockWin == null || stockWin.isValid() == false) {
					continue;
				}
				
				persistent(stockWin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void format(String filePath) throws IOException {
		Resource[] ress = resolver.getResources(filePath);
		System.out.println(ress.length);
		Set<String> folders = new HashSet<>();
		for (Resource res : ress) {
			try{
				List<String> lines = IOUtils.readLines(res.getInputStream(), "gb2312");
				String headStr = lines.get(0);
				int indexStart = headStr.indexOf("(");
				int indexEnd = headStr.indexOf(")");
				String code = headStr.substring(0, indexStart);
				String name = headStr.substring(indexStart + 1, indexEnd);
				for (String line : lines) {
					StockWindow stockWin = stockWindowBuilder.buildInstance(line);
					if (stockWin == null) {
						continue;
					}
					stockWin.setCode(code);
					stockWin.setName(name);
					persistent(stockWin);
					folders.add(DATE_FORMAT.format(stockWin.getDate()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//压缩上传所有的文件夹
		for (String folderName : folders) {
			String folderPath = messageResource.getMessage("folder.path.stock.pankou", 
					new Object[]{folderName}, Locale.getDefault());
			ZipUploadUtil.zipUpload(folderPath);
		}
	}
	
	private void persistent(StockWindow stockWin) throws IOException {
		String dateStr = DATE_FORMAT.format(stockWin.getDate());
		String filePath = messageResource.getMessage("file.path.stock.pankou", 
				new Object[]{dateStr, stockWin.getCode()}, Locale.getDefault());
		File stockFile = new File(filePath);
		
		if (stockFile.exists() == false) {
			FileUtils.write(stockFile, stockWin.getHead(), true);
		} else if (latestRecords.containsKey(stockWin.getCode()) == false) {
			Date lastDate = getLastDate(filePath);
			latestRecords.put(stockWin.getCode(), lastDate);
		}
		
		if ( stockWin.getDate().equals(latestRecords.get(stockWin.getCode())) ) {
			//reduplicate data
			return;
		}
		
		if (isNewDate(latestRecords.get(stockWin.getCode()), stockWin.getDate())) {
			FileUtils.write(stockFile, "\n", true);
		}
		
		FileUtils.write(stockFile, stockWin.toString(), true);
		latestRecords.put(stockWin.getCode(), stockWin.getDate());
		needUpload = true;
	}
	
	private boolean inOpenTime() {
		int currentMin = getCurrentMin();
		
		int startAm = 9 * 60 + 30 - 10;
		int endAm = 11 * 60 + 30 + 10;
		int startPm = 13 * 60 - 10;
		int endPm = 15 * 60 + 10;
		return  ( currentMin >= startAm && currentMin <= endAm ) ||
				( currentMin >= startPm && currentMin <= endPm );
	}
	
	private boolean inUploadTime() {
		int currentMin = getCurrentMin();
		
		return currentMin > 15 * 60 + 15;
	}
	
	private int getCurrentMin() {
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		return hour * 60 + min;
	}
	
	private Date getLastDate(String filePath) {
		try {
			Resource res = resolver.getResource("file:" + filePath);
			List<String> lines = IOUtils.readLines(res.getInputStream());
			String latestLine = lines.get(lines.size() - 1);
			int index = latestLine.indexOf("|");
			if (index > 0) {
				String dateStr = latestLine.substring(0, latestLine.indexOf("|")).trim();
				return StockWindowBuilder.DATE_FORMAT.parse(dateStr);
			} else {
				return null;
			}
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
