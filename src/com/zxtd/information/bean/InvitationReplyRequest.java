package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class InvitationReplyRequest extends RequestBean{
	private String newsId = "";
	private String invitationId = "";
	private String userId = "";
	private String content = "";
	private String infotype = "";
	
	public InvitationReplyRequest(String newsId, String invitationId, String userId, String content, String infotype) {
		this.newsId = newsId;
		this.invitationId = invitationId;
		this.userId = userId;
		this.content = content;
		this.infotype = infotype;
	}
	
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("infoid", newsId);
		map.put("id", invitationId);
		map.put("userid", userId);
		map.put("content", content);
		map.put("infotype", infotype);
		return map;
	}
}
