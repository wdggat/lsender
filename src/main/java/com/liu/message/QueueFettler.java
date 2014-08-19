package com.liu.message;

import java.util.Date;

import com.liu.helper.QueueHelper;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueFettler {
	public static void main(String argv[]) throws ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		QueueHelper.init();
		System.out.println("QueueHelper inited.");
		QueueingConsumer consumer = QueueHelper.generateConsumer();
		int i = 0;
		while(i < 100) {
			i += 1;
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String textMsg = new String(delivery.getBody());
			Message msg = Message.getFromInputJson(textMsg);
			System.out.println("$msg_" + i + " : " + new Date(msg.getTime()).toString() + " - " + textMsg);
		}
	}
}
