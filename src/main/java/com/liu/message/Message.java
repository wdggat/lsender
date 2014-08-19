package com.liu.message;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.liu.helper.Utils;

public class Message implements Comparable<Message> {
	private static final String EMPTY_SUBJECT = "";
	private String from;
	private String fromUid;
	private String to;
	private String subject;
	private long time;
	private String content;
	private long localTime;
	private DataType dataType;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromUid() {
		return fromUid;
	}

	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            unix epoch time, length_10
	 */
	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public long getLocalTime() {
		return localTime;
	}

	public void setLocalTime(long localTime) {
		this.localTime = localTime;
	}

	public Message() {
	}

	/**
	 * @param associate
	 *            The uid or email that this message sent to
	 * @param time
	 *            Unix epoch time, length_10
	 */
	public Message(String from, String fromUid, String to, String subject, long time,
			String content, DataType dataType, long localTime) {
		super();
		this.from = from;
		this.fromUid = fromUid;
		this.to = to;
		this.subject = subject;
		this.time = time;
		this.content = content;
		this.dataType = dataType;
		this.localTime = localTime;
	}
	
	public String toJson() {
		return JSON.toJSONString(this);
	}

	public String getFormatedTime() {
		return Utils.showTime(time);
	}

	@Override
	public int compareTo(Message another) {
		long anotherTime = another.getLocalTime();
		return localTime > anotherTime ? 1 : (localTime == anotherTime ? 0 : -1);
	}

	public static Message quickMessage(String from, String fromUid, String to, String content) {
		return new Message(from, fromUid, to, EMPTY_SUBJECT,
				System.currentTimeMillis() / 1000, content, DataType.QUICK_MSG, System.currentTimeMillis() / 1000);
	}
	
	public static Message getFromInputJson(String inputJson) {
		return JSON.parseObject(inputJson, Message.class);
	}

	public boolean isSentBy(String uname) {
		if(StringUtils.isEmpty(uname))
			return false;
		return uname.equals(from);
	}
	
	public boolean isToEmail() {
		return to.contains("@");
	}
	
	public boolean isValidMessage() {
		return !StringUtils.isBlank(from) && !StringUtils.isBlank(fromUid) && !StringUtils.isBlank(to) && !StringUtils.isBlank(content); 
	}
	
	public void normalize() {
    	setContent(content.trim());
    	setFrom(from.trim());
    	setFromUid(fromUid.trim());
    	setSubject(subject.trim());
    	setTo(to.trim());
    }
	
}
