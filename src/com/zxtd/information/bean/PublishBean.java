package com.zxtd.information.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class PublishBean extends Bean implements Parcelable{
	public String title = null;
	public String content = null;
	public NewTypeBean type = null;
	public String imageUrls = "";
	public String userId = null;
	public long id = -1;
	public String infoUrl = null;
	public String newsId = null;
	
	private PublishBean(Parcel source){
		title = source.readString();
		content = source.readString();
		imageUrls = source.readString();
		userId = source.readString();
		type = new NewTypeBean();
		type.id = source.readString();
		type.name = source.readString();
		id = source.readLong();
		infoUrl = source.readString();
		newsId = source.readString();
	}
	
	public PublishBean(){
		
	}
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(imageUrls);
		dest.writeString(userId);
		if(type == null){
			dest.writeString("");
			dest.writeString("");
		}else{
			dest.writeString(type.id);
			dest.writeString(type.name);
		}
		
		dest.writeLong(id);
		dest.writeString(infoUrl);
		dest.writeString(newsId);
	}
	
	public static final Creator<PublishBean> CREATOR = new Creator<PublishBean>() {
		
		
		public PublishBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PublishBean[size];
		}
		
		
		public PublishBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PublishBean(source);
		}
	};
	
	public List<String> resolveImageUrls(){
		if(imageUrls != null){
			String[] imagesArray = imageUrls.split(";");
			ArrayList<String> imagesList = new ArrayList<String>();
			for (String string : imagesArray) {
				imagesList.add(string);
			}
			return imagesList;
		}
		return null;
	}
	
	public void setImageUrls(List<String> imageUrls){
		StringBuilder sb = new StringBuilder();
		for (String string : imageUrls) {
			sb.append(string + ";");
		}
		this.imageUrls = sb.toString();
	}
}
