package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendmail(String subject, String message, String to) {
		boolean flag = false;
		
		String from = "sduttas2014@gmail.com";
		String password = "";
		
		String host = "smtp.gmail.com";
		String port = "465";
		
		Properties properties = System.getProperties();
//		Properties properties = new Properties();
//		System.out.println(properties);
		
		
		//host set
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.port", "465");
	    properties.setProperty("mail.smtp.socketFactory.port", "465");
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.debug", "true");
	    properties.put("mail.store.protocol", "pop3");
	    properties.put("mail.transport.protocol", "smtp");
//		properties.put("mail.smtp.host", host);
//		properties.put("mail.smtp.port", port);
//		properties.put("mail.smtp.ssl.enable", true);
////		properties.put("mail.smtp.socketFactory.port", port);
////		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
////		properties.put("mail.smtp.starttls.enable",true); 
//		properties.put("mail.smtp.auth", true);
		
		//Step 1: to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(from, password);
			}
			
		});
		
		session.setDebug(true);
		
		//Step 2: Compose the message[email, multimedia]
		MimeMessage mimeMessage = new MimeMessage(session);
		
		try {
			//from mail
			mimeMessage.setFrom(new InternetAddress(from));
			
			//adding recipient to message
			mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			mimeMessage.setSubject(subject);
			
			//adding text to message
			mimeMessage.setText(message);
			
			//Step 3: Send the message using transport class
			Transport.send(mimeMessage);
			
			flag = true;
		}
		catch(Exception e) {
//			e.printStackTrace();
		}
		
		
		return flag;
	}
}
