package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.util.Utils;

public class NewsHeadlineParseData extends ParseData {
	public static final String NEW_HEAD_LIST_KEY = "newsHeadlineList";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		
		List<Bean> newsHeadlineList = new ArrayList<Bean>();
		
		result.put(NEW_HEAD_LIST_KEY, newsHeadlineList);
		
		List<Map<String, Object>> newsHeadlineData = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		if(newsHeadlineData != null){
			for(int i = 0; i < newsHeadlineData.size(); i++) {
				Map<String, Object> item = newsHeadlineData.get(i);
				
				newsHeadlineList.add(getHeadlineBean(item));
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			newsHeadlineList.add(getHeadlineBean(params));
		}
		return result;
	}
	private NewBean getHeadlineBean(Map<String, Object> data) {
		NewBean headlineBean = new NewBean();
		
		headlineBean.newId = (String)data.get("ZXTD.DATA.item.id");
		headlineBean.newTitle = (String)data.get("ZXTD.DATA.item.title");
		headlineBean.iconUrl = (String)data.get("ZXTD.DATA.item.image-url");
		headlineBean.postCount = (String)data.get("ZXTD.DATA.item.postcount");
		headlineBean.infoUrl = (String)data.get("ZXTD.DATA.item.http-url");
		return headlineBean;
	}

}
