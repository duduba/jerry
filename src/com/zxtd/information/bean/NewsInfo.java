package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsInfo implements Parcelable {

	private int newsId;
	private String newsTitle;
	private String newSummary;
	private int newReplyCount;
	private String newsImg;
	private String newsUrl;
	private int newsType;
	private int newsFrom;
	private boolean isSelected=false;
	
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public String getNewsTitle() {
		if(null==newsTitle)
			newsTitle="";
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewSummary() {
		return newSummary;
	}

	public void setNewSummary(String newSummary) {
		this.newSummary = newSummary;
	}

	public int getNewReplyCount() {
		return newReplyCount;
	}

	public void setNewReplyCount(int newReplyCount) {
		this.newReplyCount = newReplyCount;
	}

	public String getNewsImg() {
		return newsImg;
	}

	public void setNewsImg(String newsImg) {
		this.newsImg = newsImg;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public int getNewsType() {
		return newsType;
	}

	public void setNewsType(int newsType) {
		this.newsType = newsType;
	}

	public int getNewsFrom() {
		return newsFrom;
	}

	public void setNewsFrom(int newsFrom) {
		this.newsFrom = newsFrom;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
