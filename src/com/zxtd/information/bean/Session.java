package com.zxtd.information.bean;

import com.zxtd.information.util.XmppUtils;
import android.os.Parcel;
import android.os.Parcelable;


public class Session implements Parcelable {

	private int sessionId;
	private String sessionName;//聊天对象ID
	private String nickName;//聊天对象
	private String img;
	private int creator;
	private String createDate;
	private String lastSendTime;
	private String lastMsg;
	private int noReadMsgCount;
	private int sort;
	private int state;//1 正常  2置顶
	private boolean isChecked=false;
	
	public Session(){}
	
	public Session(int sid,String sName,String sImg,String sLastDate,String sLastMsg){
		this.sessionId=sid;
		this.sessionName=sName;
		this.img=sImg;
		this.lastSendTime=sLastDate;
		this.lastMsg=sLastMsg;
	}
	
	
	public Session(Parcel p){
		sessionId=p.readInt();
		sessionName=p.readString();
		nickName=p.readString();
		img=p.readString();
		creator=p.readInt();
		createDate=p.readString();
		lastSendTime=p.readString();
		lastMsg=p.readString();
		noReadMsgCount=p.readInt();
		sort=p.readInt();
	}
	
	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getLastSendTime() {
		return lastSendTime;
	}

	public void setLastSendTime(String lastSendTime) {
		this.lastSendTime = lastSendTime;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public int getNoReadMsgCount() {
		return noReadMsgCount;
	}

	public void setNoReadMsgCount(int noReadMsgCount) {
		this.noReadMsgCount = noReadMsgCount;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel p, int arg1) {
		// TODO Auto-generated method stub
		p.writeInt(sessionId);
		p.writeString(sessionName);
		p.writeString(nickName);
		p.writeString(img);
		p.writeInt(creator);
		p.writeString(createDate);
		p.writeString(lastSendTime);
		p.writeString(lastMsg);
		p.writeInt(noReadMsgCount);
		p.writeInt(sort);
	}
	
	public static final Creator<Session> CREATOR = new Creator<Session>() {
		public Session[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Session[size];
		}
		
		public Session createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Session(source);
		}
	};

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getJid(){
		if (sessionName == null) return null;
		return sessionName + "@" +XmppUtils.SERVER_NAME;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	
}
