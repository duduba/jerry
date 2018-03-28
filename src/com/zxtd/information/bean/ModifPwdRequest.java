package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class ModifPwdRequest extends RequestBean {
	private String userid;
	private String originalPassword;
	private String newPassword;
	public ModifPwdRequest(String userid, String originalPassword, String newPassword){
		this.userid = userid;
		this.originalPassword = originalPassword;
		this.newPassword = newPassword;
	}
	@Override
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", userid);
		map.put("original-password", originalPassword);
		map.put("new-password", newPassword);
		return map;
	}

}
