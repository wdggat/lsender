package com.liu.message;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import com.liu.dispatcher.QueueHelper;

public class QueueFettler {
	public static void main(String argv[]) throws JMSException {
		QueueHelper.init();
		System.out.println("QueueHelper inited.");
		MessageConsumer consumer = QueueHelper.getConsumer();
		int i = 0;
		while(i < 100) {
			i += 1;
			TextMessage msg = (TextMessage) consumer.receive();
			AppMessage appMsg = AppMessage.getFromInputJson(msg.getText());
			System.out.println("$msg_" + i + " : " + new Date(appMsg.getOccurTime()).toString() + " - " + msg.getText());
		}
	}
}
