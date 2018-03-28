package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.util.Utils;

public class NewsTypeParseData extends ParseData {
	/**
	 * 新闻类型
	 * */
	public static final String NEW_TYPE_LIST_KEY = "typeBeans";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		
		List<Bean> typeBeans = new ArrayList<Bean>();
		
		result.put(NEW_TYPE_LIST_KEY, typeBeans);
		
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		if(data != null){
			for(int i = 0; i < data.size(); i++) {
				Map<String, Object> item = data.get(i);
				
				typeBeans.add(getNewTypeBean(item));
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			typeBeans.add(getNewTypeBean(params));
		}
		return result;
	}
	private NewTypeBean getNewTypeBean(Map<String, Object> data){
		NewTypeBean newTypeBean = new NewTypeBean();
		
		newTypeBean.id = (String)data.get("ZXTD.DATA.item.id");
		newTypeBean.name = (String)data.get("ZXTD.DATA.item.name");
		newTypeBean.flag = (String)data.get("ZXTD.DATA.item.flag");
		
		return newTypeBean;
	}

}
