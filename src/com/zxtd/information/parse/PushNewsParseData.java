package com.zxtd.information.parse;

import java.util.Map;

import com.zxtd.information.bean.PushNewsBean;

public class PushNewsParseData extends ParseData {
	public static final String PUBSH_NEW_BEAN_KEY = "pushNewsBean";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String pushState = (String)params.get("ZXTD.push-status");
		System.out.println("---------------------pushState = " + pushState + "----------------------");
		if((pushState != null) && pushState.equals("1")){
			PushNewsBean pushNewsBean = new PushNewsBean();
			pushNewsBean.newsId = (String)params.get("ZXTD.DATA.id");
			pushNewsBean.newsTitle = (String)params.get("ZXTD.DATA.title");
			pushNewsBean.postCount = (String)params.get("ZXTD.DATA.postcount");
			pushNewsBean.httpUrl = (String)params.get("ZXTD.DATA.http-url");
			pushNewsBean.flag = (String)params.get("ZXTD.DATA.flag");
			pushNewsBean.newsType = (String)params.get("ZXTD.DATA.type");
			result.put(PUBSH_NEW_BEAN_KEY, pushNewsBean);
		} 
		return result;
	}

}
