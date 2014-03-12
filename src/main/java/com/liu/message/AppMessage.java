package com.liu.message;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class AppMessage {
    private String dataType;
    private String msgId;
    private String msgType;
    private long occurTime;
    private long costTime;
    private String userId;
    private String appChannel;
    private String appVersion;
    private List<String> to;
    private String subject;
    private String content;
    private String deviceUdid;
    private String deviceOs;
    private String deviceNetWork;
    private String deviceOsVersion;
    private String deviceModel;
    private Map<String, String> attributes;
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public long getOccurTime() {
		return occurTime;
	}
	public void setOccurTime(long occurTime) {
		this.occurTime = occurTime;
	}
	public long getCostTime() {
		return costTime;
	}
	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAppChannel() {
		return appChannel;
	}
	public void setAppChannel(String appChannel) {
		this.appChannel = appChannel;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDeviceUdid() {
		return deviceUdid;
	}
	public void setDeviceUdid(String deviceUdid) {
		this.deviceUdid = deviceUdid;
	}
	public String getDeviceOs() {
		return deviceOs;
	}
	public void setDeviceOs(String deviceOs) {
		this.deviceOs = deviceOs;
	}
	public String getDeviceNetWork() {
		return deviceNetWork;
	}
	public void setDeviceNetWork(String deviceNetWork) {
		this.deviceNetWork = deviceNetWork;
	}
	public String getDeviceOsVersion() {
		return deviceOsVersion;
	}
	public void setDeviceOsVersion(String deviceOsVersion) {
		this.deviceOsVersion = deviceOsVersion;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public String toJsonStr() {
		return JSON.toJSONString(this);
	}
	@Override
	public String toString(){
		return toJsonStr();
	}
	public static AppMessage getFromInputJson(String json) {
		return JSON.parseObject(json, AppMessage.class);
	}
    
}
