package com.zxtd.information.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zxtd.information.bean.Session;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class SessionManager {

	private SessionManager(){
		
	}
	
	private static SessionManager manager;
	
	public static SessionManager getInstance(){
		if(null==manager)
			manager=new SessionManager();
		return manager;
	}
	
	
	/**
	 * 获取会话信息
	 * @param userId
	 * @return
	 */
	public List<Session> getSessions(int userId){
		List<Session> list=new ArrayList<Session>(10);

		//XMPPConnection conn=XmppUtils.getSimpleConnection();
		//if(null!=conn){
		//	Roster roster=conn.getRoster();
			//分组好友
			/*
			Collection<RosterGroup> entriesGroup = roster.getGroups();
			if(!entriesGroup.isEmpty()){
				for(RosterGroup group: entriesGroup){
					Collection<RosterEntry> entries = group.getEntries();
				
					for (RosterEntry entry : entries) {
						
						Session session=new Session();
						session.setSessionId(1);
						session.setSessionName(XmppUtils.getJidToUsername(entry.getUser()));
						session.setNickName(entry.getName());
						session.setLastMsg("状态:"+entry.getStatus().fromString(entry.getUser()));
						session.setImg("http://f.hiphotos.baidu.com/album/w%3D2048/sign=00929a40a686c91708035539fd0571cf/0824ab18972bd407a0a2ad457a899e510fb30924.jpg");
						list.add(session);
					}
				}
			}else{*/
				//获取所有好友
		/*
				for (RosterEntry entry : roster.getEntries()) {
					Session session=new Session();
					session.setSessionId(1);
					session.setSessionName(XmppUtils.getJidToUsername(entry.getUser()));
					session.setNickName(entry.getName());
					session.setLastMsg("状态:"+entry.getStatus().fromString(entry.getUser()));
					session.setImg("http://f.hiphotos.baidu.com/album/w%3D2048/sign=00929a40a686c91708035539fd0571cf/0824ab18972bd407a0a2ad457a899e510fb30924.jpg");
					list.add(session);
				}
				*/
				//获取未分组好友 服务器的用户信息改变后，不会通知到unfiledEntries
				//for (RosterEntry entry : roster.getUnfiledEntries()) 
			//}
		

		//}
		
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_SESSION+" where selfId=? order by sortIndex desc,lastReceive desc", new String[]{String.valueOf(userId)});
			Constant.CACHESESSION.clear();
			while(cursor.moveToNext()){
				Session session=new Session();
				session.setSessionId(cursor.getInt(cursor.getColumnIndex("sessionId")));
				String chatId=cursor.getString(cursor.getColumnIndex("chatId"));
				session.setSessionName(chatId);
				session.setNickName(cursor.getString(cursor.getColumnIndex("chatName")));
				session.setImg(cursor.getString(cursor.getColumnIndex("chatImg")));
				session.setLastMsg(cursor.getString(cursor.getColumnIndex("lastMsg")));
				session.setLastSendTime(cursor.getString(cursor.getColumnIndex("lastReceive")));
				session.setCreateDate(cursor.getString(cursor.getColumnIndex("createDate")));
				session.setNoReadMsgCount(cursor.getInt(cursor.getColumnIndex("noReadCount")));
				list.add(session);
				Constant.CACHESESSION.put(chatId, session);
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		//test();
		return list;
	}
	
	/*
	private void test(){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=helper.getReadableDatabase();
		String[] columns={"sessionId","chatId","chatName","selfId"};
		Cursor cursor=db.query(zxtd_DBhelper.TB_SESSION, columns, null, null, null, null, null);
		while(cursor.moveToNext()){
			Log.e(Constant.TAG,"cccccccccc chatId:"+cursor.getString(cursor.getColumnIndex("chatId"))+"nnnnnn chatName: " +cursor.getString(cursor.getColumnIndex("chatName"))+"    ssssssss  selfId: "+cursor.getString(cursor.getColumnIndex("selfId")));
		}
		cursor.close();
		db.close();
	}
	*/
	
	
	
	
	/**
	 * 添加回话
	 * @return
	 */
	public int addSession(String chatId,String chatName,String img,String sign,
			String lastMsg,String lastTime,int noRead,int selfId){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int _id=-1;
		try{
			db=helper.getWritableDatabase();
			//String[] columns={"chatId","chatName","chatImg","chatSign","lastMsg","lastReceive","selfId",
			//		"state","createDate","noReadCount","sortIndex"};
			ContentValues values=new ContentValues();
			values.put("chatId", chatId);
			values.put("chatName", chatName);
			values.put("chatImg", img);
			values.put("chatSign", sign);
			values.put("lastMsg", lastMsg);
			values.put("lastReceive", lastTime);
			values.put("selfId", String.valueOf(selfId));
			values.put("state", 1);
			values.put("createDate", TimeUtils.getNow());
			values.put("noReadCount", noRead);
			values.put("sortIndex", 0);
			//db.insert(zxtd_DBhelper.TB_SESSION, columns, values);
			boolean isSuccess=db.insert(zxtd_DBhelper.TB_SESSION, null, values)>0 ? true :false;
			if(isSuccess){
				Cursor cursor = db.rawQuery("select last_insert_rowid() from "+zxtd_DBhelper.TB_SESSION,null);        
		        if(cursor.moveToFirst()){
		        	_id = cursor.getInt(0);
		        }
		        cursor.close();
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return _id;
	}
	
	
	
	/**
	 * 检查是否存在该回话
	 * @param chatId
	 * @param selfId
	 * @return
	 */
	public boolean checkSessionExtis(String chatId,int selfId){
		if(!Constant.CACHESESSION.containsKey(chatId)){
			zxtd_DBhelper helper=new zxtd_DBhelper();
			SQLiteDatabase db=null;
			boolean isExtis=false;
			try{
				db=helper.getReadableDatabase();
				Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_SESSION+" where chatId=? and selfId=?",
							new String[]{chatId,String.valueOf(selfId)});
				cursor.moveToFirst();
				isExtis=cursor.getCount()>0 ? true :false;
				cursor.close();
			}catch(Exception ex){
				Utils.printException(ex);
			}finally{
				if(null!=db)
					db.close();
			}
			return isExtis;
		}else{
			return true;
		}
	}
	
	
	
	
	/**
	 * 根据ID获取回话信息
	 * @param _id
	 * @return
	 */
	public Session getSessionById(int _id){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		Session session=null;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+zxtd_DBhelper.TB_SESSION+" where sessionId=?", new String[]{String.valueOf(_id)});
			if(cursor.moveToFirst()){
				session=new Session();
				session.setSessionId(cursor.getInt(cursor.getColumnIndex("sessionId")));
				String chatId=cursor.getString(cursor.getColumnIndex("chatId"));
				session.setSessionName(chatId);
				session.setNickName(cursor.getString(cursor.getColumnIndex("chatName")));
				session.setImg(cursor.getString(cursor.getColumnIndex("chatImg")));
				session.setLastMsg(cursor.getString(cursor.getColumnIndex("lastMsg")));
				session.setLastSendTime(cursor.getString(cursor.getColumnIndex("lastReceive")));
				session.setCreateDate(cursor.getString(cursor.getColumnIndex("createDate")));
				session.setNoReadMsgCount(cursor.getInt(cursor.getColumnIndex("noReadCount")));	
			}
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return session;
	}
	
	
	
	
	/**
	 * 删除会话
	 * @param sessionId
	 * @return
	 */
	public int deleteSession(List<Session> list){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int noReadCount=0;
		try{
			db=helper.getWritableDatabase();
			Iterator<Session> it=list.iterator();
			db.beginTransaction();
			while(it.hasNext()){
				Session session=it.next();
				int sessionId=session.getSessionId();
				noReadCount+=session.getNoReadMsgCount();
				db.delete(zxtd_DBhelper.TB_MESSAGE, "sessionId=?", new String[]{String.valueOf(sessionId)});
				db.delete(zxtd_DBhelper.TB_SESSION, "sessionId=?", new String[]{String.valueOf(sessionId)});
			}
			if(noReadCount>0){
				db.execSQL("update "+zxtd_DBhelper.TB_MINE+" set letterCount=letterCount-"+noReadCount+" where userId="+XmppUtils.getUserId());
			}
			db.setTransactionSuccessful();
			isSuccess=true;
		}catch(Exception ex){
			Utils.printException(ex);
			noReadCount=-1;
		}finally{
			if(null!=db){
				db.endTransaction();
				db.close();
			}
		}
		return noReadCount;
	}
	
	
	
	/**
	 * 会话置顶
	 * @param sessionId
	 * @return
	 */
	public boolean updateSort(int sessionId,int selfId){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			ContentValues values=new ContentValues();
			values.put("sortIndex", 0);
			db.update(zxtd_DBhelper.TB_SESSION, values, "selfId=? and sortIndex>?", new String[]{String.valueOf(selfId),"0"});
			ContentValues newValues=new ContentValues();
			newValues.put("sortIndex", 1);
			db.update(zxtd_DBhelper.TB_SESSION, newValues, "sessionId=?", new String[]{String.valueOf(sessionId)});
			db.setTransactionSuccessful();
			isSuccess=true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.endTransaction();
				db.close();
			}
		}
		return isSuccess;
	}
	
	
	
	/**
	 * 重置会话未读数量为0
	 * @param sessionId
	 * @return
	 */
	public boolean clearNoRead(int sessionId){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("noReadCount", 0);
			isSuccess=db.update(zxtd_DBhelper.TB_SESSION, values, "sessionId=?", new String[]{String.valueOf(sessionId)})>0 ? true:false;
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
	 * 保存未读消息数量
	 * @param list
	 * @return
	 */
	public boolean setNoReadCount(List<Session> list){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			db.beginTransaction();
			Iterator<Session> it=list.iterator();
			int totalNoRead=0;
			while(it.hasNext()){
				Session session=it.next();
				if(session.getNoReadMsgCount()>0){
					ContentValues values=new ContentValues();
					values.put("noReadCount", session.getNoReadMsgCount());
					totalNoRead+=session.getNoReadMsgCount();
					values.put("lastMsg", session.getLastMsg());
					values.put("lastReceive", session.getLastSendTime());
					db.update(zxtd_DBhelper.TB_SESSION, values, "sessionId=?", new String[]{String.valueOf(session.getSessionId())});
				}
			}
			//db.update(table, values, whereClause, whereArgs)
			db.setTransactionSuccessful();
			isSuccess=true;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.endTransaction();
				db.close();
			}
		}
		return isSuccess;
	}
	
	
	/**
	 * 设置未读数量
	 * @return
	 */
	public boolean modifySession(int sessionId,String lastMsg,String lastTime,int count){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("noReadCount", count);
			values.put("lastMsg",lastMsg);
			values.put("lastReceive", lastTime);
			isSuccess=db.update(zxtd_DBhelper.TB_SESSION, values, "sessionId=?", new String[]{String.valueOf(sessionId)})>0 ? true:false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return isSuccess;
	}
	
	
	
	
	public boolean modifySessionLastMsg(String lastMsg,String lastTime,int sessionId){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("lastMsg", lastMsg);
			values.put("lastReceive", lastTime);
			isSuccess=db.update(zxtd_DBhelper.TB_SESSION, values, "sessionId=?", new String[]{String.valueOf(sessionId)})>0 ? true:false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return isSuccess;
	}
	
	
	/*
	public boolean updateLastTime(int sessionId,String time){
		boolean isSuccess=false;
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("lastReceive", time);
			isSuccess=db.update(zxtd_DBhelper.TB_SESSION, values, "sessionId=?", new String[]{String.valueOf(sessionId)})>0 ? true:false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return isSuccess;
	}
	*/
	
	
}
