package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class HotNewsRequest extends RequestBean {
	private String noimg = "";
	public HotNewsRequest(String noimg){
		this.noimg = noimg;
	}
	
	@Override
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("noimg", noimg);
		return map;
	}

}
