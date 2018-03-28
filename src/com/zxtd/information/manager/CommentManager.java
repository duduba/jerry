package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Xml;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class CommentManager {

	private static CommentManager manager;
	
	private CommentManager(){
		
	}
	
	public static CommentManager getInstance(){
		if(null==manager)
			manager=new CommentManager();
		return manager;
	}
	
	
	/**
	 * 获取我的评论
	 * @param typeIndex 0是我的评论，1是评论我的
	 * @return 
	 * @return
	 */
	public List<CommentBean> getComments(int userId,int pageIndex, int typeIndex){
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("page-index",String.valueOf(pageIndex));
		HttpHelper helper=null;
		if(typeIndex == 0){
			helper = new HttpHelper(Constant.RequestCode.MINE_GET_COMMENT);
		}else{
			helper = new HttpHelper(Constant.RequestCode.MINE_GET_REPLY);
		}
		List<CommentBean> list=null;
		try{
			 String result=helper.doPost(map);
			 String isDataOver = "1";
			 if(!TextUtils.isEmpty(result)){
				XmlPullParser parser=Xml.newPullParser();
				parser.setInput(new StringReader(result));
				int eventType=parser.getEventType();
				String namespace=null;
				CommentBean bean=null;
				
				while(eventType!=XmlPullParser.END_DOCUMENT){
					if(eventType==XmlPullParser.START_TAG){
						String tagName=parser.getName();
						if(tagName.equals("result-code")){
							int resultCode=Integer.valueOf(parser.nextText());
							if(resultCode==0){
								break;
							}else{
								list=new ArrayList<CommentBean>();
							}
						}else if(tagName.equals("comment")){
							bean.setCommentId(Integer.valueOf(parser.getAttributeValue(namespace, "commentid")));
							bean.setContent(parser.getAttributeValue(namespace, "content"));
							bean.setUserImg(parser.getAttributeValue(namespace, "image-url"));
							bean.setNickName(parser.getAttributeValue(namespace, "nickname"));
							bean.setPublicTime(parser.getAttributeValue(namespace, "time"));
							bean.setLocation(parser.getAttributeValue(namespace, "location"));
							bean.setUserId(Integer.parseInt(parser.getAttributeValue(namespace, "userid")));
						}else if("item".equals(tagName)){
							bean=new CommentBean();
						}else if("origcomment".equals(tagName)){
							bean.setOrigContent(parser.getAttributeValue(namespace, "content"));
							bean.setOrigCommentId(Integer.parseInt(parser.getAttributeValue(namespace, "commentid")));
						}else if("original".equals(tagName)){
							bean.setNewsId(Integer.parseInt(parser.getAttributeValue(namespace, "infoid")));
							bean.setTitle(parser.getAttributeValue(namespace, "title"));
							bean.setType(parser.getAttributeValue(namespace, "type"));
							bean.setInfoUrl(parser.getAttributeValue(namespace, "http-url"));
						}else if("dataover".equals(tagName)){
							isDataOver = parser.nextText();
						}
					}else if(eventType==XmlPullParser.END_TAG){
						String tagName = parser.getName();
						if("item".equals(tagName) && bean != null){
							list.add(bean);
							bean=null;
						}
					}
					eventType=parser.next();
					
				}
			 }
			 if("1".equals(isDataOver)){
					CommentBean commentBean = new CommentBean();
					commentBean.setCommentId(-1);
					list.add(0, commentBean);
			}
		}catch(Exception ex){
			//Utils.printException(ex);
			ex.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 发布评论
	 * @param bean
	 * @return
	 */
	public boolean doPublicComment(CommentBean bean){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=new ContentValues();
			values.put("newsId", bean.getNewsId());
			values.put("nickName", bean.getNickName());
			values.put("content", bean.getContent());
			values.put("commentDate",TimeUtils.getNow());
			values.put("floorNum", bean.getLocation());
			values.put("newsType", Integer.valueOf(bean.getType()));
			values.put("userId", XmppUtils.getUserId());
			
			flag=db.insert(zxtd_DBhelper.TB_COMMENT, null, values)>0 ? true : false;
			if(flag){
				flag=CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
				CacheManager.getInstance().UpgradeVersion(db, Constant.COMMENT_VERSION_KEY);
				//更新评论数量
				if(flag){
					db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set commentCount=commentCount+1 where userId="+XmppUtils.getUserId());
				}
			}
			db.setTransactionSuccessful();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.endTransaction();
				db.close();
			}
		}
		
		if(flag){
			Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
			intent.putExtra("type", 2);
			intent.putExtra("data", 1);
			ZXTD_Application.getMyContext().sendBroadcast(intent);
		}
		return flag;
	}
	
	
	
	
}
