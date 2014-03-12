package com.liu.message;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class EmailMsg{
	private List<String> to;
	private String subject;
	private String content;

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
	
	public EmailMsg(List<String> to, String subject, String content) {
		super();
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public static EmailMsg getFromJson(String jsonStr) {
		return JSON.parseObject(jsonStr, EmailMsg.class);
	}
	
	public static EmailMsg getFromAppMessage(AppMessage appMsg) {
		return new EmailMsg(appMsg.getTo(), appMsg.getSubject(), appMsg.getContent());
	}
	
	public String toJsonStr() {
		return JSON.toJSONString(this);
	}

}
