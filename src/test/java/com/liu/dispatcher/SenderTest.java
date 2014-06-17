package com.liu.dispatcher;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.PropertyConfigurator;

import com.liu.message.Message;
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
		Message msg = new Message();
		msg.setContent("内容test");
		msg.setSubject("主题test");
		msg.setTo("wdggat@163.com");
//		Sender.sendSimpleMail(msg);
		Sender.sendMail(msg);
		System.out.println("Mail sent.");
	}
}
