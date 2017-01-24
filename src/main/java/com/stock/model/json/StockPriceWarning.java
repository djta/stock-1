package com.stock.model.json;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@RequiredArgsConstructor
public class StockPriceWarning {
	
	private static final String MAIL_BODY_FORMAT = "现价￥%.2f %s 设定价￥%.2f， 符合%s条件 ";
	private static final String MONEY_FLAG = "￥";
	private static final String SMS_TEMPLATE = "SMS_44190084";
	
	public enum StockOperation {
		Buy, Sell;
	}

	private final String stockCode;
	private final String stockName;
	private final StockOperation operation;
	private final String condition;
	private final float goalPrice;
	
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
		return SMS_TEMPLATE;
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
	
	@JsonProperty("operation")
	public String getOperation() {
		if (operation == StockOperation.Buy) {
			return "_买买买 ";
		} else if (operation == StockOperation.Sell) {
			return "_卖卖卖";
		} else {
			return null;
		}
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
