package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class PublicBean implements Parcelable {

	private int newsId;
	private String newsTitle;
	//private String userImg;
	//private String nickName;
	private String publicTime;
	private int replayCount;
	private boolean hasNewReplay=false;
	private String channel="社会";
	private String newsUrl;
	private String content;
	
	public PublicBean(){}
	
	public PublicBean(Parcel p){
		newsId=p.readInt();
		newsTitle=p.readString();
		publicTime=p.readString();
		replayCount=p.readInt();
		channel=p.readString();
		newsUrl=p.readString();
		content=p.readString();
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getPublicTime() {
		return publicTime;
	}

	public void setPublicTime(String publicTime) {
		this.publicTime = publicTime;
	}

	public int getReplayCount() {
		return replayCount;
	}

	public void setReplayCount(int replayCount) {
		this.replayCount = replayCount;
	}

	public boolean isHasNewReplay() {
		return hasNewReplay;
	}

	public void setHasNewReplay(boolean hasNewReplay) {
		this.hasNewReplay = hasNewReplay;
	}

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int arg1) {
		// TODO Auto-generated method stub
		p.writeInt(newsId);
		p.writeString(newsTitle);
		p.writeString(publicTime);
		p.writeInt(replayCount);
		p.writeString(channel);
		p.writeString(newsUrl);
		p.writeString(content);
	}
	
	
	public static final Creator<PublicBean> CREATOR = new Creator<PublicBean>() {
		public PublicBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PublicBean[size];
		}
		
		public PublicBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PublicBean(source);
		}
	};
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getContent() {
		if(TextUtils.isEmpty(content)){
			return "";
		}else{
			if(content.length()>45){
				return content.substring(0, 45)+"...";
			}
			return content;
		}
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
