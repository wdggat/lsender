package com.liu.helper;

import org.apache.log4j.PropertyConfigurator;

import com.liu.dispatcher.Configuration;
import com.liu.message.DataType;
import com.liu.message.Message;

public class BaiduPushHelperTest {
	// Succeed to bind baidu-push-server, appid - 2967963, userId -
	// 820813141354190040, channelId - 3947634279025921276
	public static void main(String[] args) {
		PropertyConfigurator.configure(Configuration.DEFAULT_CONF_PATH);
		Configuration conf = new Configuration();
		BaiduPushHelper.init(conf.getBaiduPushApiKey(), conf.getBaiduPushSecretKey());
		sendTohz126();
	}

	// EMUL
	private static void sendTohz126() {
		Message msg = new Message();
		msg.setContent("测试baidu push.");
		msg.setFrom("hzliuxiaolong@163.com");
		msg.setFromUid("10001");
		msg.setDataType(DataType.QUICK_MSG);
		msg.setSubject("测试消息的主题");
		msg.setTime(System.currentTimeMillis() / 1000);
		msg.setTo("hzliuxiaolong@126.com");
//		msg.setTo("10002");
		String userId = "820813141354190040";
		long channelId = 3947634279025921276L;
		BaiduPushHelper.pushMessage(userId, msg);
	}

	// MI-ONE
	private static void sendTohz163() {
		Message msg = new Message();
		msg.setContent("测试baidu push.");
		msg.setFrom("hzliuxiaolong@163.com");
		msg.setFromUid("10001");
		msg.setDataType(DataType.QUICK_MSG);
		msg.setSubject("测试消息的主题");
		msg.setTime(System.currentTimeMillis() / 1000);
		msg.setTo("hzliuxiaolong@163.com");
		String userId = "799354824120430743";
		long channelId = 4320396776870498748L;
		BaiduPushHelper.pushMessage(userId, msg);
	}
}
