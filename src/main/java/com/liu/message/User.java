package com.liu.message;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.liu.helper.Utils;

public class User {
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	public static final int GENDER_UNSET = 2;

	private static final String SHOW_MALE = "汉子";
	private static final String SHOW_FEMALE = "妹子";
	private static final String SHOW_GENDER_UNSET = "路人而已";

	private String email;
	private int gender;
	private String province;
	private long birthday;
	private String phone;
	private String password;
	private String uid;

	public User() {
	}

	public User(String emailAddr, int gender, String province, long birthday,
			String phone, String password, String uid) {
		super();
		this.email = emailAddr;
		this.gender = gender;
		this.province = province;
		this.birthday = birthday;
		this.phone = phone;
		this.password = password;
		this.uid = uid;
	}

	public static User fromJsonStr(String json) {
		if (StringUtils.isBlank(json))
			return new User("", GENDER_UNSET, "", 0, "", "", "");
		return JSON.parseObject(json, User.class);
	}
	
	public boolean isNullUser() {
		return email.equals("");
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toJson() {
		return JSON.toJSONString(this);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return toJson();
	}

	public String showGender() {
		switch (gender) {
		case MALE:
			return SHOW_MALE;
		case FEMALE:
			return SHOW_FEMALE;
		case GENDER_UNSET:
			return SHOW_GENDER_UNSET;
		default:
			return "";
		}
	}
	
	public String showBirthday() {
		return Utils.showDate(birthday);
	}
}
