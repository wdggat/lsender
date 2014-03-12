package com.liu.message;

import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import com.liu.dispatcher.Configuration;

public class Sender {
	private static final Logger logger = Logger.getLogger(Sender.class);
	private static Configuration conf = new Configuration();
	
	public static boolean send(EmailMsg msg) {
		try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(conf.getMailHost163());

            email.setFrom(conf.getMailFromAddr(), conf.getMailFromName());
            for(String to: msg.getTo())
                email.addTo(to);
            email.setSubject(msg.getSubject());

            email.setHtmlMsg(msg.getContent());
            // set the alternative message
            email.setTextMsg("Your email client does not support HTML messages");

            email.send();
        } catch (Exception e) {
            logger.error("Error occurred during sending email", e);
            return false;
        }
        return true;
	}
}
