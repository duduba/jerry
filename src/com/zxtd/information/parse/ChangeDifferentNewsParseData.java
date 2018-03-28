package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.util.Utils;

public class ChangeDifferentNewsParseData extends ParseData {
	public final static String IS_DATA_OVER_KEY = "isDataOver";
	public final static String NEW_BEAN_LIST_KEY = "listDifferentNewsBean";
	public final static String PAGE_COUNT_KEY = "pageCount";
	private Result mResult;
	public ChangeDifferentNewsParseData(){
		mResult = new Result();
	}
	
	@Override
	public Result parseMap(Map<String, Object> params) {
		
		List<Bean> listDifferentNewsBean = new ArrayList<Bean>();
		mResult.put(NEW_BEAN_LIST_KEY, listDifferentNewsBean);
		
		String pageCount = (String)params.get("ZXTD.pagecount");
		mResult.put(PAGE_COUNT_KEY, pageCount);
		
		String dataState = (String)params.get("ZXTD.dataover");
		if(dataState != null && dataState.equals("1")) {
			mResult.put(IS_DATA_OVER_KEY, true);
			
		} else {
			mResult.put(IS_DATA_OVER_KEY, false);
		}
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		if(data != null){
			for(int i = 0; i < data.size(); i++) {
				Map<String, Object> item = data.get(i);
				listDifferentNewsBean.add(getDifferentNewsBean(item));
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return mResult;
			}
			listDifferentNewsBean.add(getDifferentNewsBean(params));
		}
		return mResult;
	}
	
	private NewBean getDifferentNewsBean(Map<String, Object> data) {
		NewBean differentNewsBean = new NewBean();
		differentNewsBean.newId = (String)data.get("ZXTD.DATA.item.id");
		differentNewsBean.newTitle = (String)data.get("ZXTD.DATA.item.title");
		differentNewsBean.newOutline = (String)data.get("ZXTD.DATA.item.summary");
		differentNewsBean.iconUrl = (String)data.get("ZXTD.DATA.item.image-url");
		differentNewsBean.postCount = (String)data.get("ZXTD.DATA.item.postcount");
		differentNewsBean.infoUrl = (String)data.get("ZXTD.DATA.item.http-url");
		differentNewsBean.flag = (String)data.get("ZXTD.DATA.item.ispublish");
		differentNewsBean.operid = (String)data.get("ZXTD.DATA.item.operid");
		
		return differentNewsBean;
	}

	public void addResult( String key, Object value){
		mResult.put(key, value);
	}
}
