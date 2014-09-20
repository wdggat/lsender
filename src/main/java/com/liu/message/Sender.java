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
//		return sendSimpleMail(msg);
		return sendHtml(msg);
	}
	
	private static boolean pushMsg(Message msg) {
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
	
	private static final String appendixWhoami = "我只是信息的转发工,注册匿名社交工具whoami,贴上uid,可与发信人进行半匿名对话哦.";
	private static final String appendixHtmlWhoami = "我只是信息的转发工,注册匿名社交工具<a href=\"" + conf.getDownloadLink() + "\" target=\"_blank\">whoami</a>,贴上uid,可与发信人进行半匿名对话哦.";
	private static final String appendixUid = "消息主人uid: ";
	private static String appendReadmeToHtmlMailContent(String content, String fromUid) {
		return content + "<br><br><br><br><br><font color=\"red\">" + appendixHtmlWhoami + "<br>" + appendixUid + fromUid + "<br></font>";
	}
	
	private static String appendReadmeToTextMailContent(String content, String fromUid) {
		return content + "\n\n\n\n\n" + appendixWhoami + "\n" + appendixUid + fromUid + "\n";
	}
	
	//Useless, com.sun.mail.smtp.SMTPSenderFailedException: 553 authentication is required,smtp11
	private static boolean sendHtml(Message msg) {
		try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(conf.getMailHost163());
            email.setFrom(conf.getMail163FromAddr(), conf.getMailFromName());
            email.setSSLCheckServerIdentity(true);
            email.setSSLOnConnect(true);
//            email.setStartTLSEnabled(true);
            email.setAuthentication(conf.getMail163FromAddr(), conf.getMail163FromPassword());
            email.addTo(msg.getTo());
            email.setSubject(msg.getSubject());
            email.setHtmlMsg(appendReadmeToHtmlMailContent(msg.getContent(), msg.getFromUid()));
         // set the alternative message
            email.setTextMsg(appendReadmeToTextMailContent(msg.getContent(), msg.getFromUid()));
            email.send();
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during sending email", e);
            return false;
        }
	}
	
	// in message-receiver
	/*public static boolean sendMail(MailRequest mailRequest) {
        String hostName = Configuration.MAIL_HOST_NAME; mfast.163.com

        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(hostName);

            email.setFrom(mailRequest.getFromAddress(), mailRequest.getFromName());
            email.addTo(mailRequest.getTo()[0] + "@" + Configuration.MAIL_DOMAIN);
            email.setSubject(mailRequest.getMailSubject());

            // set the html message
            email.setHtmlMsg(mailRequest.getMailContent());
            // set the alternative message
            email.setTextMsg("Your email client does not support HTML messages");

            email.send();
        } catch (Exception e) {
            logger.error("Error occurred during sending email", e);
            return false;
        }

        return true;
    }*/
}
