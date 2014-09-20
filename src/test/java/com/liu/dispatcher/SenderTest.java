package com.liu.dispatcher;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.PropertyConfigurator;

import com.liu.message.Message;
import com.liu.message.Sender;

public class SenderTest {
	public static void main(String argv[]) throws EmailException {
		PropertyConfigurator.configure(Configuration.DEFAULT_CONF_PATH);
		if(trySendMail())
		    System.out.println("Mail sent.");
		else 
		    System.out.println("Mail sent failed.");
		
//		EmailMsg msg = new EmailMsg();
//		msg.setContent("内容test");
//		msg.setSubject("主题test");
//		List<String> to = new ArrayList<String>();
//		to.add("wdggat@163.com");
//		msg.setTo(to);
//		Sender.send(msg);
	}
	
	private static boolean trySendMail() throws EmailException {
		Message msg = new Message();
		msg.setFrom("hzliuxiaolong@126.com");
		msg.setFromUid("95270");
		msg.setContent("内容test");
		msg.setSubject("主题test");
		msg.setTo("hzliuxiaolong@163.com");
//		msg.setTo("597442779@qq.com");
//		Sender.sendSimpleMail(msg);
		return Sender.sendMail(msg);
	}
}
