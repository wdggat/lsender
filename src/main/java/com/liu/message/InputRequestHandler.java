package com.liu.message;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import com.liu.dispatcher.RequestLogger;
import com.liu.helper.BaiduPushHelper;
import com.liu.helper.QueueHelper;
import com.liu.helper.RedisHelper;
import com.rabbitmq.client.QueueingConsumer;

public class InputRequestHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(InputRequestHandler.class);

    private final int id;
    private QueueingConsumer consumer;

    public InputRequestHandler(int id, QueueingConsumer consumer) {
        this.id = id;
        this.consumer = consumer;
    }

    /* Used to coordinate multiple threads when queue connection went wrong,
    and needed to shutdown and recreate another queue producer */
    private static FutureTask<QueueingConsumer> createProducerTaskFuture = null;
    @Override
    public void run() {
        if(consumer == null) {
        	logger.error("Failed to start InputRequestHandler " + id);
        	return;
        }
        logger.info("Succeeded to start InputRequestHandler " + id);

        while (true) {
        	try {
        		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        	    String textMsg = new String(delivery.getBody());
				send(textMsg);
        	} catch (Throwable e) {
        		logger.error("msg sent exception, ", e);
				consumer = null;
				try {
	                if (createProducerTaskFuture == null) {
	                    Callable<QueueingConsumer> task = new Callable<QueueingConsumer>() {
	                        public QueueingConsumer call() throws Exception {
	                            logger.info("queue producer is about to recreate");
	                            return QueueHelper.generateConsumer();
	                        }
	                    };
	                    FutureTask<QueueingConsumer> futureTask =
	                            new FutureTask<QueueingConsumer>(task);
	                    if (createProducerTaskFuture == null) {
	                        createProducerTaskFuture = futureTask;
	                        futureTask.run();
	                    }
	                }

	                // Wait until one thread finished creating a new producer
	                consumer = createProducerTaskFuture.get();
				} catch (Exception e1) {
					logger.error("create producer failed", e1);
				} finally {
	                // Reset task future at the end
	                createProducerTaskFuture = null;
	            }
        	}
        }
    }
    
	private void send(String textMsg) {
		try {
			logger.info("$Msg from queue: " + textMsg);
			Message msg = Message.getFromInputJson(textMsg);
			if (msg == null || !msg.isValidMessage()) {
				logger.error("$error_input, dropped. " + textMsg);
				return;
			}
			msg.normalize();

			boolean sendResult = false;
			if (msg.getDataType() == DataType.QUICK_MSG || msg.getDataType() == DataType.REPLY) {
				List<String> baiduUinfo;
				if (msg.isToEmail())
					baiduUinfo = RedisHelper.getBaiduUserCacheUname(msg.getTo(), 2);
				else
					baiduUinfo = RedisHelper.getBaiduUserCacheUid(msg.getTo(), 2);
				if (baiduUinfo == null || baiduUinfo.isEmpty()) {
					logger.info("get baidu_uinfo failed, msg_to: " + msg.getTo());
					sendResult = false;
				} else {
					sendResult = BaiduPushHelper.pushMessage(baiduUinfo.get(0),	msg);
				}
			} else if (msg.getDataType() == DataType.NEW_MSG) {
				sendResult = Sender.sendMail(msg);
			} else {
				logger.info("$error_mail, dropped. " + msg.toJson());
				sendResult = false;
			}
			if (sendResult) {
				RequestLogger.getRequestLogger().logMsgDone(msg, id);
				logger.info("$mail sent, " + msg.toJson());
			} else {
				RequestLogger.getRequestLogger().logMsgFailed(msg, id);
				logger.error("$mail sent failed, " + msg.toJson());
			}
		} catch (Throwable t) {
			logger.error("Exception when sending msg,", t);
			return;
		}
	}
}
