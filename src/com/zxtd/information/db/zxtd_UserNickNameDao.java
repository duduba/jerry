package com.zxtd.information.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class zxtd_UserNickNameDao {
	private zxtd_DBhelper dBhelper;

	public zxtd_UserNickNameDao() {
		dBhelper = new zxtd_DBhelper();
	}
	
	public List<String> getUserNicks(){
		List<String> nickLists = new ArrayList<String>();
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select nick_name from user_nick_name";
		Cursor cursor = database.rawQuery(sql, null);
		
		while (cursor.moveToNext()) {
			String nickName = cursor.getString(0);
			nickLists.add(nickName);
		}
		cursor.close();
		database.close();
		return nickLists;
	}
	
	public boolean hasNickName(String nickName){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "select count(*) from user_nick_name where nick_name=?";
		String[] selects = new String[]{nickName};
		Cursor cursor = database.rawQuery(sql, selects);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		database.close();
		return count != 0;
	}
	
	public void saveUserNick(String nickName){
		SQLiteDatabase database = dBhelper.getWritableDatabase();
		String sql = "insert into user_nick_name (nick_name) values (?)";
		database.execSQL(sql, new String[]{nickName});
		database.close();
	}
	
	public void delUserNick(String nickName){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql = "delete from user_nick_name where nick_name=?";
		database.execSQL(sql, new String[]{nickName});
		database.close();
	}
	
	public void delAllUserNick(){
		SQLiteDatabase database = dBhelper.getReadableDatabase();
		String sql1 = "delete from user_nick_name";
		String sql2 = "delete from sqlite_sequence where name='user_nick_name'";
		database.execSQL(sql1);
		database.execSQL(sql2);
		database.close();
	}
	
	public void closeDb(){
		dBhelper.close();
	}
}
