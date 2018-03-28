package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class NewImageBean implements Parcelable{
	
	private int imgId;
	private String imageUrl="";
	private String smallUrl="";
	private String describe="";
	
	public NewImageBean(){
		
	}
	
	public NewImageBean(Parcel p){
		imgId=p.readInt();
		imageUrl=p.readString();
		smallUrl=p.readString();
		describe=p.readString();
	}
	
	public int getImgId() {
		return imgId;
	}
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	public String getSmallUrl() {
		return smallUrl;
	}

	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(imgId);
		dest.writeString(imageUrl);
		dest.writeString(smallUrl);
		dest.writeString(describe);
	}
	
	public static final Creator<NewImageBean> CREATOR = new Creator<NewImageBean>() {
		public NewImageBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new NewImageBean[size];
		}
		
		public NewImageBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new NewImageBean(source);
		}
	};
	
}
