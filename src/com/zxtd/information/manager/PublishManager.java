package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;

import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;

public class PublishManager {
	public static PublishBean getPublishBean(String pubId){
		PublishBean publishBean = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", pubId);
		HttpHelper helper = new HttpHelper(Constant.RequestCode.MINE_GET_PUBLISH_INFO);
		try {
			String result = helper.doPost(map);
			if(!TextUtils.isEmpty(result)){
				XmlPullParser parser=Xml.newPullParser();
				parser.setInput(new StringReader(result));
				int eventType=parser.getEventType();
				while(eventType!=XmlPullParser.END_DOCUMENT){
					if(eventType==XmlPullParser.START_TAG){	
						String tagName=parser.getName();
						if(tagName.equals("result-code")){
							int resultCode=Integer.valueOf(parser.nextText());
							if(resultCode==0)
								break;
						}else if("mypublish".equals(tagName) ){
							publishBean = new PublishBean();
							publishBean.newsId = parser.getAttributeValue(null, "id");
							publishBean.content = parser.getAttributeValue(null, "content");
							NewTypeBean typeBean = new NewTypeBean();
							typeBean.id = parser.getAttributeValue(null, "type");
							publishBean.type = typeBean;
							publishBean.imageUrls = parser.getAttributeValue(null, "image-url");
							publishBean.infoUrl = parser.getAttributeValue(null, "http-url");
							publishBean.title = parser.getAttributeValue(null, "title");
						}
					}
					eventType=parser.next();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publishBean;
	}
	
}
