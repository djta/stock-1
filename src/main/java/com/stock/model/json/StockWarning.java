package com.stock.model.json;

public interface StockWarning {
	
	boolean isEnable();					//只有在enable状态下才能触发预警

	boolean isTrigger();				//是否瞒住条件触发预警
	
	String getStockCode();				//获取股票代码
			
	String getStockName();				//获取股票名字
	
	String[] getPhones();				//获取通知的手机号码，以短信形式告知
	
	String getSMSTemplateCode();		//获取短信模板ID
	
	String getSMSTemplateParam();		//获取短信模板参数
	
	String[] getEmails();				//获取通知的电子邮件地址
	
	String getEmailSubject();			//获取通知邮件的标题
	
	String getEmailBody();				//获取通知邮件的内容
}