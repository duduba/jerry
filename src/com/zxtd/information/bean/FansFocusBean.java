package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FansFocusBean implements Parcelable {

	private int userId;
	private String nickName;
	private String account;
	private String img;
	private String sign;
	private int attentionState;
	private String area;
	public static int fansCount=0;
	public static int focusCount=0;
	private boolean isAddFocus=false;
	private String sortKey;
	
	public FansFocusBean(){
	}
	
	public FansFocusBean(Parcel p){
		this.userId=p.readInt();
		this.nickName=p.readString();
		this.img=p.readString();
		this.sign=p.readString();
		this.attentionState=p.readInt();
	}
	
	
	public boolean isAddFocus() {
		return isAddFocus;
	}

	public void setAddFocus(boolean isAddFocus) {
		this.isAddFocus = isAddFocus;
	}



	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getSign() {
		if(null==sign || "null".equals(sign))
			return "";
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getAttentionState() {
		return attentionState;
	}

	public void setAttentionState(int attentionState) {
		this.attentionState = attentionState;
	}

	
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel p, int arg1) {
		// TODO Auto-generated method stub
		p.writeInt(userId);
		p.writeString(nickName);
		p.writeString(img);
		p.writeString(sign);
		p.writeInt(attentionState);
	}
	
	public static final Creator<FansFocusBean> CREATOR = new Creator<FansFocusBean>() {
		public FansFocusBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FansFocusBean[size];
		}
		
		public FansFocusBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new FansFocusBean(source);
		}
	};

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	

}
