package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class IMMessageBean implements Parcelable {
	public static final String IMMESSAGE_KEY = "immessage.key";
	public static final String KEY_TIME = "immessage.time";
	public static final int SUCCESS = 1;
	public static final int ERROR = 0;
	public static final int SEND=2;
	public static final int SELF = 1;
	public static final int OTHER = 0;
	
	private int msgId;
	private int sessionId;
	private int type;//消息类型 0文字 1图片 2语音 3文件
	private String content;//消息内容
	private String time;//消息时间
	private String sender;//消息发送人
	private int state;//消息状态  1成功  0失败  2发送中
	private int isSelf;//0接收 1发送
	private String attaFile;
	private int voiceSecond=0;
	private byte[] buff;
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeInt(msgId);
		dest.writeInt(sessionId);
		dest.writeInt(type);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(sender);
		dest.writeInt(state);
		dest.writeInt(isSelf);
		dest.writeString(attaFile);
		dest.writeInt(voiceSecond);
		//dest.writeByteArray(buff);
	}

	
	public static final Parcelable.Creator<IMMessageBean> CREATOR = new Parcelable.Creator<IMMessageBean>() {

		@Override
		public IMMessageBean createFromParcel(Parcel source) {
			IMMessageBean message = new IMMessageBean();
			message.setMsgId(source.readInt());
			message.setSessionId(source.readInt());
			message.setType(source.readInt());
			message.setContent(source.readString());
			message.setTime(source.readString());
			message.setSender(source.readString());
			message.setState(source.readInt());
			message.setIsSelf(source.readInt());
			message.setAttaFile(source.readString());
			message.setVoiceSecond(source.readInt());
			return message;
		}

		@Override
		public IMMessageBean[] newArray(int size) {
			return new IMMessageBean[size];
		}
		
	};
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public byte[] getBuff() {
		return buff;
	}

	public void setBuff(byte[] buff) {
		this.buff = buff;
	}


	public int getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(int isSelf) {
		this.isSelf = isSelf;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getAttaFile() {
		return attaFile;
	}

	public void setAttaFile(String attaFile) {
		this.attaFile = attaFile;
	}

	public int getVoiceSecond() {
		return voiceSecond;
	}

	public void setVoiceSecond(int voiceSecond) {
		this.voiceSecond = voiceSecond;
	}
}
