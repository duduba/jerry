package com.zxtd.information.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zxtd.information.bean.IMMessageBean;
import com.zxtd.information.db.zxtd_DBhelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class MessageManager {

	private MessageManager(){
		
	}
	
	private static MessageManager manager=null;
	
	public static MessageManager getInstance(){
		if(null==manager)
			manager=new MessageManager();
		return manager;
	}
	
	
	/**
	 * 添加消息记录
	 * @param bean
	 * @return
	 */
	public int insertMessage(IMMessageBean bean){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int _id=0;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("sessionId", bean.getSessionId());
			values.put("msgContext", bean.getContent());
			values.put("msgType", bean.getType());
			values.put("msgTime", bean.getTime());
			values.put("msgFrom", bean.getSender());
			values.put("msgState", bean.getState());
			values.put("msgAtta", bean.getAttaFile());
			values.put("isSelf", bean.getIsSelf());
			values.put("voiceSecend", bean.getVoiceSecond());
			boolean isSuccess=db.insert(zxtd_DBhelper.TB_MESSAGE, null, values)>0 ? true:false;
			if(isSuccess){
				Cursor cursor = db.rawQuery("select last_insert_rowid() from "+zxtd_DBhelper.TB_MESSAGE,null);        
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
	 * 获取会话聊天记录
	 * @param sessionId
	 * @param pageIndex
	 * @return
	 */
	public List<IMMessageBean> getMessagesBySession(int sessionId,int pageIndex){
		List<IMMessageBean> list=new ArrayList<IMMessageBean>(20);
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select * from "+"(select * from "+zxtd_DBhelper.TB_MESSAGE+" where sessionId=? order by msgId desc " +
					"limit "+Constant.PAGESIZE+" offset "+pageIndex*Constant.PAGESIZE+") as t order by msgId asc", new String[]{String.valueOf(sessionId)});
			while(cursor.moveToNext()){
				IMMessageBean bean=new IMMessageBean();
				bean.setMsgId(cursor.getInt(cursor.getColumnIndex("msgId")));
				bean.setSessionId(cursor.getInt(cursor.getColumnIndex("sessionId")));
				bean.setContent(cursor.getString(cursor.getColumnIndex("msgContext")));
				bean.setType(cursor.getInt(cursor.getColumnIndex("msgType")));
				bean.setTime(cursor.getString(cursor.getColumnIndex("msgTime")));
				bean.setSender(cursor.getString(cursor.getColumnIndex("msgFrom")));
				bean.setState(cursor.getInt(cursor.getColumnIndex("msgState")));
				bean.setAttaFile(cursor.getString(cursor.getColumnIndex("msgAtta")));
				bean.setIsSelf(cursor.getInt(cursor.getColumnIndex("isSelf")));
				bean.setVoiceSecond(cursor.getInt(cursor.getColumnIndex("voiceSecend")));
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
	 * 修改消息状态
	 * @param bean
	 * @return
	 */
	public boolean modifyMsgState(int msgId,int state){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		boolean isSuccess=false;
		try{
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("msgState", state);
			isSuccess=db.update(zxtd_DBhelper.TB_MESSAGE, values, "msgId=?", new String[]{String.valueOf(msgId)})>0 ? true:false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return isSuccess;
	}
	
	
	/**
	 * 删除消息
	 * @param msgId
	 * @return
	 */
	public boolean deleteMessage(int msgId){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		boolean isSuccess=false;
		try{
			db=helper.getWritableDatabase();
			isSuccess=db.delete(zxtd_DBhelper.TB_MESSAGE,"msgId=?", new String[]{String.valueOf(msgId)})>0 ? true:false;
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db)
				db.close();
		}
		return isSuccess;
	}
	
	
	public int getTotalNoReadCount(){
		zxtd_DBhelper helper=new zxtd_DBhelper();
		SQLiteDatabase db=null;
		int totalCount=0;
		try{
			db=helper.getReadableDatabase();
			Cursor cursor=db.rawQuery("select sum(noReadCount) as totalCount from "+zxtd_DBhelper.TB_SESSION+" where selfId=? and noReadCount>0 ", new String[]{String.valueOf(XmppUtils.getUserId())});
			cursor.moveToFirst();
			totalCount=cursor.getInt(cursor.getColumnIndex("totalCount"));
			cursor.close();
		}catch(Exception ex){
			Utils.printException(ex);
		}finally{
			if(null!=db){
				db.close();
			}
		}
		return totalCount;
	}
	
}
