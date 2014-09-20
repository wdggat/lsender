package com.liu.dispatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration {
    private static Logger logger = Logger.getLogger(Configuration.class);

    private Properties properties;

    public final static String DEFAULT_CONF_PATH
            = System.getProperty("user.dir") + "/conf/conf.properties";
    public final static String REQUEST_LOG_FOLDER = "logs/";

    public final static String RES_CODE = "code";
    public final static String RES_MSGID = "msgId";
    public final static String RES_ERRMSG = "errMsg";
    public final static int RES_CODE_SUCC = 200;
    public final static int RES_CODE_INPUT_INVALID = 417;
    public final static int RES_CODE_SERVER_ERROR = 500;
    
    private final static String MSG_SENT_SUCC = "logs/msg_done";
    private final static String MSG_SENT_FAIL = "logs/msg_failed";

    private InputStream getDefaultConfInputStream() {
        try {
            return new FileInputStream(DEFAULT_CONF_PATH);
        } catch (FileNotFoundException e) {
            logger.error("No configuration file found!", e);
            System.exit(1);
            return null;
        }
    }

    public Configuration() {
        loadConf(getDefaultConfInputStream());
    }

    protected void loadConf(InputStream in) {
        properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
        }
    }

    public void reloadConf() {
        loadConf(getDefaultConfInputStream());
    }
    
    public String getMQHost() {
    	return properties.getProperty("mq_host");
    }
    
    public String getMQQueueName() {
    	return properties.getProperty("mq_queuename");
    }

    public int getMQPort() {
    	return Integer.parseInt(properties.getProperty("mq_port"));
    }
    
    public String getMQUser(){
    	return properties.getProperty("mq_user");
    }
    
    public String getMQPassword() {
    	return properties.getProperty("mq_password");
    }
    
    public String getMsgDoneLogf() {
    	return MSG_SENT_SUCC;
    }
    
    public String getMsgFailLogf() {
    	return MSG_SENT_FAIL;
    }
    
    public String getMailHost163() {
    	return properties.getProperty("mail.163.host");
    }
    
    public String getMail163FromAddr() {
    	return properties.getProperty("mail.163.from.addr");
    }
    
    public String getMail163FromPassword() {
    	return properties.getProperty("mail.163.from.password");
    }
    
    public String getMailFromName() {
    	return properties.getProperty("mail.from.name");
    }
    
    public int getMail163Port() {
    	return Integer.parseInt(properties.getProperty("mail.163.port"));
    }
    
    public String getRedisServerMaster() {
    	return properties.getProperty("redis_server_master");
    }
    
    public String getRedisServerSlave() {
    	return properties.getProperty("redis_server_slave");
    }
    
    public String getBaiduPushApiKey() {
    	return properties.getProperty("baidu_api_key");
    }
    
    public String getBaiduPushSecretKey() {
    	return properties.getProperty("baidu_secret_key");
    }
    
    public String getDownloadLink() {
    	return properties.getProperty("download_link");
    }
}
