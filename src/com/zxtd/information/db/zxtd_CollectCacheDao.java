package com.zxtd.information.db;

import java.util.HashSet;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class zxtd_CollectCacheDao {
	private zxtd_DBhelper dbHelper;
	public zxtd_CollectCacheDao(){
		dbHelper = new zxtd_DBhelper();
	}
	
	public Set<String> getCollectKeys(){
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Set<String> set = new HashSet<String>();
		String sql = "select newsId,newsType from " + zxtd_DBhelper.TB_COLLECTION;
		Cursor cursor = database.rawQuery(sql, null);
		while(cursor.moveToNext()){
			int newsId = cursor.getInt(0);
			int newsType = cursor.getInt(1);
			set.add(newsId + "&" + newsType);
		}
		cursor.close();
		database.close();
		return set;
	}
}
