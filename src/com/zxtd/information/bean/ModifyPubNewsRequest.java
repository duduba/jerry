package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class ModifyPubNewsRequest extends RequestBean {
	private String title = "";
	private String content = "";
	private String imageUrl = "";
	private String type = "";
	private String id = "";
	public ModifyPubNewsRequest(String title, String content, String imageUrl, String type, String id){
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
		this.type = type;
		this.id = id;
	}
	@Override
	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("title", title);
		map.put("content", content);
		map.put("image-url", imageUrl);
		map.put("type", type);
		return map;
	}

}
