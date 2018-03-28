package com.zxtd.information.bean;


public class CommentBean {

	private int newsId;
	private int userId;
	private int commentId;
	private String title;//新闻标题
	private String content;//评论内容
	private String userImg;
	private String nickName;
	private String publicTime;//发布时间
	private String type;//新闻类型，0为新闻，1为网友发布
	private String origContent;//原评论类容
	private int origCommentId;//原评论ID
	private String infoUrl;//新闻详情链接
	private String location;
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPublicTime() {
		return publicTime;
	}
	public void setPublicTime(String publicTime) {
		this.publicTime = publicTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInfoUrl() {
		return infoUrl;
	}
	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
	public String getOrigContent() {
		return origContent;
	}
	public void setOrigContent(String origContent) {
		this.origContent = origContent;
	}
	public int getOrigCommentId() {
		return origCommentId;
	}
	public void setOrigCommentId(int origCommentId) {
		this.origCommentId = origCommentId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}