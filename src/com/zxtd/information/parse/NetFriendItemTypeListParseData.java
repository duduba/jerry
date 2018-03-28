package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NetFriendWebBean;
import com.zxtd.information.util.Utils;

public class NetFriendItemTypeListParseData extends ParseData {
	public final static String WEB_BEAN_KEY = "webBean";
	public final static String NET_FRIEND_LIST_KEY = "netFriendItemTypeList";
	public final static String IS_DATA_OVER = "isDataOver";
	public final static String PAGE_COUNT_KEY = "pageCount";
	private Result result;
	public NetFriendItemTypeListParseData(){
		result = new Result();
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public Result parseMap(Map<String, Object> params) {
		String pageCount = (String)params.get("ZXTD.pagecount"); 
		result.put(PAGE_COUNT_KEY, pageCount);
		
		NetFriendWebBean webBean = new NetFriendWebBean();
		result.put(WEB_BEAN_KEY, webBean);
		String tempFlag = (String)params.get("ZXTD.POSTERDATA.flag");
		if(tempFlag != null && tempFlag.equals("1")){
			webBean.imageUrl = (String)params.get("ZXTD.POSTERDATA.img-url");
			webBean.httpUrl = (String)params.get("ZXTD.POSTERDATA.http-url");
			webBean.flags = (String)params.get("ZXTD.POSTERDATA.flag");
			webBean.posterId = (String)params.get("ZXTD.POSTERDATA.id");
		}
		
		List<Bean> netFriendItemTypeList = new ArrayList<Bean>();
		result.put(NET_FRIEND_LIST_KEY, netFriendItemTypeList);
		
		String dataState = (String)params.get("ZXTD.dataover");
		if(dataState != null && dataState.equals("1")) {
			result.put(IS_DATA_OVER, true);
		} else {
			result.put(IS_DATA_OVER, false);
		}
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		
		if(data != null){
			for(int i = 0; i < data.size(); i++) {
				Map<String, Object> item = data.get(i);
				netFriendItemTypeList.add(getNetFriendItemTypeBean(item));
			}
		}else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			netFriendItemTypeList.add(getNetFriendItemTypeBean(params));
		}
		return result;
	}
	
	private DownloadNewsListBean getNetFriendItemTypeBean(Map<String, Object> perItem) {
		DownloadNewsListBean downloadNewsList = new DownloadNewsListBean();
		
		downloadNewsList.id = (String)perItem.get("ZXTD.DATA.item.id");	
		downloadNewsList.title = Utils.reverseTransformContent((String)perItem.get("ZXTD.DATA.item.title"));
		downloadNewsList.nickname = Utils.reverseTransformContent((String)perItem.get("ZXTD.DATA.item.nickname"));
		downloadNewsList.time = (String)perItem.get("ZXTD.DATA.item.time");
		downloadNewsList.postcount = Utils.reverseTransformContent((String)perItem.get("ZXTD.DATA.item.postcount"));
		downloadNewsList.infoUrl = (String)perItem.get("ZXTD.DATA.item.http-url");
		downloadNewsList.flag = (String)perItem.get("ZXTD.DATA.item.flag");
		downloadNewsList.operid = (String)perItem.get("ZXTD.DATA.item.operid");
		downloadNewsList.address = Utils.reverseTransformContent((String)perItem.get("ZXTD.DATA.item.address"));
		downloadNewsList.imgUrl = (String)perItem.get("ZXTD.DATA.item.image-url");
		
		return downloadNewsList;
	}
	
	public void addResult(String key, Object value){
		result.put(key, value);
	}

}
