package com.stock.model.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class StockPriceWarning {
	
	private static final String MAIL_BODY_FORMAT = "现价￥%.2f %s 设定价￥%.2f， 符合%s条件 ";
	private static final String MONEY_FLAG = "￥";
	
	public enum StockOperation {
		Buy("SMS_44090093"), Sell("SMS_44085048");
		
		@Getter
		private String value;
		
		StockOperation(String value) {
			this.value = value;
		}
		
	}

	private final String stockCode;
	private final String stockName;
	private final StockOperation operation;
	private final float goalPrice;
	private final String condition;
	
	@Setter
	private float currentPrice;
	
	@JsonIgnore
	public boolean isConditionMatch() {
		switch (condition) {
		case "=":
			return currentPrice == goalPrice;
		case ">":
			return currentPrice > goalPrice;
		case "<":
			return currentPrice < goalPrice;
		case ">=":
			return currentPrice >= goalPrice;
		case "<=":
			return currentPrice <= goalPrice;
		default:
			return false;
		}
	}
	
	@JsonIgnore
	public String getSMSTemplateCode() {
		return operation.getValue();
	}
	
	@JsonProperty("name")
	public String getName() {
		return stockCode + "("+stockName+")";
	}
	
	@JsonProperty("currentPrice")
	public String getCurrentPrice() {
		return MONEY_FLAG + currentPrice;
	}
	
	@JsonProperty("goalPrice")
	public String getGoalPrice() {
		return condition + MONEY_FLAG + goalPrice;
	}
	
	@JsonIgnore
	public String getMailSubject() {
		String operationStr = null;
		if (operation == StockOperation.Buy) {
			operationStr = "买买买 ";
		} else if (operation == StockOperation.Sell) {
			operationStr = "卖卖卖";
		}
		return operationStr + getName();
	}
	
	@JsonIgnore
	public String getMailBody() {
		if (operation == StockOperation.Buy) {
			return String.format(MAIL_BODY_FORMAT, currentPrice, condition, goalPrice, "买入");
		} else if (operation == StockOperation.Sell) {
			return String.format(MAIL_BODY_FORMAT, currentPrice, condition, goalPrice, "卖出");
		}
		return null;
	}
}
