package com.stock.model.json;

import org.junit.Test;

import com.stock.model.json.StockWarningAbstract.StockOperation;
import com.stock.util.JsonUtil;

public class StockPriceWarningTest {

	@Test
	public void printTest() {
		StockPriceWarning priceWarning = new StockPriceWarning();
		priceWarning.setCondition(">=");
		priceWarning.setEmails(new String[]{"111@126.com", "222@163.com"});
		priceWarning.setEnable(true);
		priceWarning.setOperation(StockOperation.Buy);
		priceWarning.setPhones(new String[]{"11111", "222222"});
		priceWarning.setStockCode("601766");
		priceWarning.setStockName("中国中车");
		priceWarning.setGoalPrice(10.1f);
		System.out.println(JsonUtil.toJsonString(priceWarning));
	}
}
