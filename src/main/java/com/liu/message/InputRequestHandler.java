package com.liu.message;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.liu.dispatcher.QueueHelper;
import com.liu.dispatcher.RequestLogger;

public class InputRequestHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(InputRequestHandler.class);

    private final int id;
    private static MessageConsumer consumer;

    public InputRequestHandler(int id, MessageConsumer consumer) {
        this.id = id;
        InputRequestHandler.consumer = consumer;
    }

    /* Used to coordinate multiple threads when queue connection went wrong,
    and needed to shutdown and recreate another queue producer */
    private static FutureTask<MessageConsumer> createProducerTaskFuture = null;
    @Override
    public void run() {
        if(consumer == null) {
        	logger.error("Failed to start InputRequestHandler " + id);
        	return;
        }
        logger.info("Succeeded to start InputRequestHandler " + id);

        while (true) {
        	try {
				TextMessage msg = (TextMessage) consumer.receive();
				logger.debug("$Msg from queue: " + msg.getText());
				AppMessage appMsg = AppMessage.getFromInputJson(msg.getText());
				EmailMsg emailMsg = EmailMsg.getFromAppMessage(appMsg);
				if(Sender.sendSimpleMail(emailMsg))
					RequestLogger.getRequestLogger().logMsgDone(appMsg, id);
				else
					RequestLogger.getRequestLogger().logMsgFailed(appMsg, id);
			} catch (Throwable e) {
				if (consumer != null) {
					try {
						consumer.close();
					} catch (Throwable e2) {
						logger.error("Failed to shutdown InputRequestHandler: " + id, e2);
					}
				}
				try {
	                if (createProducerTaskFuture == null) {
	                    Callable<MessageConsumer> task = new Callable<MessageConsumer>() {
	                        public MessageConsumer call() throws Exception {
	                            logger.info("queue producer is about to recreate");
	                            return QueueHelper.getConsumer();
	                        }
	                    };
	                    FutureTask<MessageConsumer> futureTask =
	                            new FutureTask<MessageConsumer>(task);
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
}
