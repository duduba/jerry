package com.zxtd.information.manager;

import java.io.File;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ImageUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class AlbumsManager {

	private AlbumsManager(){}
	
	private static AlbumsManager manager=null;
	
	public static AlbumsManager getInstance(){
		if(manager==null)
			manager=new AlbumsManager();
		return manager;
	}
	
	public boolean addAlbums(NewImageBean bean){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=new ContentValues();
			values.put("imageId", bean.getImgId());
			values.put("imageUrl", bean.getImageUrl());
			values.put("smallUrl", bean.getSmallUrl());
			values.put("imageDesc", bean.getDescribe());
			values.put("userId", XmppUtils.getUserId());
			flag=db.insert(zxtd_DBhelper.TB_IMAGES, null, values)>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db,Constant.USER_VERSION_KEY);
			}
			db.setTransactionSuccessful();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(db!=null){
				db.endTransaction();
				db.close();
			}
		}
		return flag;
	}
	
	
	public boolean deleteImg(NewImageBean bean){
		boolean flag=false;
		SQLiteDatabase db=null;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			flag=db.delete(zxtd_DBhelper.TB_IMAGES, "imageId=?",new String[]{String.valueOf( bean.getImgId())})>0 ? true : false;
			if(flag){
				CacheManager.getInstance().UpgradeVersion(db, Constant.USER_VERSION_KEY);
				File file=new File(Constant.CACHE_SAVE_PATH+ImageUtils.getFileName(bean.getImageUrl()));
				if(file.exists()){
					file.delete();
				}
				
				file=new File(Constant.CACHE_SAVE_PATH+ImageUtils.getFileName(bean.getSmallUrl()));
				if(file.exists()){
					file.delete();
				}
			}
			db.setTransactionSuccessful();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(db!=null){
				db.endTransaction();
				db.close();
			}
		}
		return flag;
	}

	
	
}
