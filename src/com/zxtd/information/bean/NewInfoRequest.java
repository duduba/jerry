package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class NewInfoRequest extends RequestBean{
	private String newId;
	private String flag;
	private String width;
	
	public NewInfoRequest(String id, String flag, String width) {
		this.newId = id;
		this.flag = flag;
		this.width = width;
	}
	
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("id", newId);
		map.put("flag", flag);
		map.put("width", width);
		return map;
	}
}
