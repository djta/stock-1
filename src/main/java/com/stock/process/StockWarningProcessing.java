package com.stock.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.stock.api.StockApiHelper;
import com.stock.model.StockWindow;
import com.stock.model.builder.StockWindowBuilder;
import com.stock.model.json.StockPriceWarning;
import com.stock.model.json.StockWarning;
import com.stock.util.JsonUtil;
import com.stock.util.MailUtils2;
import com.stock.util.SmsUtils;
import com.taobao.api.ApiException;

@Component
public class StockWarningProcessing implements StockProcessing {
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	@Autowired
	private StockApiHelper apiHelper;
	@Autowired
	private StockWindowBuilder stockWindowBuilder;
	
	private List<StockWarning> allWarnings = new ArrayList<>();

	@Override
	public int getPriority() {
		return 99;
	}
	
	@PostConstruct
	public void init() {
		allWarnings = loadAllWarning();
	}

	@Override
	public void process() {
		Map<String, StockWindow> allStockWindows = getAllStockDetail(allWarnings);
		
		for (StockWarning warning : allWarnings) {
			if (warning.isEnable() == false) {
				continue;
			}
			
			StockWindow stockWindow = allStockWindows.get(warning.getStockCode());
			if (stockWindow == null) {
				continue;
			}
			
			if (warning instanceof StockPriceWarning) {
				((StockPriceWarning)warning).setCurrentPrice(stockWindow.getCurrentPrice());
			}
			
			if (warning.isTrigger()) {
				notify(warning);
			}
		}
	}
	
	private void notify(StockWarning warning) {
		if (warning.getPhones() != null && warning.getPhones().length > 0) {
			for (String phoneNum : warning.getPhones()) {
				try {
					SmsUtils.sendSms(warning.getSMSTemplateCode(), warning.getSMSTemplateParam(), phoneNum);
				} catch (ApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (warning.getEmails() != null && warning.getEmails().length > 0) {
			MailUtils2.sendMail(warning.getEmailSubject(), warning.getEmailBody(), warning.getEmails());
		}
	}

	private List<StockWarning> loadAllWarning() {
		try {
			Resource[] urlResource = resolver.getResources("classpath:resource/*预警.json");
			if (urlResource == null) {
				return null;
			}
			
			List<StockWarning> allWarnings = new ArrayList<>();
			for (Resource res : urlResource) {
				String content = IOUtils.toString(res.getInputStream());
				List<StockWarning> warningList = JsonUtil.toObjectList(content, StockWarning.class);
				allWarnings.addAll(warningList);
			}
			return allWarnings;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private Map<String, StockWindow> getAllStockDetail(List<StockWarning> warnings) {
		Set<String> allStockCodeSet = new HashSet<>();
		for (StockWarning warning : warnings) {
			allStockCodeSet.add(warning.getStockCode());
		}
		
		StringBuilder codeBuilder = new StringBuilder();
		for (String code : allStockCodeSet) {
			if (codeBuilder.length() == 0) {
				codeBuilder.append(getCodeStr(code));
			} else {
				codeBuilder.append(",").append(getCodeStr(code));
			}
		}
		
		String contentStr = apiHelper.getTodayMessage(codeBuilder.toString());
		List<StockWindow> allWindows = stockWindowBuilder.buildAllInstance(contentStr);
		
		Map<String, StockWindow> resMap = new HashMap<>();
		for (StockWindow sw : allWindows) {
			resMap.put(sw.getCode(), sw);
		}
		return resMap;
	}
	
	private String getCodeStr(String stockCode) {
		if (stockCode.startsWith("6")) {
			return "sh" + stockCode;
		} else {
			return "sz" + stockCode;
		}
	}
}
