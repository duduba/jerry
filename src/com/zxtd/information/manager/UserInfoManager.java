package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class UserInfoManager {

	private static UserInfoManager manager=null;
	
	private UserInfoManager(){
	}
	
	public static UserInfoManager getInstance(){
		if(null==manager)
			manager=new UserInfoManager();
		return manager;
	}
	
	public UserInfo getUserInfo(int userId,int loginId,boolean isSelf){
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		String action=Constant.RequestCode.MINE_GOTO_SELFHOMEPAGE;
		if(!isSelf){
			action=Constant.RequestCode.MINE_GOTO_OTHERHOMEPAGE;
			map.put("loginuserid", String.valueOf(loginId));
		}
		HttpHelper helper=new HttpHelper(action);
		UserInfo info=null;
		try{
			 String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					info=new UserInfo();
					String namespace=null;
					List<NewImageBean> imgList=new ArrayList<NewImageBean>();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if(tagName.equals("user")){
								int status=Integer.valueOf(parser.getAttributeValue(namespace, "login-status"));
								if(status==0){
									break;
								}else{
									String account=parser.getAttributeValue(namespace, "user-account");
									info.setUserAccount(Utils.isEmpty(account) || "null".equals(account) ? "" : account);
									String password=parser.getAttributeValue(namespace, "password");
									info.setPassword(Utils.isEmpty(password) || "null".equals(password) ? "" : password);
									info.setUserId(Integer.valueOf(parser.getAttributeValue(namespace, "userid")));
									info.setNickName(parser.getAttributeValue(namespace, "nickname"));
									info.setAccountType(Integer.valueOf(parser.getAttributeValue(namespace, "accounttype")));
									String sign=parser.getAttributeValue(namespace, "aspiration");
									info.setSign(TextUtils.isEmpty(sign) || "null".equals(sign)? "编辑个性签名" : sign);
									String location=parser.getAttributeValue(namespace, "location");
									info.setArea(TextUtils.isEmpty(location) || "null".equals(location) ? "保密":location);
									String work=parser.getAttributeValue(namespace, "profession");
									info.setWork(TextUtils.isEmpty(work) || "null".equals(work) ? "保密":work);
									String header=parser.getAttributeValue(namespace, "portrait");
									info.setHeader(header);
									info.setSex(parser.getAttributeValue(namespace, "sex"));
									String birth=parser.getAttributeValue(namespace, "birthday");
									info.setBirth(birth);
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
								info.setCommentCount(Integer.valueOf(parser.nextText()));
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
							}else if(tagName.equals("img")){
								NewImageBean bean=new NewImageBean();
								bean.setImgId(Integer.valueOf(parser.getAttributeValue(namespace, "id")));
								bean.setSmallUrl(parser.getAttributeValue(namespace, "small"));
								bean.setImageUrl(parser.nextText());
								bean.setDescribe("");
								imgList.add(bean);
							}
						}
						eventType=parser.next();
					}
					info.setImgList(imgList);
			 }
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return info;
	}
	
	
	
	/**
	 * 获取本地数据
	 * @return
	 */
	public UserInfo getLocalUserInfo(){
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		UserInfo userInfo=null;
		try{
			Log.e(Constant.TAG, "userId:"+XmppUtils.getUserId());
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_MINE+" where userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
			while(cursor.moveToNext()){
				userInfo=new UserInfo();
				userInfo.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
				userInfo.setUserAccount(cursor.getString(cursor.getColumnIndex("userAccount")));
				userInfo.setPassword(cursor.getString(cursor.getColumnIndex("userPassword")));
				userInfo.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
				userInfo.setHeader(cursor.getString(cursor.getColumnIndex("header")));
				userInfo.setSign(cursor.getString(cursor.getColumnIndex("signText")));
				userInfo.setArea(cursor.getString(cursor.getColumnIndex("location")));
				userInfo.setWork(cursor.getString(cursor.getColumnIndex("job")));
				userInfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
				userInfo.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
				userInfo.setFansCount(cursor.getInt(cursor.getColumnIndex("fansCount")));
				userInfo.setHasNewFans(cursor.getInt(cursor.getColumnIndex("fansHasNew")));
				userInfo.setAttentionCount(cursor.getInt(cursor.getColumnIndex("focusCount")));
				userInfo.setPublishCount(cursor.getInt(cursor.getColumnIndex("publicCount")));
				userInfo.setHasNewPublish(cursor.getInt(cursor.getColumnIndex("publicHasNew")));
				userInfo.setCommentCount(cursor.getInt(cursor.getColumnIndex("commentCount")));
				userInfo.setHasNewComment(cursor.getInt(cursor.getColumnIndex("hasNewComment")));
				userInfo.setCollectionCount(cursor.getInt(cursor.getColumnIndex("collectionCount")));
				userInfo.setLetterCount(cursor.getInt(cursor.getColumnIndex("letterCount")));
				userInfo.setAccountType(cursor.getInt(cursor.getColumnIndex("accountType")));
				break;
			}
			cursor.close();
			if(null!=userInfo){
				cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_IMAGES+" where userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
				List<NewImageBean> imgList=new ArrayList<NewImageBean>();
				while(cursor.moveToNext()){
					NewImageBean bean=new NewImageBean();
					bean.setImgId(cursor.getInt(cursor.getColumnIndex("imageId")));
					bean.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
					bean.setSmallUrl(cursor.getString(cursor.getColumnIndex("smallUrl")));
					//Log.e(Constant.TAG, "大图:"+bean.getImageUrl());
					//Log.e(Constant.TAG, "小图:"+bean.getSmallUrl());
					bean.setDescribe("");
					imgList.add(bean);
				}
				userInfo.setImgList(imgList);
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return userInfo;
	}
	
	
	
	/**
	 * 添加用户
	 * @param info
	 * @return
	 */
	public boolean addOrModifyUserInfo(UserInfo info){
		boolean isExist=checkExist();
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=getContentValues(info);
			if(isExist){
				db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
				db.delete(zxtd_DBhelper.TB_IMAGES, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
			}else{
				values.put("userId", info.getUserId());
				values.put("letterCount", info.getLetterCount());
				db.insert(zxtd_DBhelper.TB_MINE, null, values);
			}
			CacheManager.getInstance().modifyVersion(db, Constant.USER_VERSION_KEY, CacheManager.CACHEVERSION.get(Constant.USER_VERSION_KEY));
			Iterator<NewImageBean> it=info.getImgList().iterator();
			while(it.hasNext()){
				NewImageBean bean=it.next();
				ContentValues imgsValues=new ContentValues();
				imgsValues.put("imageId", bean.getImgId());
				imgsValues.put("imageUrl", bean.getImageUrl());
				imgsValues.put("smallUrl", bean.getSmallUrl());
				imgsValues.put("imageDesc", bean.getDescribe());
				imgsValues.put("userId", XmppUtils.getUserId());
				db.insert(zxtd_DBhelper.TB_IMAGES, null, imgsValues);
			}
			db.setTransactionSuccessful();
			flag=true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.endTransaction();
				db.close();
			}
		}
		return flag;
	}
	
	
	/**
	 * 修改用户
	 * @return
	 */
	public boolean modifyUser(UserInfo info){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=getContentValues(info);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db,Constant.USER_VERSION_KEY);
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
		return flag;
	}
	
	
	private boolean checkExist(){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_MINE+" where userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
			cursor.moveToFirst();
			flag=cursor.getCount()>0 ? true : false;
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	
	private ContentValues getContentValues(UserInfo info){
		ContentValues values=new ContentValues();
		values.put("userAccount", info.getUserAccount());
		values.put("userPassword", info.getPassword());
		values.put("nickName", info.getNickName());
		values.put("header", info.getHeader());
		values.put("signText", info.getSign());
		values.put("location", info.getArea());
		values.put("job", info.getWork());
		values.put("sex", info.getSex());
		values.put("birth", info.getBirth());
		values.put("fansCount", info.getFansCount());
		values.put("fansHasNew", info.isHasNewFans());
		values.put("focusCount", info.getAttentionCount());
		values.put("publicCount", info.getPublishCount());
		values.put("publicHasNew", info.getHasNewPublish());
		values.put("commentCount", info.getCommentCount());
		values.put("hasNewComment", info.isHasNewComment());
		values.put("collectionCount", info.getCollectionCount());
		values.put("accountType", info.getAccountType());
		return values;
	}
	
	
	/**
	 * 删除图片
	 * @param picId
	 * @return
	 */
	public int deletAlbum(int id,int type){
		int resultCode=-1;
		Map<String,String> map=new HashMap<String,String>();
		map.put("id", String.valueOf(id));
		map.put("type", String.valueOf(type));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_DELETE_ALBUMS_HEAD);
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
	
	
	
	
	/**
	 * 设置头像
	 * @param userId
	 * @param picId
	 * @return
	 */
	public String setHead(int userId,int picId){
		int resultCode=-1;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("image-id", String.valueOf(picId));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_MODIFY_HEAD);
		String headUrl="";
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
							if(resultCode!=1){
								break;
							}
						}else if(tagName.equals("image-url")){
							headUrl=parser.nextText();
						}
					}
					eventType=parser.next();
				}
				
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
	    return headUrl;
	}
	
	
	
	public boolean setHead(String headUrl){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=new ContentValues();
			values.put("header", headUrl);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db,Constant.USER_VERSION_KEY);
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
		return flag;
	}
	
	
	
	public boolean setNewFans(int fansCount,int isAdd){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=new ContentValues();
			values.put("fansCount", fansCount);
			values.put("fansHasNew", isAdd);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db,Constant.USER_VERSION_KEY);
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
		return flag;
	}
	
	
	/**
	 * 设置消息数量
	 */
	public boolean setIMMessageCount(int count){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("letterCount", count);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	public boolean setRedPoint(String column){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put(column, 1);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	public boolean removeUserRedPoint(String column){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put(column, 0);
			flag=db.update(zxtd_DBhelper.TB_MINE, values, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	
	
	
	public int getFansCount(){
		int fansCount=-1;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select fansCount from "+zxtd_DBhelper.TB_MINE+" where userId="+XmppUtils.getUserId(), null);
			cursor.moveToFirst();
			fansCount=cursor.getInt(cursor.getColumnIndex("fansCount"));
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return fansCount;
	}
	
	
	/**
	 * 获取未读消息
	 * @return
	 */
	public int getNoReadCount(){
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		int count=0;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select fansHasNew,publicHasNew,hasNewComment,letterCount from "+zxtd_DBhelper.TB_MINE+" where userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
			while(cursor.moveToNext()){
				count+=cursor.getInt(cursor.getColumnIndex("fansHasNew"));
				count+=cursor.getInt(cursor.getColumnIndex("publicHasNew"));
				count+=cursor.getInt(cursor.getColumnIndex("hasNewComment"));
				count+=cursor.getInt(cursor.getColumnIndex("letterCount"));
				break;
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return count;
	}
	
	
	
	/**
	 * 将本地关注+1
	 * @return
	 */
	public boolean addLocalFocus(){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set focusCount=focusCount+1 where userId="+XmppUtils.getUserId());
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
	 * 将本地关注-1
	 * @return
	 */
	public boolean removeLocalFocus(){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set focusCount=focusCount-1 where userId="+XmppUtils.getUserId());
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
	 * 未读私信+1
	 */
	public boolean upLetterCount(){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set letterCount=letterCount+1 where userId="+XmppUtils.getUserId());
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
	
	
}


	



