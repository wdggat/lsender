package com.liu.helper;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.liu.dispatcher.Configuration;

public class QueueHelper {
	private static final Logger logger = Logger.getLogger(QueueHelper.class);

	private static Configuration conf = new Configuration();
	private static Connection conn = null;
	private static Session session = null;
	private static MessageConsumer consumer = null;

	public static boolean init() {
		ConnectionFactory connectFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, conf.getMQBrokerUrl());
		try {
			conn = connectFactory.createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(conf.getMQQueueName());
			consumer = session.createConsumer(destination);
		} catch (Exception e) {
			logger.error("MQ init failed,", e);
			if(conn != null)
				try {
					conn.close();
				} catch (JMSException e2) {
					logger.error("MQ close failed,", e2);
				}
			return false;
		}
		return true;
	}

	public static MessageConsumer getConsumer() {
		if(conn == null)
			init();
		return consumer;
	}
}
