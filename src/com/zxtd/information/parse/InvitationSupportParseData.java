package com.zxtd.information.parse;

import java.util.Map;

import android.util.Log;

public class InvitationSupportParseData extends ParseData {
	public static final String IS_SUCCESS = "isSuccess";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String recvData = params.get("ZXTD.DATA.responsecode").toString();
		Log.i(this.getClass().getName(),"返回数据：" + recvData);
		result.put(IS_SUCCESS, "200".equals(recvData));
		return result;
	}

}
