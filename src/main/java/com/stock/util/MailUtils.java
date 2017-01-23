package com.stock.util;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {
	
	private static final String USER_NAME = "760680733";
	private static final String PASSWORD = "mhnxqrzxicycbdbc";

//	private static final Properties props = new Properties();
	private static Session session;
	
//	static {
//		props.setProperty("mail.transport.protocol", "pop3");
//		props.setProperty("mail.host", "pop.126.com");
//		props.setProperty("mail.smtp.auth", "true");
////		props.setProperty("mail.debug", "true");
//		
//		session = Session.getInstance(props);
//	}
	
	public static void sendMail(String toAddress, String subject, String content) throws Exception {
		
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "pop3");
		props.setProperty("mail.host", "pop.qq.com");
		props.setProperty("mail.smtp.port", "995");
//		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.debug", "true");
		
		session = Session.getInstance(props, new Authenticator() {
			 protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USER_NAME, PASSWORD);
			 }
		});
		
		MimeMessage message = new MimeMessage(session);
		
		message.setSubject(subject);
		message.setText(content);
		message.setFrom(new InternetAddress("760680733@qq.com"));
		message.setRecipients(RecipientType.TO, new Address[]{new InternetAddress(toAddress)});
		
		Transport transport = session.getTransport();
		transport.connect(USER_NAME, PASSWORD);
		
		transport.sendMessage(message, new Address[]{new InternetAddress(toAddress)});
		
		transport.close();
	}
	
	public static void main(String... args) throws Exception {
		sendMail("zhangjie0574@126.com", "Hello", "HelloWorld!");
	}
}
