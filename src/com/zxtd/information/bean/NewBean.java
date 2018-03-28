package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 
 * @author Administrator
 *<li> newId		新闻id
 *<li> newTitle		新闻标题
 *<li> infoUrl		新闻url
 *<li> newContent	新闻内容
 *<li> postCount	新闻评论数
 */
public class NewBean extends Bean implements Parcelable{

	public String newId = null;
	public String newTitle = null;
	public String newOutline = null;
	public String newsTpye = null;
	public String iconUrl = null;
	public String newInfoUrl = null;
	public String postCount = null;
	public String infoUrl = null;
	public String flag = null;
	public String operid = null;
	public String newContent = null;
	
	private NewBean(Parcel source){
		newId = source.readString();
		newTitle = source.readString();
		newInfoUrl = source.readString();
		newOutline = source.readString();
		iconUrl = source.readString();
		postCount = source.readString();
		infoUrl = source.readString();
		flag = source.readString();
		operid = source.readString();
		newContent = source.readString();
	}
	
	public NewBean(){
		
	}
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(newId);
		dest.writeString(newTitle);
		dest.writeString(newInfoUrl);
		dest.writeString(newOutline);
		dest.writeString(iconUrl);
		dest.writeString(postCount);
		dest.writeString(infoUrl);
		dest.writeString(flag);
		dest.writeString(operid);
		dest.writeString(newContent);
	}
	
	public static final Creator<NewBean> CREATOR = new Creator<NewBean>() {
		
		
		public NewBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new NewBean[size];
		}
		
		
		public NewBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new NewBean(source);
		}
	};
	public String toString() {
		return operid;
	};
}
