package com.zxtd.information.parse;

import java.util.Map;

import android.util.Log;

public class InvitationReplyParseData extends ParseData {
	public final static String IS_SUCCESS = "isSuccess";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String mRecvData = (String)params.get("ZXTD.DATA.responsecode");
		Log.i(this.getClass().getName(),"返回数据：" + mRecvData);
		result.put(IS_SUCCESS, "200".equals(mRecvData));
		return result;
	}

}
