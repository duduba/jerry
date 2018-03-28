package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;

import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class SearchUserManager {

	private static SearchUserManager manager=null;
	
	private SearchUserManager(){
	}
	
	public static SearchUserManager getInstance(){
		if(null==manager)
			manager=new SearchUserManager();
		return manager;
	}
	
	
	/**
	 * 查询
	 * @param param
	 * @param pageIndex
	 * @return
	 */
	public List<FansFocusBean> searchUsers(String param,int pageIndex){
		List<FansFocusBean> list=null;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(XmppUtils.getUserId()));
		map.put("page-index",String.valueOf(pageIndex));
		map.put("param", param);
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_SEARCH_USER);
		try{
			String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
				    list=new ArrayList<FansFocusBean>(20);
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					String namespace=null;
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if(tagName.equals("result-code")){
								int resultCode=Integer.valueOf(parser.nextText());
								if(resultCode==0)
									break;
							}else if("user".equals(tagName)){
								FansFocusBean bean=new FansFocusBean();
								bean.setUserId(Integer.valueOf(parser.getAttributeValue(namespace, "oppoid")));
								bean.setImg(parser.getAttributeValue(namespace, "image-url"));
								bean.setNickName(parser.getAttributeValue(namespace, "nickname"));
								bean.setArea(parser.getAttributeValue(namespace, "location"));
								bean.setAttentionState(Integer.valueOf(parser.getAttributeValue(namespace, "attention-status")));
								list.add(bean);
							}
						}
						eventType=parser.next();
					}
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return list;
	}
	
}
