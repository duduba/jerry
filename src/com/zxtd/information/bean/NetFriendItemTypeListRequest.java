package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class NetFriendItemTypeListRequest extends RequestBean{
	private String id = "";
	private String type = "";
	private String posterId = "";
	
	public NetFriendItemTypeListRequest(String id, String type, String posterId){
		this.id = id;
		this.type = type;
		this.posterId = posterId;
	}
	
	public Map<String, String> getMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("type", type);
		map.put("posterid", posterId);
		return map;
	}
}
