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
import com.zxtd.information.bean.NewsInfo;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class MyCollectionManager {

	 private static MyCollectionManager manager;
	
	 private MyCollectionManager(){
	 }
	
	 
	 public static MyCollectionManager getInstance(){
		 if(null==manager){
			 manager=new MyCollectionManager();
		 }
		 return manager;
	 }
	 
	 
	 
	 private List<NewsInfo> getCollections(String requestCode,Map<String,String> map){
		 HttpHelper helper=new HttpHelper(requestCode);
		 List<NewsInfo> list=null;
		 try{
			 String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
				XmlPullParser parser=Xml.newPullParser();
				parser.setInput(new StringReader(result));
				int eventType=parser.getEventType();
				list=new ArrayList<NewsInfo>(20);
				String namespace=null;
				while(eventType!=XmlPullParser.END_DOCUMENT){
					if(eventType==XmlPullParser.START_TAG){	
						String tagName=parser.getName();
						if(tagName.equals("result-code")){
							int resultCode=Integer.valueOf(parser.nextText());
							if(resultCode==0)
								break;
						}else if(tagName.equals("news")){
							NewsInfo bean=new NewsInfo();
							bean.setNewsId(Integer.valueOf(parser.getAttributeValue(namespace, "id")));
							bean.setNewsTitle(parser.getAttributeValue(namespace, "title"));
							bean.setNewSummary(parser.getAttributeValue(namespace, "content"));
							bean.setNewReplyCount(Integer.valueOf(parser.getAttributeValue(namespace,"quantity")));
							bean.setNewsType(Integer.valueOf(parser.getAttributeValue(namespace, "type")));
							bean.setNewsUrl(parser.getAttributeValue(namespace, "http-url"));
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
	  * 获取收藏的新闻
	  * @param userId
	  * @param pageIndex
	  * @return
	  */
	 public List<NewsInfo> getCollection(int userId,int pageIndex){
		if(userId==XmppUtils.getUserId()){
				return getLocalCollections(pageIndex);
		}else{
			Map<String,String> map=new HashMap<String,String>();
			map.put("userid", String.valueOf(userId));
			map.put("page-index",String.valueOf(pageIndex));
			return getCollections(Constant.RequestCode.MINE_GET_COLLECTION, map);
		}
	 }
	 
	 
	 /**
	  * 删除收藏
	  * 
	  */
	 public int deleteColl(int userId,int newsId,int type){
		int resultCode=-1;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userId));
		map.put("infoid",String.valueOf(newsId));
		map.put("type", String.valueOf(type));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_DELETE_COLLECTION);
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
		 * 删除本地缓存数据
		 * @param bean
		 */
		public boolean deleteLocalCollection(int newsId,int type){
			SQLiteDatabase db=null;
			boolean flag=false;
			zxtd_DBhelper helper=new zxtd_DBhelper();
			try{
				db=helper.getWritableDatabase();
				db.beginTransaction();
				flag=db.delete(zxtd_DBhelper.TB_COLLECTION, "newsId=? and userId=? and newsType=? ", new String[]{String.valueOf(newsId),String.valueOf(XmppUtils.getUserId()),String.valueOf(type)})>0 ? true : false;
				if(flag){
					boolean isSuccess=CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
					CacheManager.getInstance().UpgradeVersion(db, Constant.COLLECTION_VERSION_KEY);
					//更新发布数量
					if(isSuccess){
						db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set collectionCount=collectionCount-1 where userId="+XmppUtils.getUserId());
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
		public boolean clearCollection(){
			SQLiteDatabase db=null;
			boolean flag=false;
			zxtd_DBhelper helper=new zxtd_DBhelper();
			try{
				db=helper.getWritableDatabase();
				db.delete(zxtd_DBhelper.TB_COLLECTION, "userId=?", new String[]{String.valueOf(XmppUtils.getUserId())});
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
		
		
		/**
		 * 同步数据
		 * @return
		 */
		public boolean synchronizedData(){
			boolean isSuccess=false;
			Map<String,String> map=new HashMap<String,String>();
			map.put("userid", String.valueOf(XmppUtils.getUserId()));
			 try{
				 int pageCount=0;
				 HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_SYNCH_GET_COLLECTION_PAGE);
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
			List<NewsInfo> list=getCollections(Constant.RequestCode.MINE_SYNCH_COLLECTION_DATA, map);
			SQLiteDatabase db=null;
			if(null!=list){
				if(list.isEmpty())
					return true;
				try{
					zxtd_DBhelper dbHelper=new zxtd_DBhelper();
					db=dbHelper.getWritableDatabase();
					Iterator<NewsInfo> it=list.iterator();
					int i=0;
					db.beginTransaction();
					while(it.hasNext()){
						NewsInfo bean=it.next();
						ContentValues values=getContentValues(bean);
						db.insert(zxtd_DBhelper.TB_COLLECTION, null, values);
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
		
		
		private ContentValues getContentValues(NewsInfo bean){
			ContentValues values=new ContentValues();
			values.put("newsId", bean.getNewsId());
			values.put("newsTitle", bean.getNewsTitle());
			values.put("newSummary", bean.getNewSummary());
			values.put("replayCount", bean.getNewReplyCount());
			values.put("newsType", bean.getNewsType());
			values.put("newsUrl", bean.getNewsUrl());
			values.put("userId", XmppUtils.getUserId());
			return values;
		}
		
		
		/**
		 * 获取缓存数据
		 * @param pageIndex
		 * @return
		 */
		private List<NewsInfo> getLocalCollections(int pageIndex){
			List<NewsInfo> list=new ArrayList<NewsInfo>(20);
			zxtd_DBhelper helper=new zxtd_DBhelper();
			SQLiteDatabase db=null;
			try{
				db=helper.getReadableDatabase();
				Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_COLLECTION+" where userId="+XmppUtils.getUserId()
						+" order by autoId desc limit "+Constant.PAGESIZE+" offset "+pageIndex*Constant.PAGESIZE, null);
				while(cursor.moveToNext()){
					NewsInfo bean=new NewsInfo();
					bean.setNewsId(cursor.getInt(cursor.getColumnIndex("newsId")));
					bean.setNewsTitle(cursor.getString(cursor.getColumnIndex("newsTitle")));
					bean.setNewSummary(cursor.getString(cursor.getColumnIndex("newSummary")).replaceAll("\n", ""));
					bean.setNewReplyCount(cursor.getInt(cursor.getColumnIndex("replayCount")));
					bean.setNewsType(cursor.getInt(cursor.getColumnIndex("newsType")));
					bean.setNewsUrl(cursor.getString(cursor.getColumnIndex("newsUrl")));
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
		 * 添加本地发布数据
		 * @return
		 */
		public boolean addLocalCollection(NewsInfo bean){
			SQLiteDatabase db=null;
			boolean flag=false;
			zxtd_DBhelper helper=new zxtd_DBhelper();
			try{
				db=helper.getWritableDatabase();
				db.beginTransaction();
				ContentValues values=getContentValues(bean);
				flag=db.insert(zxtd_DBhelper.TB_COLLECTION, null, values)>0 ? true : false;
				if(flag){
					boolean isSuccess=CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
					CacheManager.getInstance().UpgradeVersion(db, Constant.COLLECTION_VERSION_KEY);
					//更新发布数量
					if(isSuccess){
						db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set collectionCount=collectionCount+1 where userId="+XmppUtils.getUserId());
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
		 * 添加收藏
		 * @param newsId
		 * @param type
		 * @return
		 */
		public int addServerCollection(String newsId,int type){
			Map<String,String> map=new HashMap<String,String>();
			map.put("userid", String.valueOf(XmppUtils.getUserId()));
			map.put("id",newsId);
			map.put("type", String.valueOf(type));
		    HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_ADD_COLLECTION);
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
								return Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
				 }
			 }catch(Exception ex){
				Utils.printException(ex);
			 }
			 return -1;
		}
	 
}
