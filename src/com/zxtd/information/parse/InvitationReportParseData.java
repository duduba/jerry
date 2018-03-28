package com.zxtd.information.parse;

import java.util.Map;

public class InvitationReportParseData extends ParseData {
	public static final String IS_SUCCESS = "isSuccess";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String recvData = params.get("ZXTD.DATA.responsecode").toString();
		result.put(IS_SUCCESS, "200".equals(recvData));
		return result;
	}

}
