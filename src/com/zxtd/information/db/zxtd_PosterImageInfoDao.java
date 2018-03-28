package com.zxtd.information.db;

import com.zxtd.information.bean.PosterImageBean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class zxtd_PosterImageInfoDao {
	private zxtd_DBhelper dbHelper;
	
	public zxtd_PosterImageInfoDao() {
		dbHelper = new zxtd_DBhelper();
	}
	
	/**
	 * 查看数据库中是否有某个ID存在
	 */
	public int isHasData(int id) {
		int count = 0;
		SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
		String sql = "select *  from poster_info where _id="+id;
		Cursor cursor = dataBase.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			count = 1;
		}
		cursor.close();
		return count;
	}
	
	// 添加一条记录信息
	public void addRecordById(int id, String posterId, String imageName, String imagePath, String httpUrl){
		SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("_id", id);
		cv.put("poster_id", posterId);
		cv.put("imagefile_name", imageName);
		cv.put("image_path", imagePath);
		cv.put("http_url", httpUrl);
		dataBase.insert("poster_info", null, cv);
	}
	
	// 通过主键ID删除一条记录信息
	public void deleteRecordById(int id){
		SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
		String[] args = {String.valueOf(id)};
		dataBase.delete("poster_info", "_id=?", args);
	}
	
	// 通过主键ID更新一条记录信息
	public void updateRecordById(int id, String posterId, String imageName, String imagePath, String httpUrl){
		SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("poster_id", posterId);
		cv.put("imagefile_name", imageName);
		cv.put("image_path", imagePath);
		cv.put("http_url", httpUrl);
		String[] args = {String.valueOf(id)};
		dataBase.update("poster_info", cv, "_id=?", args);
	}
	
	// 通过主键ID来获取表的一条记录信息
	public PosterImageBean getPosterDataById(int id){
		PosterImageBean imageBean = new PosterImageBean();
		SQLiteDatabase dataBase = dbHelper.getReadableDatabase();
		String sql = "select * from poster_info where _id=?";
		Cursor cursor = dataBase.rawQuery(sql, new String[]{id+""});
		if(cursor.moveToFirst()){
			imageBean.posterId = cursor.getString(cursor.getColumnIndex("poster_id"));
			imageBean.imageName = cursor.getString(cursor.getColumnIndex("imagefile_name"));
			imageBean.imagePath = cursor.getString(cursor.getColumnIndex("image_path"));
			imageBean.httpUrl = cursor.getString(cursor.getColumnIndex("http_url"));
		}
		cursor.close();
		return imageBean;
	}
	
	public void closeDb(){
		dbHelper.close();
	}
}
