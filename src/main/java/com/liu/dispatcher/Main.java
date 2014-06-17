package com.liu.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.MessageConsumer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.liu.helper.QueueHelper;
import com.liu.message.InputRequestHandler;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException{
		PropertyConfigurator.configure(Configuration.DEFAULT_CONF_PATH);
		Configuration conf = new Configuration();
		
        logger.info("Init request loggers");
        if (!RequestLogger.init()) {
            logger.error("Error occurred during initializing request logger, abort");
            return;
        }

        logger.info("initing receipt mq...");
        if(!QueueHelper.init()){
        	logger.fatal("Error occurs during initing receipt mq, abort.");
        	return;
        }
        
/*        logger.info("Starting HttpClient ...");
        HttpClientVM.init(200, 40);*/

        logger.info("Setup messages consumers ...");
        MessageConsumer consumer = QueueHelper.getConsumer();
        int inputMsgQueuePoolSize = 30;
        ExecutorService msgPool = Executors.newFixedThreadPool(inputMsgQueuePoolSize);
        for (int i = 0; i < inputMsgQueuePoolSize; i++) {
            msgPool.submit(new InputRequestHandler(i, consumer));
        }

	}
}
