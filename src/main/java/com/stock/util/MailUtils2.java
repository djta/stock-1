package com.stock.util;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.stock.model.json.StockPriceWarning;

public class MailUtils2 {
	
	private static final JavaMailSenderImpl MAIL_SENDER = createJavaMailSenderImpl();
	
//	private static final String EMAIL_HOST = "smtp.139.com";
//	private static final String EMAIL_USER = "13777862834@139.com";
//	private static final String EMAIL_PASSWORD = "1983a1113f";
	
//	private static final String EMAIL_HOST = "smtp.qq.com";
//	private static final String EMAIL_USER = "760680733@qq.com";
//	private static final String EMAIL_PASSWORD = "mhnxqrzxicycbdbc";
	
	private static final String EMAIL_HOST = "smtp.126.com";
	private static final String EMAIL_USER = "zhangjie0574@126.com";
	private static final String EMAIL_PASSWORD = "3'ylfml";

	public static JavaMailSenderImpl createJavaMailSenderImpl() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        
        javaMailSender.setHost(EMAIL_HOST);
        javaMailSender.setPort(465);
        javaMailSender.setUsername(EMAIL_USER);
        javaMailSender.setPassword(EMAIL_PASSWORD);
        
        Properties properties = new Properties();
        properties.setProperty("mail.host", EMAIL_HOST);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.debug", "true");

        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
	}
	
	public static void sendMail(String subject, String content, String... toAddress) {
        MimeMessage mailMessage = MAIL_SENDER.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "utf-8");
            helper.setFrom(EMAIL_USER);// 设置发件人
            helper.setTo(toAddress);// 设置收件人
//            helper.setCc(cc);// 设置抄送
            helper.setSubject(subject);// 设置主题
            helper.setText(content, true);// 邮件体
            MAIL_SENDER.send(mailMessage);// 发送邮件
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e1) {
            }
        }
    }
	
	public static void main(String... args) {
		StockPriceWarning stockParam = new StockPriceWarning();
		stockParam.setStockCode("601766");
		stockParam.setStockName("中国中车");
		stockParam.setOperation(StockPriceWarning.StockOperation.Sell);
		stockParam.setCondition("<=");
		stockParam.setGoalPrice(9.93f);
		stockParam.setEmails(new String[]{"13777862834@139.com", "760680733@qq.com"});
		
		stockParam.setCurrentPrice(9.92f);
		
		if (stockParam.isTrigger()) {
			sendMail(stockParam.getEmailSubject(), stockParam.getEmailBody(), stockParam.getEmails());
		}
	}
}
