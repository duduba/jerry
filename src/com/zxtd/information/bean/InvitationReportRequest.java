package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class InvitationReportRequest extends RequestBean{
	private String newsId = "";
	private String userId = "";
	private String content = "";
	private String infotype = "";
	
	public InvitationReportRequest(String newsId, String userId, String content, String infotype) {
		this.newsId = newsId;
		this.userId = userId;
		this.content = content;
		this.infotype = infotype;
	}
	
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("infoid", newsId);
		map.put("userid", userId);
		map.put("content", content);
		map.put("infotype", infotype);
		return map;
	}
}
