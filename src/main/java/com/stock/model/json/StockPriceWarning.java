package com.stock.model.json;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class StockPriceWarning extends StockWarningAbstract {
	
	private static final String MAIL_BODY_FORMAT = "现价<font color='red' size='4'>￥%.2f</font> "
					+ "<font color='blue' size='4'><B>%s</B></font> "
					+ "设定价<font color='red' size='4'>￥%.2f</font>，"
					+ " 符合<font color='CD3700' size='4'>%s</font>条件 ";
	
	private static final String SMS_TEMPLATE = "SMS_44190084";
	private static final String SMS_PARAM_FORMAT = "{\"name\":\"%s\", \"currentPrice\":\"￥%.2f\", \"goalPrice\":\" %s￥%.2f\", \"operation\":\"%s\"}";
	
	private float currentPrice;
	private String condition;
	private float goalPrice;
	
	@Override
	public boolean isTrigger() {
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
	
	@Override
	public String getSMSTemplateCode() {
		return SMS_TEMPLATE;
	}

	@Override
	public String getSMSTemplateParam() {
		return String.format(SMS_PARAM_FORMAT, getNameDes(), currentPrice, condition, goalPrice, getOperationDes() );
	}

	@Override
	public String getEmailSubject() {
		String operationStr = null;
		if (operation == StockOperation.Buy) {
			operationStr = "买买买 ";
		} else if (operation == StockOperation.Sell) {
			operationStr = "卖卖卖";
		}
		return operationStr + getNameDes();
	}

	@Override
	public String getEmailBody() {
		if (operation == StockOperation.Buy) {
			return String.format(MAIL_BODY_FORMAT, currentPrice, condition, goalPrice, "买入");
		} else if (operation == StockOperation.Sell) {
			return String.format(MAIL_BODY_FORMAT, currentPrice, condition, goalPrice, "卖出");
		}
		return null;
	}
	
	
	/*** Private function start ***************************************************************/
	
	private String getNameDes() {
		return stockCode + "("+stockName+")";
	}
	
	private String getOperationDes() {
		if (operation == StockOperation.Buy) {
			return "_买买买 ";
		} else if (operation == StockOperation.Sell) {
			return "_卖卖卖";
		} else {
			return null;
		}
	}

}
