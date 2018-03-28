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
import android.util.Xml;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class CacheManager {

	private static CacheManager manager=null;
	
	public static final Map<String,Integer> CACHEVERSION=new HashMap<String,Integer>(5);
	public static final Map<String,Integer> LOCALVERSION=new HashMap<String,Integer>(5);
	
	private CacheManager(){	
	}
	
	public static CacheManager getInstance(){
		if(null==manager)
			manager=new CacheManager();
		return manager;
	}
	
	
	public class VersionBean{
		private String versionType;
		private int versioNum=0;
	
		public String getVersionType() {
			return versionType;
		}
		
		public void setVersionType(String versionType) {
			this.versionType = versionType;
		}

		public int getVersioNum() {
			return versioNum;
		}

		public void setVersioNum(int versioNum) {
			this.versioNum = versioNum;
		}

		public VersionBean(String type,int num){
			this.versionType=type;
			this.versioNum=num;
		}
	}
	
	
	private boolean addVersions(List<VersionBean> list){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		boolean flag=false;
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			Iterator<VersionBean> it=list.iterator();
			while(it.hasNext()){
				VersionBean bean=it.next();
				ContentValues versionValues=new ContentValues();
				versionValues.put("versionType",bean.getVersionType());
				versionValues.put("versionNum", bean.getVersioNum());
				versionValues.put("userId",XmppUtils.getUserId());
				db.insert(zxtd_DBhelper.TB_VERSION, null, versionValues);
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
	
	private boolean checkExistVersion(){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		boolean flag=false;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_VERSION+" where userId="+XmppUtils.getUserId(), null);
			cursor.moveToFirst();
			int count=cursor.getCount();
			flag=count>0 ? true : false;
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
	
	
	public boolean initVersions(){
		boolean isExist=checkExistVersion();
		if(!isExist){
			List<VersionBean> list=new ArrayList<VersionBean>();
			list.add(new VersionBean(Constant.USER_VERSION_KEY,0));
			list.add(new VersionBean(Constant.FANS_VERSION_KEY,1));
			list.add(new VersionBean(Constant.PUBLIC_VERSION_KEY,1));
			list.add(new VersionBean(Constant.COMMENT_VERSION_KEY,1));
			list.add(new VersionBean(Constant.COLLECTION_VERSION_KEY,1));
			isExist=addVersions(list);
		}
		return isExist;
	}
	
	
	/**
	 * 获取版本号
	 * @return
	 */
	public boolean getVersions(){
		boolean flag=false;
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(XmppUtils.getUserId()));
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_CACHE_GETVERSION);
		try{
			String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					String namespace=null;
					CACHEVERSION.clear();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){
							String tagName=parser.getName();
							if(tagName.equals("result-code")){
								int resultCode=Integer.valueOf(parser.nextText());
								if(resultCode!=1){
									return false;
								}
							}else if(tagName.equals("userversion")){
								int value=Integer.valueOf(parser.getAttributeValue(namespace, "version"));
								String key=parser.getAttributeValue(namespace, "operate");
								CACHEVERSION.put(key, value);
							}
						}
						eventType=parser.next();
					}
					return true;
			 }
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return flag;
	}
	
	
	
	/**
	 *  获取本地版本号
	 */
	public void getLocalVersion(){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_VERSION+" where userId=? order by versionType asc", new String[]{String.valueOf(XmppUtils.getUserId())});
			LOCALVERSION.clear();
			while(cursor.moveToNext()){
				String key=cursor.getString(cursor.getColumnIndex("versionType"));
				int value=cursor.getInt(cursor.getColumnIndex("versionNum"));
				LOCALVERSION.put(key, value);
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		
		if(CacheManager.LOCALVERSION.isEmpty()){
			initVersions();
			getLocalVersion();
		}
	}
	
	public boolean modifyVersion(SQLiteDatabase db,String versionType,int versionNum){
		boolean isSuccess=false;
		try{
			ContentValues values=new ContentValues();
			values.put("versionNum",versionNum);
			isSuccess=db.update(zxtd_DBhelper.TB_VERSION, values, "versionType=? and userId=?", new String[]{versionType,String.valueOf(XmppUtils.getUserId())})> 0 ? true:false;
			if(isSuccess){
				CACHEVERSION.put(versionType, versionNum);
				LOCALVERSION.put(versionType, versionNum);
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return isSuccess;
	}
	
	
	public boolean modifyVersion(String versionType,int versionNum){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("versionNum",versionNum);
			isSuccess=db.update(zxtd_DBhelper.TB_VERSION, values, "versionType=? and userId=?", new String[]{versionType,String.valueOf(XmppUtils.getUserId())})> 0 ? true:false;
			if(isSuccess){
				CACHEVERSION.put(versionType, versionNum);
				LOCALVERSION.put(versionType, versionNum);
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return isSuccess;
	}
	
	/**
	 * 升级版本号
	 * @return
	 */
	public boolean UpgradeVersion(SQLiteDatabase db,String versionType){
		int versionNum=CACHEVERSION.get(versionType);
		boolean isSuccess=false;
		try{
			ContentValues values=new ContentValues();
			versionNum++;
			values.put("versionNum",versionNum);
			isSuccess=db.update(zxtd_DBhelper.TB_VERSION, values, "versionType=? and userId=?", new String[]{versionType,String.valueOf(XmppUtils.getUserId())})> 0 ? true:false;
			if(isSuccess){
				CACHEVERSION.put(versionType, versionNum);
				LOCALVERSION.put(versionType, versionNum);
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return isSuccess;
	}
	
	
	/**
	 * 升级版本号
	 * @return
	 */
	public boolean UpgradeVersion(String versionType){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int versionNum=CACHEVERSION.get(versionType);
		boolean isSuccess=false;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			versionNum++;
			values.put("versionNum",versionNum);
			isSuccess=db.update(zxtd_DBhelper.TB_VERSION, values, "versionType=? and userId=?", new String[]{versionType,String.valueOf(XmppUtils.getUserId())})> 0 ? true:false;
			if(isSuccess){
				CACHEVERSION.put(versionType, versionNum);
				LOCALVERSION.put(versionType, versionNum);
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		return isSuccess;
	}
	
	
	/**
	 * 
	 * @param versionType
	 * @return
	 */
	public int getVersion(String versionType){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int versionNum=1;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_VERSION+" where versionType=? and userId=?",
					new String[]{versionType,String.valueOf(XmppUtils.getUserId())});
			cursor.moveToFirst();
			versionNum=cursor.getInt(cursor.getColumnIndex("versionNum"));
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return versionNum;
	}
	
	
	
}
