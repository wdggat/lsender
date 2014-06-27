package com.liu.helper;

import com.liu.dispatcher.Configuration;
import com.liu.message.DataType;
import com.liu.message.Message;

public class BaiduPushHelperTest {
	//Succeed to bind baidu-push-server, appid - 2967963, userId - 820813141354190040, channelId - 3947634279025921276
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		BaiduPushHelper.init(conf.getBaiduPushApiKey(), conf.getBaiduPushSecretKey());
		
		Message msg = new Message();
		msg.setContent("测试baidu push.");
		msg.setFrom("hzliuxiaolong@163.com");
		msg.setDataType(DataType.QUICK_MSG);
		msg.setSubject("测试消息的主题");
		msg.setTime(System.currentTimeMillis() / 1000);
		msg.setTo("shliuxiaolong@163.com");
		String userId = "820813141354190040";
		long channelId = 3947634279025921276L;
		BaiduPushHelper.pushMessage(userId, channelId, msg);
	}
}
