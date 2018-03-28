package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class ChangeDifferentNewsRequest extends RequestBean{
	private String id = "";
	private String type = "";
	private String noimg = "";
	
	public ChangeDifferentNewsRequest(String id, String type, String noimg){
		this.id = id;
		this.type = type;
		this.noimg = noimg;
	}
	
	public Map<String, String> getMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("type", type);
		map.put("noimg", noimg);
		return map;
	}
}
