package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.InvitationCheckedBean;
import com.zxtd.information.bean.InvitationReplyBean;
import com.zxtd.information.util.Utils;

public class InvitationCheckedParseData extends ParseData {
	public static String IS_DATA_OVER = "isDataOver";
	public static String POST_COUNTS_KEY = "postCounts";
	public final static String PAGE_COUNT_KEY = "pageCount";
	public static String NEW_COMMENT_LIST_KEY = "newCommentListData";
	public static String HOT_COMMENT_LIST_KEY = "hotCommentListData";
	
	private Result result;
	public InvitationCheckedParseData(){
		result = new Result();
	}
	
	@Override
	public Result parseMap(Map<String, Object> params) {
		List<Bean> newCommentListData = new ArrayList<Bean>();
		List<Bean> hotCommentListData = new ArrayList<Bean>();
		
		String dataState = (String)params.get("ZXTD.dataover");
		String postCounts = (String)params.get("ZXTD.postcount");
		String pageCount = (String)params.get("ZXTD.pagecount"); 
		
		result.put(POST_COUNTS_KEY, postCounts);
		result.put(PAGE_COUNT_KEY, pageCount);
		result.put(NEW_COMMENT_LIST_KEY, newCommentListData);
		result.put(HOT_COMMENT_LIST_KEY, hotCommentListData);
		
		if(dataState != null && dataState.equals("1")) {
			result.put(IS_DATA_OVER, true);
		} else {
			result.put(IS_DATA_OVER, false);
		}
		parseCommonData(params, hotCommentListData, "HOTDATA."); // 热门评论
		parseCommonData(params, newCommentListData, ""); //最新评论
		return result;
	}
	
	private void parseCommonData(Map<String, Object> params, List<Bean> bean, String type){
		String dataPath = "ZXTD."+type+"DATA.item";
		String subPath = "ZXTD."+type+"SUBDATA.listitem";
		
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get(dataPath);
		List<Map<String, Object>> subData = null;
		Object object = params.get(subPath);
		if(object instanceof List<?>){
			subData = (List<Map<String, Object>>)params.get(subPath);
		}
		
		if(data != null) {
			for(int i = 0; i < data.size();i++) {
				Map<String, Object> perItem = data.get(i);
				InvitationCheckedBean invitationCheckedBean = getInvitationCheckedBean(perItem, type);
				if(subData != null){
					Map<String, Object> perSubdataItem = subData.get(i);
					invitationCheckedBean.invitationReplyBeans = getInvitationReplyBeans(perSubdataItem, type);
				}else{
					invitationCheckedBean.invitationReplyBeans = null;
				}
				bean.add(invitationCheckedBean);
			}
		}else {
			String id = (String)params.get("ZXTD."+type+"DATA.item.id");
			if(Utils.isEmpty(id)){
				return;
			}
			InvitationCheckedBean invitationCheckedBean = getInvitationCheckedBean(params, type);
			invitationCheckedBean.invitationReplyBeans = getInvitationReplyBeans(params, type);
			bean.add(invitationCheckedBean);
		}
	}
	
	private InvitationReplyBean[] getInvitationReplyBeans(Map<String, Object> perSubdataItem, String type){
		List<Map<String, Object>> item = (List<Map<String, Object>>)perSubdataItem.get("ZXTD."+type+"SUBDATA.listitem.item");
		if(item != null) {
			InvitationReplyBean[] arrayReplyBean = new InvitationReplyBean[item.size()];
			for(int j = 0; j < item.size(); j++) {
				Map<String, Object> eachItem = item.get(j);
				arrayReplyBean[j] = getInvitationReplyBean(eachItem, type);
			}
			return arrayReplyBean;
		} else {
			String id = (String)perSubdataItem.get("ZXTD."+type+"SUBDATA.listitem.item.id");
			if(Utils.isEmpty(id)){
				return null;
			}
			return new InvitationReplyBean[]{getInvitationReplyBean(perSubdataItem, type)};
		}
	}
	
	private InvitationReplyBean getInvitationReplyBean(Map<String, Object> data, String type){
		InvitationReplyBean invitationReplyBean = new InvitationReplyBean();
		invitationReplyBean.invitationId = (String)data.get("ZXTD."+type+"SUBDATA.listitem.item.id");
		invitationReplyBean.time = (String)data.get("ZXTD."+type+"SUBDATA.listitem.item.time");
		invitationReplyBean.nickname = Utils.reverseTransformContent((String)data.get("ZXTD."+type+"SUBDATA.listitem.item.nickname"));
		invitationReplyBean.content = Utils.reverseTransformContent((String)data.get("ZXTD."+type+"SUBDATA.listitem.item.content"));
		invitationReplyBean.address = (String)data.get("ZXTD."+type+"SUBDATA.listitem.item.address");
		invitationReplyBean.imageUrl = (String)data.get("ZXTD."+type+"SUBDATA.listitem.item.image-url");
		invitationReplyBean.userId = (String)data.get("ZXTD."+type+"SUBDATA.listitem.item.userid");
		return invitationReplyBean;
	}
	
	private InvitationCheckedBean getInvitationCheckedBean(Map<String, Object> data, String type) {
		InvitationCheckedBean invitationListData = new InvitationCheckedBean();
		invitationListData.invitationId = (String)data.get("ZXTD."+type+"DATA.item.id");
		invitationListData.time = (String)data.get("ZXTD."+type+"DATA.item.time");
		invitationListData.number = (String)data.get("ZXTD."+type+"DATA.item.number");
		invitationListData.nickname = Utils.reverseTransformContent((String)data.get("ZXTD."+type+"DATA.item.nickname"));
		invitationListData.content = Utils.reverseTransformContent((String)data.get("ZXTD."+type+"DATA.item.content"));
		invitationListData.address = (String)data.get("ZXTD."+type+"DATA.item.address");
		invitationListData.imageUrl = (String)data.get("ZXTD." + type + "DATA.item.image-url");
		invitationListData.userId = (String)data.get("ZXTD." + type + "DATA.item.userid");
		return invitationListData;
	}
	
	public void addResult(String key, Object value){
		result.put(key, value);
	}

}
