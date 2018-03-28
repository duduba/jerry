package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class PushNewsRequest extends RequestBean{
	private String newsId = null;
	
	public PushNewsRequest(String id) {
		this.newsId = id;
	}
	@Override
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", newsId);
		return map;
	}
}
