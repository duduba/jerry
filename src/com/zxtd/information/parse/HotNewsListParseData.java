package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.util.Utils;

public class HotNewsListParseData extends ParseData {
	public static String HOT_NEWS_LIST_KEY = "hotNewsListData";
	public final static String PAGE_COUNT_KEY = "pageCount";
	
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		List<Bean> hotNewsListData = new ArrayList<Bean>();
		result.put(HOT_NEWS_LIST_KEY, hotNewsListData);
		
		String pageCount = (String)params.get("ZXTD.pagecount"); 
		result.put(PAGE_COUNT_KEY, pageCount);
		
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		if(data != null){
			for(int i = 0; i < data.size(); i++) {
				Map<String, Object> item = data.get(i);
				
				hotNewsListData.add(getHotNewsBean(item));
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			hotNewsListData.add(getHotNewsBean(params));
		}
		return result;
	}
	
	private NewBean getHotNewsBean(Map<String, Object> data) {
		NewBean hotNewsBean = new NewBean();
		hotNewsBean.newId = (String)data.get("ZXTD.DATA.item.id");
		hotNewsBean.newTitle = (String)data.get("ZXTD.DATA.item.title");
		hotNewsBean.newOutline = (String)data.get("ZXTD.DATA.item.summary");
		hotNewsBean.iconUrl = (String)data.get("ZXTD.DATA.item.image-url");
		hotNewsBean.postCount = (String)data.get("ZXTD.DATA.item.postcount");
		hotNewsBean.infoUrl = (String)data.get("ZXTD.DATA.item.http-url");
		hotNewsBean.flag = (String)data.get("ZXTD.DATA.item.ispublish");
		hotNewsBean.operid = (String)data.get("ZXTD.DATA.item.operid");
		return hotNewsBean;
	}

}
