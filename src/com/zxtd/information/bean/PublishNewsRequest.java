package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class PublishNewsRequest extends RequestBean{
	private String title = "";
	private String nickname = "";
	private String content = "";
	private String imageUrl = "";
	private String type = "";
	private String userId = "";
	
	public PublishNewsRequest(String title, String nickname, String content, String imageUrl, String type, String userId) {
		this.title = title;
		this.nickname = nickname;
		this.content = content;
		this.imageUrl = imageUrl;
		this.type = type;
		this.userId = userId;
	}
	
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		map.put("nickname", nickname);
		map.put("content", content);
		map.put("image-url", imageUrl == null ? "" : imageUrl);
		map.put("type", type);
		map.put("userid", userId);
		return map;
	}
}
