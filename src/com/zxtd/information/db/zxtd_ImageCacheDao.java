package com.zxtd.information.db;

import com.zxtd.information.util.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class zxtd_ImageCacheDao {
	private zxtd_DBhelper dBhelper;

	private static zxtd_ImageCacheDao cacheDao;
	
	public static zxtd_ImageCacheDao Instance(){
		if(cacheDao == null){
			cacheDao = new zxtd_ImageCacheDao();
		}
		return cacheDao;
	}
	
	private zxtd_ImageCacheDao() {
		dBhelper = new zxtd_DBhelper();
	}
	
	public long saveImage(String imageUrl,String imageFile){
		SQLiteDatabase database = dBhelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("image_file", imageFile);
		values.put("image_url", imageUrl);
		long result= database.insert("image_cache", null, values);
		database.close();
		return result;
	}
	
	public int deleteImageById(long id){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		return database.delete("image_cache", "_id=?", new String[]{String.valueOf(id)});
	}
	
	public int deleteImageByFileName(String fileName){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		return database.delete("image_cache", "image_file=?", new String[]{fileName});
	}
	
	public String getImageCacheFile(String imageUrl){
		if(Utils.isEmpty(imageUrl)){
			return null;
		}
		String imageFile = "";
		synchronized (this) {
			SQLiteDatabase database = dBhelper.getReadableDatabase();
			String sql = "select image_file from image_cache where image_url=?";
			Cursor cursor = database.rawQuery(sql, new String[]{imageUrl});
			if(cursor.moveToFirst()){
				imageFile = cursor.getString(0);
			}
			cursor.close();
			database.close();
		}
		return imageFile;
	}
	
	public boolean isMaxCount(int max){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select count(*)  from image_cache";
		Cursor cursor = database.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count > max;
	}
	
	public String getImageFileById(long id){
		String imageFile = "";
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select image_file from image_cache where _id=?";
		Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
		if(cursor.moveToFirst()){
			imageFile = cursor.getString(0);
		}
		cursor.close();
		return imageFile;
	}
	
	public long getMinId(){
		long id = -1;
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select _id  from image_cache ORDER BY _id ASC";
		Cursor cursor = database.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			id = cursor.getLong(0);
		}
		cursor.close();
		return id;
	}
	
	public void closeDb(){
		dBhelper.close();
	}
}
