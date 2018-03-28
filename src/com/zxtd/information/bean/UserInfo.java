package com.zxtd.information.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

	private int userId=0;
	private String userAccount;
	private String password;
	private String nickName;
	private String header="";
	private String sign="";
	private String area="";
	private String work="";
	private String sex="";
	private String birth="";
	private String albums;
	
	private int fansCount=0;
	private int hasNewFans;
	private int attentionCount;
	private boolean hasAttention=false;
	
	private int publishCount;
	private int hasNewPublish;
	private List<NewImageBean> imgList=new ArrayList<NewImageBean>();
	
	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

	private int commentCount;
	private int hasNewComment;
	private int collectionCount;
	//private int hasNewCollection;
	private int letterCount;
	private int hasNewLetter;
	private int accountType;
	
	public UserInfo(){
		
	}
	
	public UserInfo(Parcel p){
		userId=p.readInt();
		userAccount=p.readString();
		nickName=p.readString();
		header=p.readString();
		sign=p.readString();
		area=p.readString();
		work=p.readString();
		sex=p.readString();
		birth=p.readString();
		albums=p.readString();
		
		fansCount=p.readInt();
		hasNewFans=p.readInt();
		attentionCount=p.readInt();
		//hasNewAttention=p.readInt();
		
		publishCount=p.readInt();
		hasNewPublish=p.readInt();
		commentCount=p.readInt();
		hasNewComment=p.readInt();
		collectionCount=p.readInt();
		//hasNewCollection=p.readInt();
		letterCount=p.readInt();
		hasNewLetter=p.readInt();
		accountType=p.readInt();
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel p, int arg1) {
		// TODO Auto-generated method stub
		p.writeInt(userId);
		p.writeString(userAccount);
		p.writeString(nickName);
		p.writeString(header);
		p.writeString(sign);
		p.writeString(area);
		p.writeString(work);
		p.writeString(sex);
		p.writeString(birth);
		p.writeString(albums);
		
		p.writeInt(fansCount);
		p.writeInt(hasNewFans);
		p.writeInt(attentionCount);
		//p.writeInt(hasNewAttention);
		
		p.writeInt(publishCount);
		p.writeInt(hasNewPublish);
		p.writeInt(commentCount);
		p.writeInt(hasNewComment);
		p.writeInt(collectionCount);
		//p.writeInt(hasNewCollection);
		p.writeInt(letterCount);
		p.writeInt(hasNewLetter);
		p.writeInt(accountType);
	}
	
	public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
		public UserInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new UserInfo[size];
		}
		
		public UserInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new UserInfo(source);
		}
	};
	

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

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getAlbums() {
		return albums;
	}

	public void setAlbums(String albums) {
		this.albums = albums;
	}

	public int getFansCount() {
		return fansCount;
	}

	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}

	/*
	public int isHasNewAttention() {
		return hasNewAttention;
	}

	public void setHasNewAttention(int hasNewAttention) {
		this.hasNewAttention = hasNewAttention;
	}*/

	public int getPublishCount() {
		return publishCount;
	}

	public void setPublishCount(int publishCount) {
		this.publishCount = publishCount;
	}

	public int getHasNewPublish() {
		return hasNewPublish;
	}

	public void setHasNewPublish(int hasNewPublish) {
		this.hasNewPublish = hasNewPublish;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int isHasNewComment() {
		return hasNewComment;
	}

	public void setHasNewComment(int hasNewComment) {
		this.hasNewComment = hasNewComment;
	}

	public int getCollectionCount() {
		return collectionCount;
	}

	public void setCollectionCount(int collectionCount) {
		this.collectionCount = collectionCount;
	}

	/*
	public int isHasNewCollection() {
		return hasNewCollection;
	}

	public void setHasNewCollection(int hasNewCollection) {
		this.hasNewCollection = hasNewCollection;
	}
	 */
	
	public int getLetterCount() {
		return letterCount;
	}

	public void setLetterCount(int letterCount) {
		this.letterCount = letterCount;
	}

	public int isHasNewLetter() {
		return hasNewLetter;
	}

	public void setHasNewLetter(int hasNewLetter) {
		this.hasNewLetter = hasNewLetter;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int isHasNewFans() {
		return hasNewFans;
	}

	public void setHasNewFans(int hasNewFans) {
		this.hasNewFans = hasNewFans;
	}

	public int getAttentionCount() {
		return attentionCount;
	}

	public void setAttentionCount(int attentionCount) {
		this.attentionCount = attentionCount;
	}

	public boolean isHasAttention() {
		return hasAttention;
	}

	public void setHasAttention(boolean hasAttention) {
		this.hasAttention = hasAttention;
	}

	public List<NewImageBean> getImgList() {
		return imgList;
	}

	public void setImgList(List<NewImageBean> imgList) {
		this.imgList = imgList;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
