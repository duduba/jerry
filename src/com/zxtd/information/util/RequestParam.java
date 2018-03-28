package com.zxtd.information.util;

import com.zxtd.information.application.ZXTD_Application;

public class RequestParam {
	
	public String getUuid() {
		return Utils.UUID;
	}
	public String getVersion() {
		return ZXTD_Application.versionName;
	}
	
	
}
