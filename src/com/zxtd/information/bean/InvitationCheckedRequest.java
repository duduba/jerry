package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class InvitationCheckedRequest extends RequestBean{
	private String newsId = "";
	private String invitationId = "";
	private String infotype = "";
	
	public InvitationCheckedRequest(String newsId, String invitationId, String infoType) {
		this.newsId = newsId;
		this.invitationId = invitationId;
		this.infotype = infoType;
	}
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("infoid", newsId);
		map.put("id", invitationId);
		map.put("infotype", infotype);
		return map;
	}
}
