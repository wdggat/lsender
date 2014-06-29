package com.liu.helper;

import org.apache.log4j.Logger;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.liu.message.Message;

public class BaiduPushHelper {
	private static final Logger logger = Logger.getLogger(BaiduPushHelper.class);
	private static BaiduChannelClient channelClient;
	
	public static void init(String apikey, String secretkey) {
		/*
         * @brief 推送单播消息(消息类型为透传，由开发方应用自己来解析消息内容) message_type = 0 (默认为0)
         */

        ChannelKeyPair pair = new ChannelKeyPair(apikey, secretkey);

        // 2. 创建BaiduChannelClient对象实例
        channelClient = new BaiduChannelClient(pair);

        // 3. 若要了解交互细节，请注册YunLogHandler类
        channelClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                logger.info(event.getMessage());
            }
        });
	}
	
	// 手机端的ChannelId， 手机端的UserId
	public static boolean pushMessage(String userid, Message message) {
		try {
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(3); // device_type => 1: web 2: pc 3:android 4:ios 5:wp
//			request.setChannelId(channelid);   // only baiduUserId works
			request.setUserId(userid);
			request.setMessage(message.toJson());

			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
			return response.getSuccessAmount() != 0;
		} catch (ChannelClientException e) {
			e.printStackTrace();
			return false;
		} catch (ChannelServerException e) {
			e.printStackTrace();
			return false;
		}
	}
}
