package com.stock.util;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class SmsUtils {
	
	private static final String TAOBAO_URL = "http://gw.api.taobao.com/router/rest";
	private static final String APP_KEY = "23615485";
	private static final String SECRET="f0f005ed50d0e46f54e533bb7dd2b07c";

	public static void sendSms() throws ApiException{
		TaobaoClient client = new DefaultTaobaoClient(TAOBAO_URL, APP_KEY, SECRET);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("杰仔财富");
		req.setSmsParamString("{\"code\":\"1234\",\"product\":\"alidayu\"}");
		req.setRecNum("13777862834");
		req.setSmsTemplateCode("SMS_44165005");
		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
	}
	
	public static void main(String... args) {
		try {
			sendSms();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
