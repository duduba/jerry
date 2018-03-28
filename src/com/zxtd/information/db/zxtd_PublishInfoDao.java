package com.zxtd.information.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.bean.PublishBean;

public class zxtd_PublishInfoDao {
	private zxtd_DBhelper dBhelper;
	public zxtd_PublishInfoDao(){
		dBhelper = new zxtd_DBhelper();
	}
	
	/**
	 * 查看数据库中是否有数据
	 */
	public boolean isHasPublish(long id) {
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select count(*)  from publish_info where _id=?";
		Cursor cursor = database.rawQuery(sql, new String[] { id + "" });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 0;
	}
	
	public boolean isHasTitle(String title) {
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select count(*)  from publish_info where title=? ORDER BY _id DESC";
		Cursor cursor = database.rawQuery(sql, new String[] { title });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 0;
	}
	
	public long getMaxId(){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select _id  from publish_info ORDER BY _id DESC";
		Cursor cursor = database.rawQuery(sql, null);
		long id = 0;
		if(cursor.moveToFirst()){
			id = cursor.getInt(0);
		}
		cursor.close();
		return id;
	}
	/**
	 *查询数据中所有数据 
	 */
	public List<PublishBean> getInfos(){
		List<PublishBean> infos = new ArrayList<PublishBean>();
		PublishBean info = null;
		NewTypeBean typeBean = null;
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select _id, title, content, typeid, typename, imageurls from publish_info";
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			info = new PublishBean();
			typeBean = new NewTypeBean();
			info.id = cursor.getInt(0);
			info.title = cursor.getString(1);
			info.content = cursor.getString(2);
			typeBean.id = cursor.getString(3);
			typeBean.name = cursor.getString(4);
			info.imageUrls = cursor.getString(5);
			info.type = typeBean;
			infos.add(info);
		}
		cursor.close();
		return infos;
	}

	/**
	 * 保存 新闻的具体信息
	 */
	public long saveInfos(PublishBean publishBean) {
		SQLiteDatabase database = dBhelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("title", publishBean.title);
		values.put("content", publishBean.content);
		values.put("typeid", publishBean.type.id);
		values.put("typename", publishBean.type.name);
		values.put("imageurls", publishBean.imageUrls);
		return database.insert("publish_info", null, values);
	}

	/**
	 * 得到下载具体信息
	 */
//	public SoftDownBean getInfo(String downUrl) {
//		SoftDownBean info = null;
//		SQLiteDatabase database = dBhelper.getReadableDatabase();
//		String sql = "select thread_id, start_pos, end_pos, compelete_size, url, status, filename from publish_info where url=?";
//		Cursor cursor = database.rawQuery(sql, new String[] { downUrl });
//		while (cursor.moveToNext()) {
//			info = new SoftDownBean();
//			info.softId = cursor.getString(0);
//			info.startPos = cursor.getInt(1);
//			info.softSize = cursor.getInt(2);
//			info.downedSize = cursor.getInt(3);
//			info.downUrl = cursor.getString(4);
//			info.downStatus = cursor.getInt(5);
//			info.softName = cursor.getString(6);
//		}
//		cursor.close();
//		return info;
//	}

	/**
	 * 更新数据库中的新闻信息
	 */
	public void updataInfo(PublishBean publishBean) {
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "update publish_info set title=?, content=?, typeid=?, typename=?, imageurls=? where _id=?";
		Object[] bindArgs = { publishBean.title, publishBean.content, publishBean.type.id, publishBean.type.name, publishBean.imageUrls, publishBean.id };
		database.execSQL(sql, bindArgs);
		database.close();
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		dBhelper.close();
	}

	/**
	 * 删除数据库中的数据
	 */
	public void delete(long id) {
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		database.delete("publish_info", "_id=?", new String[] { id + "" });
		database.close();
	}
	
}
