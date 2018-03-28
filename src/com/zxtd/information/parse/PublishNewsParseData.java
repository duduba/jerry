package com.zxtd.information.parse;

import java.util.Map;

import android.util.Log;

public class PublishNewsParseData extends ParseData {
	public final static String IS_SUCCESS = "isSuccess";
	public final static String PUB_TIME = "pubTime";
	public final static String PUB_URL = "pubUrl";
	public final static String PUB_ID = "pubId";
	private Result result = null;
	public PublishNewsParseData(){
		result = new Result();
	}
	@Override
	public Result parseMap(Map<String, Object> params) {
		String mRecvData = (String)params.get("ZXTD.DATA.responsecode");
		String pubTime = (String)params.get("ZXTD.DATA.time");
		String pubUrl = (String)params.get("ZXTD.DATA.http-url");
		String pubId = (String)params.get("ZXTD.DATA.id");
		Log.i(this.getClass().getName(),"返回数据：" + mRecvData);
		result.put(IS_SUCCESS, "200".equals(mRecvData));
		result.put(PUB_TIME, pubTime);
		result.put(PUB_URL, pubUrl);
		result.put(PUB_ID, pubId);
		return result;
	}

	public void addResult(String key, String value){
		result.put(key, value);
	}
	
}
