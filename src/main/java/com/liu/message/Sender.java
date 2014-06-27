package com.liu.message;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import com.liu.dispatcher.Configuration;

public class Sender {
	private static final Logger logger = Logger.getLogger(Sender.class);
	private static Configuration conf = new Configuration();
	
	public static boolean sendMail(Message msg) {
		return sendSimpleMail(msg);
	}
	
	public static boolean pushMsg(Message msg) {
		//TODO
		return false;
	}
	
	private static boolean sendSimpleMail(Message msg){
		try {
			Email email = new SimpleEmail();
			email.setHostName(conf.getMailHost163());
			email.setSmtpPort(conf.getMail163Port());
			email.setAuthenticator(new DefaultAuthenticator(conf.getMail163FromAddr(), conf.getMail163FromPassword()));
			email.setSSLOnConnect(true);
			email.setFrom(conf.getMail163FromAddr(), conf.getMailFromName());
			email.setSubject(msg.getSubject());
			email.setMsg(msg.getContent());
            email.addTo(msg.getTo());
			email.send();
			return true;
		} catch (EmailException e) {
			logger.error("exception when sending simple mail, ", e);
			return false;
		}
	}
	
	private static boolean sendHtml(Message msg) {
		try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(conf.getMailHost163());
            email.setFrom(conf.getMail163FromAddr(), conf.getMailFromName());
            email.setSSLCheckServerIdentity(true);
            email.addTo(msg.getTo());
            email.setSubject(msg.getSubject());
            email.setHtmlMsg(msg.getContent());
            email.send();
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during sending email", e);
            return false;
        }
	}
}
