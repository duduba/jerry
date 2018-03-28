package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class ModifUserInfoRequest extends RequestBean {
	public String userid = "";
	public String userAccount = "";
	public String nickname = "";
	public String aspiration = "";
	public String location = "";
	public String profession = "";
	public String sex = "";
	public String birthday = "";
	
	
	@Override
	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", userid);
		map.put("user-account", userAccount);
		map.put("nickname", nickname);
		map.put("aspiration", aspiration);
		map.put("location", location);
		map.put("profession", profession);
		map.put("sex", sex);
		map.put("birthday", birthday);
		return map;
	}

}
