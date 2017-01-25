package com.stock.util;

import com.stock.model.json.StockPriceWarning;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class SmsUtils {
	
	private static final String TAOBAO_URL = "http://gw.api.taobao.com/router/rest";
	private static final String APP_KEY = "23615485";
	private static final String SECRET="f0f005ed50d0e46f54e533bb7dd2b07c";
	private static final String SMS_SIGN_NAME = "杰仔财富";
	private static final String SMS_TYPE = "normal";
	private static final TaobaoClient TAOBAO_CLIENT = new DefaultTaobaoClient(TAOBAO_URL, APP_KEY, SECRET);

	public static void sendSms() throws ApiException{
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//		req.setExtend("123456");
		req.setSmsType(SMS_TYPE);
		req.setSmsFreeSignName(SMS_SIGN_NAME);
		req.setSmsParamString("{\"name\":\"601766(中国中车)\", \"currentPrice\":\"￥9.98\", \"goalPrice\":\" >=￥9.96\"}");
		req.setRecNum("13777862834");
		req.setSmsTemplateCode("SMS_44085048");
		AlibabaAliqinFcSmsNumSendResponse rsp = TAOBAO_CLIENT.execute(req);
		System.out.println(rsp.getBody());
	}
	
	public static void sendSms(String stockParam, String phoneNum) throws ApiException{
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType(SMS_TYPE);
		req.setSmsFreeSignName(SMS_SIGN_NAME);
		req.setSmsParamString(JsonUtil.toJsonString(stockParam));
		req.setRecNum(phoneNum);
		req.setSmsTemplateCode(stockParam);
		AlibabaAliqinFcSmsNumSendResponse rsp = TAOBAO_CLIENT.execute(req);
		System.out.println(rsp.getBody());
	}
	
	public static void main(String... args) {
		try {
			StockPriceWarning stockParam = new StockPriceWarning();
			stockParam.setStockCode("601766");
			stockParam.setStockName("中国中车");
			stockParam.setOperation(StockPriceWarning.StockOperation.Sell);
			stockParam.setCondition("<=");
			stockParam.setGoalPrice(9.93f);
			stockParam.setPhones(new String[]{"13777862834"});
			
			stockParam.setCurrentPrice(9.2f);
			if (stockParam.isTrigger()) {
				for (String phoneNum : stockParam.getPhones()) {
					sendSms(stockParam.getSMSTemplateParam(), phoneNum);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
