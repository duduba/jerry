package com.zxtd.information.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.db.zxtd_UserNickNameDao;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public class UserNickManager {
	private static UserNickManager nickManager = null;
	private zxtd_UserNickNameDao mNameDao;
	private String mNickName = null;
	private UserNickManager(){
		mNameDao = new zxtd_UserNickNameDao();
	}
	
	public static UserNickManager getNewInstance(){
		if(nickManager == null){
			nickManager = new UserNickManager();
		}
		return nickManager;
	}
	
	public void setNickName(String nickName){
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.DataKey.NICK_NAME, nickName);
		Utils.save(ZXTD_Application.getMyContext(), map);
		mNickName = nickName;
		
		if(nickName == null || nickName.equals("")){
			return;
		}
		
		if(!mNameDao.hasNickName(nickName)){
			mNameDao.saveUserNick(nickName);
		}
	}
	
	public String getNickName(){
		if(mNickName == null){
			String[] values = Utils.load(ZXTD_Application.getMyContext(), new String[]{Constant.DataKey.NICK_NAME});
			mNickName = values[0];
			if(mNickName == null || mNickName.equals("")){
				mNickName = "杂色网友";
			}
		}
		
		return mNickName;
	}
	
	public List<String> getNickNameList(){
		return mNameDao.getUserNicks();
	}
	
	public void deleteNickName(String nickName){
		if(nickName == null){
			return;
		}
		mNameDao.delUserNick(nickName);
	}
	
	public void deleteAllNickName(){
		mNameDao.delAllUserNick();
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.DataKey.NICK_NAME, "");
		Utils.save(ZXTD_Application.getMyContext(), map);
		mNickName = null;
	}
	
	public void close(){
		mNameDao.closeDb();
		nickManager = null;
	}
	
	public boolean isCurrentNickName(String nickName){
		if(nickName != null && nickName.equals(mNickName)){
			return true;
		}
		return false;
	}
}
