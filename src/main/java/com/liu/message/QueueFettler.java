package com.liu.message;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import com.liu.helper.QueueHelper;

public class QueueFettler {
	public static void main(String argv[]) throws JMSException {
		QueueHelper.init();
		System.out.println("QueueHelper inited.");
		MessageConsumer consumer = QueueHelper.getConsumer();
		int i = 0;
		while(i < 100) {
			i += 1;
			TextMessage textMsg = (TextMessage) consumer.receive();
			Message msg = Message.getFromInputJson(textMsg.getText());
			System.out.println("$msg_" + i + " : " + new Date(msg.getTime()).toString() + " - " + textMsg.getText());
		}
	}
}
