package com.stock.util;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.stock.model.json.StockPriceWarning;

public class MailUtils2 {

	public static JavaMailSenderImpl createJavaMailSenderImpl() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(465);
        javaMailSender.setUsername("760680733@qq.com");
        javaMailSender.setPassword("mhnxqrzxicycbdbc");
        
        Properties properties = new Properties();
        properties.setProperty("mail.host", "smtp.qq.com");
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
		JavaMailSenderImpl senderImpl = createJavaMailSenderImpl();
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "utf-8");
            helper.setFrom("760680733@qq.com");// 设置发件人
            helper.setTo(toAddress);// 设置收件人
//            helper.setCc(cc);// 设置抄送
            helper.setSubject(subject);// 设置主题
            helper.setText(content);// 邮件体
            senderImpl.send(mailMessage);// 发送邮件
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 1000);
            } catch (InterruptedException e1) {
            }
        }
    }
	
	public static void main(String... args) {
		StockPriceWarning stockParam = new StockPriceWarning("601766", "中国中车", StockPriceWarning.StockOperation.Buy, 9.9f, ">=");
		stockParam.setCurrentPrice(9f);
		String[] toAddr = new String[]{"13777862834@139.com"};
		
		if (stockParam.isConditionMatch()) {
			sendMail(stockParam.getMailSubject(), stockParam.getMailBody(), toAddr);
		}
	}
}
