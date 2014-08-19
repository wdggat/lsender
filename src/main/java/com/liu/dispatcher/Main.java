package com.liu.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.liu.helper.BaiduPushHelper;
import com.liu.helper.QueueHelper;
import com.liu.helper.RedisHelper;
import com.liu.message.InputRequestHandler;
import com.rabbitmq.client.QueueingConsumer;

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
        
        logger.info("Initializing RedisHelper");
        RedisHelper.init(conf.getRedisServerMaster(), conf.getRedisServerSlave());

        logger.info("initing receipt mq...");
        if(!QueueHelper.init()){
        	logger.fatal("Error occurs during initing receipt mq, abort.");
        	return;
        }
        
        logger.info("Initializing BaiduPushHelper");
        BaiduPushHelper.init(conf.getBaiduPushApiKey(), conf.getBaiduPushSecretKey());
        
/*        logger.info("Starting HttpClient ...");
        HttpClientVM.init(200, 40);*/

        logger.info("Setup messages consumers ...");
        int inputMsgQueuePoolSize = 5;
        ExecutorService msgPool = Executors.newFixedThreadPool(inputMsgQueuePoolSize);
        for (int i = 0; i < inputMsgQueuePoolSize; i++) {
        	QueueingConsumer consumer = QueueHelper.generateConsumer();
            msgPool.submit(new InputRequestHandler(i, consumer));
        }

	}
}
