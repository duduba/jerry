package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadNewsListBean extends Bean implements Parcelable{
	public String id = null;
	public String title = null;
	public String nickname = null;
	public String time = null;
	public String postcount = null;
	public String infoUrl = null;
	public String flag = null;
	public String operid = null;
	public String address = null;
	public String imgUrl = null;
	
	public DownloadNewsListBean(){
		
	}
	
	private DownloadNewsListBean(Parcel source){
		id = source.readString();
		title = source.readString();
		nickname = source.readString();
		time = source.readString();
		postcount = source.readString();
		infoUrl = source.readString();
		flag = source.readString();
		operid = source.readString();
		imgUrl = source.readString();
	}
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(nickname);
		dest.writeString(time);
		dest.writeString(postcount);
		dest.writeString(infoUrl);
		dest.writeString(flag);
		dest.writeString(operid);
		dest.writeString(imgUrl);
	}
	
	public static final Creator<DownloadNewsListBean> CREATOR = new Creator<DownloadNewsListBean>() {
		
		
		public DownloadNewsListBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new DownloadNewsListBean[size];
		}
		
		
		public DownloadNewsListBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new DownloadNewsListBean(source);
		}
	};
	
	public String toString() {
		return operid;
	};
}
