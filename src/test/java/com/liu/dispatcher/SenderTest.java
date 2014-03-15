package com.liu.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.PropertyConfigurator;

import com.liu.message.EmailMsg;
import com.liu.message.Sender;

public class SenderTest {
	public static void main(String argv[]) throws EmailException {
		PropertyConfigurator.configure(Configuration.DEFAULT_CONF_PATH);
		trySendSimpleMail();
		
//		EmailMsg msg = new EmailMsg();
//		msg.setContent("内容test");
//		msg.setSubject("主题test");
//		List<String> to = new ArrayList<String>();
//		to.add("wdggat@163.com");
//		msg.setTo(to);
//		Sender.send(msg);
	}
	
	private static void trySendSimpleMail() throws EmailException {
		EmailMsg msg = new EmailMsg();
		msg.setContent("内容test");
		msg.setSubject("主题test");
		List<String> to = new ArrayList<String>();
		to.add("wdggat@163.com");
		msg.setTo(to);
		Sender.sendSimpleMail(msg);
		System.out.println("Mail sent.");
	}
}
