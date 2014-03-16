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
	
	public static boolean sendSimpleMail(EmailMsg msg){
		try {
			Email email = new SimpleEmail();
			email.setHostName(conf.getMailHost163());
			email.setSmtpPort(conf.getMail163Port());
			email.setAuthenticator(new DefaultAuthenticator(conf.getMail163FromAddr(), conf.getMail163FromPassword()));
			email.setSSLOnConnect(true);
			email.setFrom(conf.getMail163FromAddr(), conf.getMailFromName());
			email.setSubject(msg.getSubject());
			email.setMsg(msg.getContent());
			for(String to: msg.getTo())
                email.addTo(to);
			email.send();
			return true;
		} catch (EmailException e) {
			logger.error("exception when sending simple mail, ", e);
			return false;
		}
	}
	
	public static boolean sendHtml(EmailMsg msg) {
		try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(conf.getMailHost163());
            email.setFrom(conf.getMail163FromAddr(), conf.getMailFromName());
            email.setFrom(conf.getMail163FromAddr(), "Whoami");
            email.setSSLCheckServerIdentity(true);
            for(String to: msg.getTo())
                email.addTo(to);
            email.setSubject(msg.getSubject());
            email.setHtmlMsg(msg.getContent());
            email.setTextMsg("Your email client does not support HTML messages");
            email.send();
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during sending email", e);
            return false;
        }
	}
}
