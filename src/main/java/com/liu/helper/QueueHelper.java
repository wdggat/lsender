package com.liu.helper;

import org.apache.log4j.Logger;

import com.liu.dispatcher.Configuration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class QueueHelper {
	private static final Logger logger = Logger.getLogger(QueueHelper.class);

	private static Configuration conf = new Configuration();
	private static Connection conn = null;
	private static Channel channel;

	public static boolean init() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(conf.getMQHost());
			factory.setPort(conf.getMQPort());
			factory.setUsername(conf.getMQUser());
			factory.setPassword(conf.getMQPassword());
			conn = factory.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare(conf.getMQQueueName(), true, false, false,	null);
			return true;
		} catch (Exception e) {
			logger.error("Error occurs when initing MQ, ", e);
			return false;
		}
	}

	public static QueueingConsumer generateConsumer() {
		if(conn == null)
			init();
		try {
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(conf.getMQQueueName(), true, consumer);
			return consumer;
		} catch (Exception e) {
			logger.error("Error occurs when generate consumer, ", e);
			return null;
		}
	}
}
