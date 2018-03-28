package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class ModifPortraitRequest extends RequestBean {
	private String userid;
	private String portraitUrl;
	public ModifPortraitRequest(String userid, String portraitUrl){
		this.userid = userid;
		this.portraitUrl = portraitUrl;
	}
	@Override
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", userid);
		map.put("portrait-url", portraitUrl);
		return map;
	}

}
