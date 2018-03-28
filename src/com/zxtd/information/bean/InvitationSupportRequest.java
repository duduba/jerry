package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class InvitationSupportRequest extends RequestBean{
	private String newsId = "";
	private String invitationId = "";
	private String infotype = "";
	
	public InvitationSupportRequest(String newsId, String invitationId, String infotype) {
		this.newsId = newsId;
		this.invitationId = invitationId;
		this.infotype = infotype;
	}
	
	public Map<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("infoid", newsId);
		map.put("id", invitationId);
		map.put("infotype", infotype);
		return map;
	}
}
