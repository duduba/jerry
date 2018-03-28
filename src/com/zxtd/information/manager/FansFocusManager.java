package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Xml;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.FocusComparator;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class FansFocusManager {

	/**
	 * 我的粉丝
	 * @param requestCode
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public List<FansFocusBean> getFans(String requestCode,int userId,int pageIndex){
		List<FansFocusBean> list=null;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("page-index",String.valueOf(pageIndex));
		HttpHelper helper=new HttpHelper(requestCode);
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
								bean.setSign(parser.getAttributeValue(namespace, "aspiration"));
								bean.setAttentionState(Integer.valueOf(parser.getAttributeValue(namespace, "attention-status")));
								String account=parser.getAttributeValue(namespace, "user-account");
								bean.setAccount(Utils.isEmpty(account) || "null".equals(account) ? "":account);
								bean.setNickName(parser.nextText());
								list.add(bean);
							}else if("fans-quantity".equals(tagName)){
								FansFocusBean.fansCount=Integer.valueOf(parser.nextText());
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
	
	
	
	/**
	 * 我的关注
	 * @param requestCode
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public List<FansFocusBean> getFocus(String requestCode,int userId,int pageIndex){
		List<FansFocusBean> list=null;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("page-index",String.valueOf(pageIndex));
		HttpHelper helper=new HttpHelper(requestCode);
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
								bean.setSign(parser.getAttributeValue(namespace, "aspiration"));
								bean.setAttentionState(Integer.valueOf(parser.getAttributeValue(namespace, "eachother")));
								String account=parser.getAttributeValue(namespace, "user-account");
								bean.setNickName(parser.nextText());
								bean.setAccount(Utils.isEmpty(account) || "null".equals(account) ? "":account);
								list.add(bean);
							}else if("attention-quantity".equals(tagName)){
								FansFocusBean.focusCount=Integer.valueOf(parser.nextText());
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
	
	
	
	/**
	 * 推荐
	 * @param requestCode
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public List<FansFocusBean> getRecommand(String requestCode,int userId,int pageIndex){
		List<FansFocusBean> list=null;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("page-index",String.valueOf(pageIndex));
		HttpHelper helper=new HttpHelper(requestCode);
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
								bean.setSign(parser.getAttributeValue(namespace, "aspiration"));
								bean.setAttentionState(Integer.valueOf(parser.getAttributeValue(namespace, "eachother")));
								String account=parser.getAttributeValue(namespace, "user-account");
								bean.setAccount(Utils.isEmpty(account) || "null".equals(account) ? "":account);
								bean.setNickName(parser.nextText());
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
	
	
	/**
	 * 添加关注
	 * @param ueseId
	 * @param focusId
	 * @param focusName
	 * @return
	 */
	public int addFocus(int ueseId,int focusId,String focusName){
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(ueseId));
		map.put("attention-uid",String.valueOf(focusId));
		map.put("nickname", focusName);
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_ADD_FOCUS);
		int status=-2;
		try{
			 String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
				 XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if("result-code".equals(tagName)){
								Integer state=Integer.valueOf(parser.nextText());
								if(state==0){
									status=-1;
									break;
								}else{
									XmppUtils.addFriend(String.valueOf(focusId),focusName);
								}
							}else if("eachother".equals(tagName)){
								status=Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
			 }
			 //将本地关注数+1
			 if(status==0 || status==1){
				 UserInfoManager.getInstance().addLocalFocus();
			 }
			 
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return status;
	}
	
	
	/**
	 * 取消关注
	 * @param ueseId
	 * @param unFocusId
	 * <type>0标识粉丝列表，1标识关注或推荐列表</type> 
	 * @return
	 */
	public int cancelFocus(int ueseId,int unFocusId,int type){
		if(type==2)
			type=1;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(ueseId));
		map.put("attention-uid",String.valueOf(unFocusId));
		map.put("type", String.valueOf(type));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_CANCEL_FOCUS);
		int status=-2;
		try{
			 String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
				 XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if("result-code".equals(tagName)){
								Integer state=Integer.valueOf(parser.nextText());
								if(state==0){
									status=-1;
								}else{
									status=1;
									XmppUtils.removeFriend(String.valueOf(unFocusId));
								}	
								break;
							}
							/*else if("eachother".equals(tagName)){
								status=Integer.valueOf(parser.nextText());
							}*/
						}
						eventType=parser.next();
					}
					if(status==1){
						cancelLocalFocus(unFocusId,ueseId);
					}
			 }
			 //将本地关注数+1
			 if(status==0 || status==1){
				 UserInfoManager.getInstance().removeLocalFocus();
			 }
			 
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return status;
	}
	
	
	/**
	 * 添加到本地关注
	 */
	public boolean addLocalFocus(FansFocusBean bean,int userId){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("focusId", bean.getUserId());
			values.put("focusName", bean.getNickName());
			values.put("focusSign", bean.getSign());
			values.put("focusImg", bean.getImg());
			values.put("focusStatus", bean.getAttentionState());//0未关注  1已关注 2相互关注
			values.put("userId", userId);
			long result=db.insert(zxtd_DBhelper.TB_FOCUS, null, values);
			if(result>0)
				flag=true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	/**
	 * 取消本地关注
	 */
	public boolean cancelLocalFocus(int unFocusId,int userId){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			int result=db.delete(zxtd_DBhelper.TB_FOCUS, "focusId=? and userId=?", new String[]{String.valueOf(unFocusId),String.valueOf(userId)});
			if(result>0)
				flag=true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	/**
	 * 查找关注好友
	 * @return
	 */
	public List<FansFocusBean> searchFocus(String nickName,int userId){
		List<FansFocusBean> list=new ArrayList<FansFocusBean>();
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getReadableDatabase();
			String[] columns=new String[]{"focusId","focusName","focusSign","focusImg","focusStatus"};
			Cursor cursor=db.query(zxtd_DBhelper.TB_FOCUS, columns, "userId=? and focusName like ?", new String[]{String.valueOf(userId),"%"+nickName+"%"}, null, null, " focusName asc");
			while(cursor.moveToNext()){
				FansFocusBean bean=new FansFocusBean();
				bean.setUserId(cursor.getInt(cursor.getColumnIndex("focusId")));
				String name=cursor.getString(cursor.getColumnIndex("focusName"));
				bean.setNickName(name);
				bean.setSign(cursor.getString(cursor.getColumnIndex("focusSign")));
				bean.setImg(cursor.getString(cursor.getColumnIndex("focusImg")));
				bean.setAttentionState(cursor.getInt(cursor.getColumnIndex("focusStatus")));
				//bean.setSortKey(Utils.getPinYin(name));
				list.add(bean);
			}
			cursor.close();
			//Collections.sort(list, new FocusComparator());
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return list;
	}
	
	
	/**
	 * 获取
	 * @param id
	 * @return
	 */
	public FansFocusBean getFocusById(String id,int selfId){
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		FansFocusBean bean=null;
		try{
			db=helper.getReadableDatabase();
			String[] columns=new String[]{"focusId","focusName","focusSign","focusImg","focusStatus"};
			Cursor cursor=db.query(zxtd_DBhelper.TB_FOCUS, columns, "userId=? and focusId=?", new String[]{String.valueOf(selfId),id}, null, null, null);
			if(cursor.moveToNext()){
				bean=new FansFocusBean();
				bean.setUserId(cursor.getInt(cursor.getColumnIndex("focusId")));
				String name=cursor.getString(cursor.getColumnIndex("focusName"));
				bean.setNickName(name);
				bean.setSign(cursor.getString(cursor.getColumnIndex("focusSign")));
				bean.setImg(cursor.getString(cursor.getColumnIndex("focusImg")));
				bean.setAttentionState(cursor.getInt(cursor.getColumnIndex("focusStatus")));
				bean.setSortKey(Utils.getPinYin(name));	
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return bean;
	}
	

	/**
	 * 同步关注数据
	 * @return
	 */
	public boolean syncFocusData(){
		List<FansFocusBean> list=getAllFocus();
		return syncLocal(list);
	}
	
	
	public boolean syncLocal(List<FansFocusBean> list){
		boolean isSuccess=false;
		if(null!=list && !list.isEmpty()){
			zxtd_DBhelper dbhelper=new zxtd_DBhelper();
			SQLiteDatabase db=null;
			try{
				db=dbhelper.getWritableDatabase();
				Iterator<FansFocusBean> it=list.iterator();
				db.beginTransaction();
				db.delete(zxtd_DBhelper.TB_FOCUS, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
				while(it.hasNext()){
					FansFocusBean bean=it.next();
					ContentValues values=new ContentValues();
					values.put("focusId", bean.getUserId());
					values.put("focusName", bean.getNickName());
					values.put("focusSign", bean.getSign());
					values.put("focusImg", bean.getImg());
					values.put("focusStatus", bean.getAttentionState());//0未关注  1已关注 2相互关注
					values.put("userId", XmppUtils.getUserId());
					db.insert(zxtd_DBhelper.TB_FOCUS, null, values);
				}
				db.setTransactionSuccessful();
				isSuccess=true;
			}catch(Exception ex){
				Utils.printException(ex);
			}finally{
				if(null!=db){
					db.endTransaction();
					db.close();
				}
			}
		}
		return isSuccess;
	}
	
	
	/**
	 * 获取所有的关注用户
	 * @return
	 */
	public List<FansFocusBean> getAllFocus(){
		int userId=XmppUtils.getUserId();
		List<FansFocusBean> list=null;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_GET_ALL_FOCUS);
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
								bean.setSign(parser.getAttributeValue(namespace, "aspiration"));
								bean.setAttentionState(Integer.valueOf(parser.getAttributeValue(namespace, "eachother")));
								String account=parser.getAttributeValue(namespace, "user-account");
								bean.setAccount(Utils.isEmpty(account) || "null".equals(account) ? "":account);
								String name=parser.nextText();
								bean.setNickName(name);
								bean.setSortKey(Utils.getPinYin(name));
								list.add(bean);
							}else if("attention-quantity".equals(tagName)){
								FansFocusBean.focusCount=Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
					Collections.sort(list, new FocusComparator());
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return list;
	}
	
	
	
	
}
