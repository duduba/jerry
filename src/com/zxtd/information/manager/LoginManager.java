package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;

import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;

public class LoginManager {

	private LoginManager(){
		
	}
	
	private static LoginManager manager=null;
	
	public static LoginManager getInstance(){
		if(null==manager){
			manager=new LoginManager();
		}
		return manager;
	}
	
	
	/**
	 * 登陆
	 * @param userAccount
	 * @param password
	 * @param accountType
	 * @return
	 */
	public UserInfo doLogin(String userAccount,String password,int accountType){
		String result="";
		Map<String,String> map=new HashMap<String,String>();
		map.put("useraccount", userAccount);
		map.put("password",password);
		map.put("account-type", String.valueOf(accountType));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_LOGIN);
		UserInfo info=null;
		try{
			 result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					info=new UserInfo();
					//StringBuffer sb=new StringBuffer();
					String namespace=null;
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if(tagName.equals("user")){
								int status=Integer.valueOf(parser.getAttributeValue(namespace, "login-status"));
								if(status==0){
									break;
								}else{
									info.setUserId(Integer.valueOf(parser.getAttributeValue(namespace, "userid")));
									info.setNickName(parser.getAttributeValue(namespace, "nickname"));
									info.setAccountType(Integer.valueOf(parser.getAttributeValue(namespace, "accounttype")));
									String sign=parser.getAttributeValue(namespace, "aspiration");
									info.setSign(TextUtils.isEmpty(sign) || "null".equals(sign)? "编辑个性签名" : sign);
									String location=parser.getAttributeValue(namespace, "location");
									info.setArea(TextUtils.isEmpty(location) ? "保密":location);
									String work=parser.getAttributeValue(namespace, "profession");
									info.setWork(TextUtils.isEmpty(work) || "null".equals(work) ? "保密":work);
									String header=parser.getAttributeValue(namespace, "portrait");
									info.setHeader(header);
									int temp=Integer.valueOf(parser.getAttributeValue(namespace, "sex"));
									String sex=temp==0?"男":temp==1?"女":"保密";
									info.setSex(sex);
									String birth=parser.getAttributeValue(namespace, "birthday");
									info.setBirth(TextUtils.isEmpty(birth) || "null".equals(birth)? "保密" : birth);
								}
							}
							else if(tagName.equals("fans-quantity")){
								info.setFansCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("fans-isnew")){
								info.setHasNewFans(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("attention-quantity")){
								info.setAttentionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("publish-quantity")){
								info.setPublishCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("publish-isnew")){
								info.setHasNewPublish(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("comment-quantity")){
								info.setCollectionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("comment-isnew")){
								info.setHasNewComment(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("collect-quantity")){
								info.setCollectionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("sms-quantity")){
								info.setLetterCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("sms-isnew")){
								info.setHasNewLetter(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("attention-status")){
								info.setHasAttention(Integer.valueOf(parser.nextText())==1 ? true : false);
							}
							/*if(tagName.equals("login-status")){
								int status=Integer.valueOf(parser.nextText());
								if(status==0)
									break;
							}
							if(tagName.equals("userid")){
								info.setUserId(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("nickname")){
								info.setNickName(parser.nextText());
							}else if(tagName.equals("aspiration")){
								info.setSign(parser.nextText());
							}else if(tagName.equals("location")){
								info.setArea(parser.nextText());
							}else if(tagName.equals("profession")){
								info.setWork(parser.nextText());
							}else if(tagName.equals("portrait")){
								info.setHeader(parser.nextText());
							}else if(tagName.equals("sex")){
								int temp=Integer.valueOf(parser.nextText());
								String sex=temp==0?"男":temp==1?"女":"保密";
								info.setSex(sex);
							}else if(tagName.equals("birthday")){
								info.setBirth(parser.nextText());
							}else if(tagName.equals("img")){
								//sb.append()
							}else if(tagName.equals("fans-quantity")){
								info.setFansCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("fans-isnew")){
								info.setHasNewFans(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("attention-quantity")){
								info.setAttentionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("publish-quantity")){
								info.setPublishCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("publish-isnew")){
								info.setHasNewPublish(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("comment-quantity")){
								info.setCollectionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("comment-isnew")){
								info.setHasNewComment(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("collect-quantity")){
								info.setCollectionCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("sms-quantity")){
								info.setLetterCount(Integer.valueOf(parser.nextText()));
							}else if(tagName.equals("sms-isnew")){
								info.setHasNewLetter(Integer.valueOf(parser.nextText()));
							}*/
						}
						eventType=parser.next();
					}
			 }
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return info;
	}
	
	
	
	/**
	 * 找回密码
	 * @param email
	 * @return
	 */
	public int findPassword(String email){
		Map<String,String> map=new HashMap<String,String>();
		map.put("useraccount", email);
		int resultCode=-1;
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_FIND_PASSWORD);
		try{
			String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){	
							String tagName=parser.getName();
							if(tagName.equals("result-code")){
								resultCode=Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
			 }
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return resultCode;
	}
	
	
	
}
