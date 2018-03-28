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
import com.zxtd.information.bean.PublicBean;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class PublicManager {

	private PublicManager(){}
	
	private static PublicManager manager;
	
	public static PublicManager getInstance(){
		if(null==manager)
			manager=new PublicManager();
		return manager;
	}
	
	
	private List<PublicBean> getPublicList(String requestCode,Map<String,String> map){
		List<PublicBean> list=null;
		HttpHelper helper=new HttpHelper(requestCode);
		try{
			 String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
				XmlPullParser parser=Xml.newPullParser();
				parser.setInput(new StringReader(result));
				int eventType=parser.getEventType();
				list=new ArrayList<PublicBean>();
				String namespace=null;
				while(eventType!=XmlPullParser.END_DOCUMENT){
					if(eventType==XmlPullParser.START_TAG){
						String tagName=parser.getName();
						if(tagName.equals("result-code")){
							int resultCode=Integer.valueOf(parser.nextText());
							if(resultCode==0)
								break;
						}else if(tagName.equals("user")){
							PublicBean bean=new PublicBean();
							bean.setNewsId(Integer.valueOf(parser.getAttributeValue(namespace, "id")));
							bean.setNewsTitle(parser.getAttributeValue(namespace, "title"));
							bean.setPublicTime(parser.getAttributeValue(namespace, "time"));
							bean.setReplayCount(Integer.valueOf(parser.getAttributeValue(namespace, "quantity")));
							bean.setHasNewReplay(Integer.valueOf(parser.getAttributeValue(namespace, "isnew"))==1 ? true : false);
							bean.setNewsUrl(parser.getAttributeValue(namespace, "http-url"));
							bean.setContent(parser.getAttributeValue(namespace, "content"));
							list.add(bean);
						}
					}
					eventType=parser.next();
				}
			}
		}catch(Exception ex){
			Utils.printException(ex);
			return null;
		}
		return list;
	}
	
	/**
	 * 获取自己发布的新闻
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public List<PublicBean> getPublicInfo(int userId,int pageIndex){
		if(userId==XmppUtils.getUserId()){
			return getLocalPublics(pageIndex);
		}else{
			Map<String,String> map=new HashMap<String,String>();
			map.put("userid", String.valueOf(userId));
			map.put("page-index",String.valueOf(pageIndex));
			return getPublicList(Constant.RequestCode.MINE_GET_PUBLIC, map);
		}
	}
	
	
	/**
	 * 从缓存获取发布数据
	 * @param pageIndex
	 * @return
	 */
	public List<PublicBean> getLocalPublics(int pageIndex){
		List<PublicBean> list=new ArrayList<PublicBean>(20);
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_PUBLIC+" where userId="+XmppUtils.getUserId()
					+" order by hasNewReplay desc,autoId desc limit "+Constant.PAGESIZE+" offset "+pageIndex*Constant.PAGESIZE, null);
			while(cursor.moveToNext()){
				PublicBean bean=new PublicBean();
				bean.setNewsId(cursor.getInt(cursor.getColumnIndex("newsId")));
				bean.setNewsTitle(cursor.getString(cursor.getColumnIndex("newsTitle")));
				bean.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
				bean.setHasNewReplay(cursor.getInt(cursor.getColumnIndex("hasNewReplay"))==1 ? true :false);
				bean.setNewsUrl(cursor.getString(cursor.getColumnIndex("newsUrl")));
				bean.setPublicTime(cursor.getString(cursor.getColumnIndex("publicTime")));
				bean.setReplayCount(cursor.getInt(cursor.getColumnIndex("replayCount")));
				bean.setContent(cursor.getString(cursor.getColumnIndex("content")));
				list.add(bean);
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return list;
	}
	
	
	
	
	/**
	 * 删除新闻
	 * @param newsId
	 * @return
	 */
	public int doDelete(int newsId){
		int resultCode=-1;
		Map<String,String> map=new HashMap<String,String>();
		map.put("id", String.valueOf(newsId));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_DELETE_PUBLIC);
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
	
	
	private ContentValues getContentValues(PublicBean bean){
		ContentValues values=new ContentValues();
		values.put("newsId", bean.getNewsId());
		values.put("newsTitle", bean.getNewsTitle());
		values.put("content", bean.getContent());
		values.put("publicTime", bean.getPublicTime());
		values.put("replayCount", bean.getReplayCount());
		values.put("channel", bean.getChannel());
		values.put("newsUrl", bean.getNewsUrl());
		values.put("hasNewReplay", 0);
		values.put("userId", XmppUtils.getUserId());
		return values;
	}
	
	
	/**
	 * 添加本地发布数据
	 * @return
	 */
	public boolean insertPublic(PublicBean bean){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=getContentValues(bean);
			flag=db.insert(zxtd_DBhelper.TB_PUBLIC, null, values)>0 ? true : false;
			if(flag){
				boolean isSuccess=CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
				CacheManager.getInstance().UpgradeVersion(db, Constant.PUBLIC_VERSION_KEY);
				//更新发布数量
				if(isSuccess){
					db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set publicCount=publicCount+1 where userId="+XmppUtils.getUserId());
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
		return flag;
	}
	
	
	/**
	 * 删除本地缓存数据
	 * @param bean
	 */
	public boolean deleteLocal(PublicBean bean){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			flag=db.delete(zxtd_DBhelper.TB_PUBLIC, "newsId=? and userId=?", new String[]{String.valueOf(bean.getNewsId()),String.valueOf(XmppUtils.getUserId())})>0 ? true : false;
			if(flag){
				boolean isSuccess=CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
				CacheManager.getInstance().UpgradeVersion(db, Constant.PUBLIC_VERSION_KEY);
				//更新发布数量
				if(isSuccess){
					db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set publicCount=publicCount-1 where userId="+XmppUtils.getUserId());
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
		return flag;
	}
	
	
	/**
	 * 清空发布数据
	 * @return
	 */
	public boolean clearPublic(){
		SQLiteDatabase db=null;
		boolean flag=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.delete(zxtd_DBhelper.TB_PUBLIC, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
			return true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return flag;
	}
	
	
	public boolean synchronizedData(){
		boolean isSuccess=false;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(XmppUtils.getUserId()));
		 try{
			 int pageCount=0;
			 HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_SYNCH_GET_PUBLIC_PAGER);
			 String result=helper.doPost(map);
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
							}else if(tagName.equals("totalpage")){
								pageCount=Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
					if(pageCount==0)
						return true;
					for(int i=0;i<pageCount;i++){
						isSuccess=syncDataWithPager(i);
					}
			 }
		 }catch(Exception ex){
			 Utils.printException(ex);
		 }
		return isSuccess;
	}
	
	
	private boolean syncDataWithPager(int pageIndex){
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(XmppUtils.getUserId()));
		map.put("page-index", String.valueOf(pageIndex));
		List<PublicBean> list=getPublicList(Constant.RequestCode.MINE_SYNCH_PUBLIC_DATA, map);
		SQLiteDatabase db=null;
		if(null!=list){
			if(list.isEmpty())
				return true;
			try{
				zxtd_DBhelper dbHelper=new zxtd_DBhelper();
				db=dbHelper.getWritableDatabase();
				Iterator<PublicBean> it=list.iterator();
				int i=0;
				db.beginTransaction();
				while(it.hasNext()){
					PublicBean bean=it.next();
					ContentValues values=getContentValues(bean);
					db.insert(zxtd_DBhelper.TB_PUBLIC, null, values);
					if(i%50==0 && i!=0){
						db.setTransactionSuccessful();
						db.endTransaction();
						db.beginTransaction();
					}
					i++;
				}
				db.setTransactionSuccessful();
				db.endTransaction();
				return true;
			}catch(Exception ex){
				Utils.printException(ex);
				return false;
			}finally{
				if(null!=db){
					db.close();
				}
			}
		 }else{
			return false;
		}
	}
	
	
	
	/**
	 * 修改发布数量
	 * @param count
	 * @return
	 */
	public boolean setUserPublicCount(int count){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("publicCount", count);
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
	
	
	
	/**
	 * 设置发布红点
	 * @param ids
	 * @return
	 */
	public boolean setPublicRedPoint(String ids){
		boolean flag=false;
		if(!TextUtils.isEmpty(ids)){
			SQLiteDatabase db=null;
			zxtd_DBhelper helper=new zxtd_DBhelper();
			try{
				db=helper.getWritableDatabase();
				db.beginTransaction();
				String[] publicIds=ids.split("-");
				for(int i=0;i<publicIds.length;i++){
					db.execSQL("update "+zxtd_DBhelper.TB_PUBLIC+" set hasNewReplay=1,replayCount=replayCount+1 where userId="+String.valueOf(XmppUtils.getUserId())+" and newsId="+publicIds[i]);
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
		}
		return flag;
	}
	
	
	
	/**
	 * 设置为已读
	 * @return
	 */
	public boolean setIsReaded(int newsId){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("hasNewReplay", 0);
			flag=db.update(zxtd_DBhelper.TB_PUBLIC, values, "userId=? and newsId=?", new String[]{String.valueOf(XmppUtils.getUserId()),String.valueOf(newsId)})>0 ? true : false;
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
	 * 获取未读的评论数
	 * @return
	 */
	public int getNoReadCount(){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int count=0;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select count(autoId) as rowCount from "+zxtd_DBhelper.TB_PUBLIC+" where userId="+XmppUtils.getUserId()+" and hasNewReplay=1", null);
			while(cursor.moveToNext()){
				count=cursor.getInt(cursor.getColumnIndex("rowCount"));
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return count;
	}
	
	
	
	/**
	 * 修改发布信息
	 */
	public boolean modifyPublic(PublicBean bean){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("newsTitle", bean.getNewsTitle());
			//values.put("publicTime", bean.getPublicTime());
			values.put("channel", bean.getChannel());
			values.put("content", bean.getContent());
			flag=db.update(zxtd_DBhelper.TB_PUBLIC, values, "userId=? and newsId=?", new String[]{String.valueOf(XmppUtils.getUserId()),String.valueOf(bean.getNewsId())})>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db, Constant.PUBLIC_VERSION_KEY);
			}
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
