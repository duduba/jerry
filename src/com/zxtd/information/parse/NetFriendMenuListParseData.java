package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NetFriendWebBean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.util.Utils;

public class NetFriendMenuListParseData extends ParseData {
	public final static String MENU_LIST_WEB_BEAN_KEY = "menuListWebBean";
	public final static String NET_FRIEND_BEAN_LIST_KEY = "netFriendNewsBean";
	public final static String PAGE_COUNT_KEY = "pageCount";
	
	private Result result;
	
	public NetFriendMenuListParseData(){
		result = new Result();
	}
	
	
	@Override
	public Result parseMap(Map<String, Object> params) {
		
		String pageCount = (String)params.get("ZXTD.pagecount"); 
		result.put(PAGE_COUNT_KEY, pageCount);
		
		NetFriendWebBean menuListWebBean = new NetFriendWebBean();
		result.put(MENU_LIST_WEB_BEAN_KEY, menuListWebBean);
		String tempFlag = (String)params.get("ZXTD.POSTERDATA.flag");
		if(tempFlag != null && tempFlag.equals("1")){
			menuListWebBean.imageUrl = (String)params.get("ZXTD.POSTERDATA.img-url");
			menuListWebBean.httpUrl = (String)params.get("ZXTD.POSTERDATA.http-url");
			menuListWebBean.flags = (String)params.get("ZXTD.POSTERDATA.flag");
			menuListWebBean.posterId = (String)params.get("ZXTD.POSTERDATA.id");
		}

		List<Bean> netFriendNewsBean = new ArrayList<Bean>();
		
		result.put(NET_FRIEND_BEAN_LIST_KEY, netFriendNewsBean);
		
		List<Map<String, Object>> netFriendData = (List<Map<String, Object>>)params.get("ZXTD.DATA.iteminfo");
		
		if(netFriendData != null) {
			for(int i = 0; i < netFriendData.size(); i++){
				Map<String, Object> typeMap = netFriendData.get(i);
				netFriendNewsBean.add(getNewTypeBean(typeMap));
				List<Map<String, Object>> netFriendItemData = (List<Map<String, Object>>)typeMap.get("ZXTD.DATA.iteminfo.item");
				if(netFriendItemData != null) {
					for(int j = 0; j < netFriendItemData.size(); j++) {
						Map<String, Object> itemMap = netFriendItemData.get(j);
						netFriendNewsBean.add(getDownloadNewsListBean(itemMap));
					}
				} else {
					String id = (String)typeMap.get("ZXTD.DATA.iteminfo.item.id");
					if(Utils.isEmpty(id)){
						continue;
					}
					
					netFriendNewsBean.add(getDownloadNewsListBean(typeMap));
				}
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.iteminfo.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			
			netFriendNewsBean.add(getNewTypeBean(params));
		}
		return result;
	}
	
	private NewTypeBean getNewTypeBean(Map<String, Object> perItemData) {
		NewTypeBean typeBean = new NewTypeBean();
		typeBean.id = (String)perItemData.get("ZXTD.DATA.iteminfo.id");
		typeBean.name = (String)perItemData.get("ZXTD.DATA.iteminfo.type");
		return typeBean;
	}
	
	private DownloadNewsListBean getDownloadNewsListBean(Map<String, Object> perItemData) {
		DownloadNewsListBean bean =  new DownloadNewsListBean();
		bean.id = (String)perItemData.get("ZXTD.DATA.iteminfo.item.id");
		bean.title = Utils.reverseTransformContent((String)perItemData.get("ZXTD.DATA.iteminfo.item.title"));
		bean.nickname = Utils.reverseTransformContent((String)perItemData.get("ZXTD.DATA.iteminfo.item.nickname"));
		bean.time = (String)perItemData.get("ZXTD.DATA.iteminfo.item.time");
		bean.postcount = Utils.reverseTransformContent((String)perItemData.get("ZXTD.DATA.iteminfo.item.postcount"));
		bean.infoUrl = (String)perItemData.get("ZXTD.DATA.iteminfo.item.http-url");
		bean.address = Utils.reverseTransformContent((String)perItemData.get("ZXTD.DATA.iteminfo.item.address"));
		bean.flag = (String)perItemData.get("ZXTD.DATA.iteminfo.item.flag");
		bean.operid = (String)perItemData.get("ZXTD.DATA.iteminfo.item.operid");
		return bean;
	}

	public void addResult(String key,Object value){
		result.put(key, value);
	}
}
