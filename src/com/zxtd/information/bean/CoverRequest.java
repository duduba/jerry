package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class CoverRequest extends RequestBean {
	private String id;
	private String width;
	private String high;
	
	public CoverRequest(String id, String width, String high) {
		this.id = id;
		this.width = width;
		this.high = high;
	}
	
	@Override
	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("width", width);
		map.put("high", high);
		return map;
	}

}
