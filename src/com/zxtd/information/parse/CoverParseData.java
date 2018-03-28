package com.zxtd.information.parse;

import java.util.Map;

import com.zxtd.information.bean.CoverBean;

public class CoverParseData extends ParseData {
	public static final String COVER_BEAN_KEY = "coverBeans";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String state = (String)params.get("ZXTD.status");
		if((state != null) && state.equals("1")) {
			System.out.println("---------------------解析封面数据----------------------");
			CoverBean coverBean = new CoverBean();
			coverBean.id = (String)params.get("ZXTD.DATA.id");
			coverBean.imageUrl = (String)params.get("ZXTD.DATA.img-url");
			coverBean.describe = (String)params.get("ZXTD.DATA.describe");
			coverBean.time = (String)params.get("ZXTD.DATA.time");
			result.put(COVER_BEAN_KEY, coverBean);
		}else {
			System.out.println("---------------------无更新封面数据----------------------");
		}
		return result;
	}
}
