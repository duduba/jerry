package com.zxtd.information.manager;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zxtd.information.bean.ChangeDifferentNewsRequest;
import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.bean.CoverRequest;
import com.zxtd.information.bean.HotNewsRequest;
import com.zxtd.information.bean.InvitationCheckedRequest;
import com.zxtd.information.bean.InvitationReplyRequest;
import com.zxtd.information.bean.InvitationReportRequest;
import com.zxtd.information.bean.InvitationSupportRequest;
import com.zxtd.information.bean.ModifPwdRequest;
import com.zxtd.information.bean.ModifUserInfoRequest;
import com.zxtd.information.bean.ModifyPubNewsRequest;
import com.zxtd.information.bean.NetFriendItemTypeListRequest;
import com.zxtd.information.bean.EmptyRequest;
import com.zxtd.information.bean.NetFriendMenuListRequest;
import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.bean.PublishNewsRequest;
import com.zxtd.information.bean.PushNewsRequest;
import com.zxtd.information.bean.RegisterData;
import com.zxtd.information.bean.UserInfoBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.net.ProgressRequestCallBack;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.net.RequestComm;
import com.zxtd.information.net.ZXTD_HttpRequest;
import com.zxtd.information.parse.ChangeDifferentNewsParseData;
import com.zxtd.information.parse.CoverParseData;
import com.zxtd.information.parse.HotNewsListParseData;
import com.zxtd.information.parse.InvitationCheckedParseData;
import com.zxtd.information.parse.InvitationReplyParseData;
import com.zxtd.information.parse.InvitationReportParseData;
import com.zxtd.information.parse.InvitationSupportParseData;
import com.zxtd.information.parse.ModifyPubParseData;
import com.zxtd.information.parse.NetFriendItemTypeListParseData;
import com.zxtd.information.parse.NetFriendMenuListParseData;
import com.zxtd.information.parse.NetFriendNewsTypeParseData;
import com.zxtd.information.parse.NewsHeadlineParseData;
import com.zxtd.information.parse.NewsTypeParseData;
import com.zxtd.information.parse.ParseData;
import com.zxtd.information.parse.PublishNewsParseData;
import com.zxtd.information.parse.PushNewsParseData;
import com.zxtd.information.parse.RegisterParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.parse.doc.ModifUserInfoParseData;
import com.zxtd.information.parse.doc.ParseXmlToDocument;
import com.zxtd.information.ui.pub.PublishActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class RequestManager {
	private static RequestManager requestManager;
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private RequestManager(){
	}
	public static RequestManager newInstance(){
		if(requestManager == null){
			requestManager = new RequestManager();
		}
		return requestManager;
	}
	/**
	 * 第一次请求，其实包含了注册，新闻类型请求，头条请求，热门列表请求
	 * */
	public void firstComm( RegisterData registerData,final RequestCallBack requestCallBack, String noimg){
		//注册
		final RequestComm registerRequestComm = new RequestComm(registerData, Constant.RequestCode.REGISTER);
		final RegisterParseData registerParseData = new RegisterParseData();
		
		EmptyRequest emptyRequest = new EmptyRequest();
		HotNewsRequest newsRequest = new HotNewsRequest(noimg);
		//新闻类型
		final RequestComm newTypeRequestComm = new RequestComm(emptyRequest, Constant.RequestCode.NEWS_FIRST);
		final NewsTypeParseData newTypeparseData = new NewsTypeParseData();
		//网友新闻类型
		final RequestComm requestComm = new RequestComm(emptyRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_TYPE);
		final NetFriendNewsTypeParseData parseData = new NetFriendNewsTypeParseData();
		
		//头条
		final RequestComm headLineRequestComm = new RequestComm(emptyRequest, Constant.RequestCode.NEWS_LIST);
		final NewsHeadlineParseData headLineParseData = new NewsHeadlineParseData();
		//热门
		final RequestComm hotNewsRequestComm = new RequestComm(newsRequest, Constant.RequestCode.NEW_INFO);
		final HotNewsListParseData hotNewsListParseData = new HotNewsListParseData();
		
		executorService.execute(new Runnable() {
			
			public void run() {
				registerRequestComm.doPost(registerParseData, requestCallBack);
				newTypeRequestComm.doPost(newTypeparseData, requestCallBack);
				requestComm.doPost(parseData, requestCallBack);
				headLineRequestComm.doPost(headLineParseData, requestCallBack);
				hotNewsRequestComm.doPost(hotNewsListParseData, requestCallBack);
			}
		});
		
	}
	
	/**
	 * 注册信息
	 * */
	public void registerComm(RegisterData registerData, RequestCallBack requestCallBack){
		RequestComm requestComm = new RequestComm(registerData, Constant.RequestCode.REGISTER);
		RegisterParseData parseData = new RegisterParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 切换新闻类型
	 * @param id 操作id，用来排序
	 * */
	public void changeDifferentNewsComm(String id, String type, Integer typeIndex, String noimg, RequestCallBack requestCallBack, int state){
		ChangeDifferentNewsRequest differentNewsRequest = new ChangeDifferentNewsRequest(id, type, noimg);
		RequestComm requestComm = new RequestComm(differentNewsRequest, Constant.RequestCode.CHANGE_NEWS_INFO);
		ChangeDifferentNewsParseData parseData = new ChangeDifferentNewsParseData();
		parseData.addResult("typeIndex", typeIndex);
		parseData.addResult("state", state);
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 请求视频页面
	 * @param id 操作id，用来排序
	 * */
	public void videoNewsListComm(String id, String type, Integer typeIndex, String noimg, RequestCallBack requestCallBack, int state){
		ChangeDifferentNewsRequest differentNewsRequest = new ChangeDifferentNewsRequest(id, type, noimg);
		RequestComm requestComm = new RequestComm(differentNewsRequest, Constant.RequestCode.ZXTD_VIDEO_LIST);
		ChangeDifferentNewsParseData parseData = new ChangeDifferentNewsParseData();
		parseData.addResult("typeIndex", typeIndex);
		parseData.addResult("state", state);
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	
	
	/**
	 * 热门新闻列表
	 * */
	public void hotNewsListComm( RequestCallBack requestCallBack){
		EmptyRequest newsListRequest = new EmptyRequest();
		RequestComm requestComm = new RequestComm(newsListRequest, Constant.RequestCode.NEW_INFO);
		HotNewsListParseData hotNewsListParseData = new HotNewsListParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, hotNewsListParseData));
	}
	/**
	 * 请求新闻回复列表
	 * @param invitationId 楼层id
	 * */
	public void invitationCheckedComm(String newsId, String invitationId, String flag, RequestCallBack requestCallBack, int state){
		InvitationCheckedRequest checkedRequest = null;
		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
			checkedRequest = new InvitationCheckedRequest(newsId, invitationId, "1");
		}else if (Constant.Flag.VIDEO_TYPE.equals(flag)){
			checkedRequest = new InvitationCheckedRequest(newsId, invitationId, "0");
		}else{
			checkedRequest = new InvitationCheckedRequest(newsId, invitationId, "0");
		}
		RequestComm requestComm = new RequestComm(checkedRequest, Constant.RequestCode.ZXTD_CHECK_INVITATION);
		InvitationCheckedParseData parseData = new InvitationCheckedParseData();
		parseData.addResult("state", state);
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 对楼层的回复
	 * @param invitationId 楼层id
	 * */
	public void invitationReplyComm(String newsId,String invitationId,String nickname,String content,String flag, RequestCallBack requestCallBack){
		InvitationReplyRequest replyRequest = null;
		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
			replyRequest = new InvitationReplyRequest(newsId, invitationId, nickname, Utils.transformContent(content), "1");
			//requestComm = new RequestComm(replyRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_REPLY_INVITATION);
		}else if (Constant.Flag.VIDEO_TYPE.equals(flag)){
			//requestComm = new RequestComm(replyRequest, Constant.RequestCode.ZXTD_VIDEO_REPLY_INVITATION);
		}else{
			replyRequest = new InvitationReplyRequest(newsId, invitationId, nickname, Utils.transformContent(content), "0");
		}
		RequestComm requestComm = new RequestComm(replyRequest, Constant.RequestCode.ZXTD_REPLY_INVITATION);
		InvitationReplyParseData parseData = new InvitationReplyParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
		
		//评论数
		saveComment(newsId, nickname, invitationId, content, flag);
	}
	/**
	 * 对新闻的回复
	 * */
	public void invitationReportComm(String newsId,String nickname,String content,String flag, RequestCallBack requestCallBack){
		InvitationReportRequest request = null;
		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
			request = new InvitationReportRequest(newsId, nickname, Utils.transformContent(content), "1");
		}else if (Constant.Flag.VIDEO_TYPE.equals(flag)) {
			//requestComm = new RequestComm(request, Constant.RequestCode.ZXTD_VIDEO_REPORT_INVITATION);
		}else{
			//requestComm = new RequestComm(request, Constant.RequestCode.ZXTD_REPORT_INVITATION);
			request = new InvitationReportRequest(newsId, nickname, Utils.transformContent(content), "0");
		}
		RequestComm requestComm = new RequestComm(request, Constant.RequestCode.ZXTD_REPORT_INVITATION);
		InvitationReportParseData parseData = new InvitationReportParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
		
		//评论数
		saveComment(newsId, nickname, "0", content, flag);
	}
	
	/**
	 * 缓存评论数据
	 * @param newsId
	 * @param nickname
	 * @param invitationId
	 * @param content
	 * @param flag
	 */
	private void saveComment(String newsId,String nickname,String invitationId,String content,String flag){
		CommentBean bean=new CommentBean();
		bean.setNewsId(Integer.valueOf(newsId));
		bean.setNickName(nickname);
		bean.setLocation(invitationId);
		bean.setContent(content);
		bean.setType(Constant.Flag.NET_FRIEND_TYPE.equals(flag) ? "1" : "0");
		CommentManager.getInstance().doPublicComment(bean);
	}
	
	
	
	/**
	 * 顶贴
	 * @param invitationId 楼层id
	 * */
	public void invitationSupportComm(String newsId, String invitationId, String flag, RequestCallBack requestCallBack){
		InvitationSupportRequest supportRequest = new InvitationSupportRequest(newsId, invitationId, Constant.Flag.NET_FRIEND_TYPE.equals(flag) ? "1" : "0");
		RequestComm requestComm = new RequestComm(supportRequest, Constant.RequestCode.ZXTD_SUPPORT_INVITATION);
//		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
//			requestComm = new RequestComm(supportRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_SUPPORT_INVITATION);
//		}else if (Constant.Flag.VIDEO_TYPE.equals(flag)){
//			requestComm = new RequestComm(supportRequest, Constant.RequestCode.ZXTD_VIDEO_SUPPORT_INVITATION);
//		}else{
//			requestComm = new RequestComm(supportRequest, Constant.RequestCode.ZXTD_SUPPORT_INVITATION);
//		}
		InvitationSupportParseData parseData = new InvitationSupportParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	
	/**
	 * 请求网友，某类型的列表
	 * @param id 操作id，用来排序
	 * */
	public void netFriendItemTypeListComm(String id, String type, String posterId, RequestCallBack requestCallBack, int state){
		NetFriendItemTypeListRequest itemTypeListRequest = new NetFriendItemTypeListRequest(id, type, posterId);
		RequestComm requestComm = new RequestComm(itemTypeListRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_ITEM_LIST);
		NetFriendItemTypeListParseData parseData = new NetFriendItemTypeListParseData();
		parseData.addResult("state", state);
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 请求网友列表
	 * @param id 操作id，用来排序
	 * */
	public void netFriendMenuListComm(String id, String type, String posterId, RequestCallBack requestCallBack, int state){
		NetFriendMenuListRequest menuListRequest = new NetFriendMenuListRequest(id, type, posterId);
		RequestComm requestComm = new RequestComm(menuListRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_MENU);
		NetFriendMenuListParseData parseData = new NetFriendMenuListParseData();
		parseData.addResult("state", state);
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	
	/**
	 * 请求新闻发布类型列表
	 * */
	public void netFriendNewsTypeComm(RequestCallBack requestCallBack){
		EmptyRequest newsTypeRequest = new EmptyRequest();
		RequestComm requestComm = new RequestComm(newsTypeRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS_TYPE);
		NetFriendNewsTypeParseData parseData = new NetFriendNewsTypeParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 请求头条列表
	 * */
	public void newsHeadlineComm(RequestCallBack requestCallBack){
		EmptyRequest newsHeadlineRequest = new EmptyRequest();
		RequestComm requestComm = new RequestComm(newsHeadlineRequest, Constant.RequestCode.NEWS_LIST);
		NewsHeadlineParseData parseData = new NewsHeadlineParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	/**
	 * 请求新闻类型列表
	 * */
	public void newsTypeComm(RequestCallBack requestCallBack){
		EmptyRequest newsHeadlineRequest = new EmptyRequest();
		RequestComm requestComm = new RequestComm(newsHeadlineRequest, Constant.RequestCode.NEWS_FIRST);
		NewsTypeParseData parseData = new NewsTypeParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	
	/**
	 * 发布新闻
	 * */
	private void publishNewsComm(String title, String nickname, String content,String imageUrl, String type,String userId, ProgressRequestCallBack requestCallBack, Handler handler){
		PublishNewsRequest publishNewsRequest = new PublishNewsRequest(Utils.transformContent(title), Utils.transformContent(nickname), Utils.transformContent(content), imageUrl, type, userId);
		RequestComm requestComm = new RequestComm(publishNewsRequest, Constant.RequestCode.ZXTD_PUBLISH_NEWS);
		PublishNewsParseData parseData = new PublishNewsParseData();
		parseData.addResult("pubTitle", title);
		parseData.addResult("pubContent", content);
		requestComm.doPost(parseData, requestCallBack,handler);
	}
	
	private void modifyPubNewsComm(String title, String nickname, String content,String imageUrl, String type,String pubId, ProgressRequestCallBack requestCallBack, Handler handler ){
		ModifyPubNewsRequest newsRequest = new ModifyPubNewsRequest(title, content, imageUrl, type, pubId);
		RequestComm requestComm = new RequestComm(newsRequest, Constant.RequestCode.MINE_MODIFY_PUB);
		ModifyPubParseData parseData = new ModifyPubParseData();
		requestComm.doPost(parseData, requestCallBack,handler);
	}
	
	/**
	 * 发布新闻
	 * */
	public void publishNewsComm(PublishBean publishBean, int mode, ProgressRequestCallBack requestCallBack, Handler handler){
		executorService.execute(new PublishRunnable(publishBean, requestCallBack, handler, mode));
	}
	/**
	 * 推送消息
	 * */
	public void pushNewsComm(String id, RequestCallBack requestCallBack){
		PushNewsRequest newsRequest = new PushNewsRequest(id);
		RequestComm requestComm = new RequestComm(newsRequest, Constant.RequestCode.ZXTD_PUSH_NEWS);
		PushNewsParseData parseData = new PushNewsParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, parseData));
	}
	
	/**
	 * 封面请求
	 * */
	public void CoverComm(String id, String width, String high, RequestCallBack requestCallBack){
		CoverRequest coverRequest = new CoverRequest(id, width, high);
		RequestComm requestComm = new RequestComm(coverRequest, Constant.RequestCode.ZXTD_COVER);
		CoverParseData coverParseData = new CoverParseData();
		executorService.execute(new RequestRunnable(requestComm, requestCallBack, coverParseData));
	}
	/**
	 * 修改用户资料
	 * */
	public void modifUserInfoComm(UserInfoBean userInfoBean, RequestCallBack callBack){
		ModifUserInfoParseData parseData = new ModifUserInfoParseData();
		ModifUserInfoRequest userInfoRequest = new ModifUserInfoRequest();
		userInfoRequest.userid = userInfoBean.userid;
		userInfoRequest.userAccount = userInfoBean.userAccount;
		userInfoRequest.nickname = userInfoBean.nickname;
		userInfoRequest.aspiration = userInfoBean.aspiration;
		userInfoRequest.birthday = userInfoBean.birthday;
		userInfoRequest.location = userInfoBean.location;
		userInfoRequest.profession = userInfoBean.profession;
		userInfoRequest.sex = userInfoBean.sex;
		RequestComm requestComm = new RequestComm(userInfoRequest, Constant.RequestCode.MINE_MODIF_USERINFO);
		executorService.execute(new RequestDocRunnable(requestComm, parseData, callBack));
	}
	/**
	 * 修改密码
	 * @param userId
	 * @param oldPwd
	 * @param newPwd
	 * @param callBack
	 */
	public void modifPwdComm(String userId, String oldPwd, String newPwd, RequestCallBack callBack){
		ModifUserInfoParseData parseData = new ModifUserInfoParseData();
		ModifPwdRequest modifPwdRequest = new ModifPwdRequest(userId, oldPwd, newPwd);
		RequestComm requestComm = new RequestComm(modifPwdRequest, Constant.RequestCode.MINE_MODIF_PASSWORD);
		executorService.execute(new RequestDocRunnable(requestComm, parseData, callBack));
	}
	
	public void modifPortraitComm(String userId, Bitmap bitmap, RequestCallBack callBack){
		executorService.execute(new UpLoadImage(bitmap, userId, callBack, new Handler()));
	}
	
	
	private class RequestDocRunnable implements Runnable{
		private RequestCallBack mRequestCallBack;
		private RequestComm mRequestComm;
		private ParseXmlToDocument mXmlToDocument;
		public RequestDocRunnable(RequestComm requestComm, ParseXmlToDocument xmlToDocument, RequestCallBack mCallBack){
			mRequestComm = requestComm;
			mXmlToDocument = xmlToDocument;
			mRequestCallBack = mCallBack;
		}
		@Override
		public void run() {
			mRequestComm.doPost(mXmlToDocument, mRequestCallBack);
		}
	}
	
	private class RequestRunnable implements Runnable{
		private RequestComm mRequestComm;
		private RequestCallBack mRequestCallBack;
		private ParseData mParseData;
		private RequestRunnable(RequestComm requestComm, RequestCallBack requestCallBack, ParseData parseData){
			mRequestComm = requestComm;
			mRequestCallBack = requestCallBack;
			mParseData = parseData;
		}
		public void run() {
			mRequestComm.doPost(mParseData, mRequestCallBack);
		}
		
	}
	
	private class MessageRunnable implements Runnable{
		private String mMessage;
		private ProgressRequestCallBack mRequestCallBack;
		public MessageRunnable(String message, ProgressRequestCallBack requestCallBack){
			mMessage = message;
			mRequestCallBack = requestCallBack;
		}
		
		public void run() {
			mRequestCallBack.progress(Constant.RequestCode.ZXTD_PUBLISH_NEWS, mMessage);
		}
	}
	
	private class UpLoadImage implements Runnable{
		private Bitmap mBitmap;
		private String userId;
		private RequestCallBack callBack;
		private Handler handler;
		public UpLoadImage(Bitmap bitmap, String userId, RequestCallBack callBack, Handler handler){
			mBitmap = bitmap;
			this.userId = userId;
			this.callBack = callBack;
			this.handler = handler;
		}
		@Override
		public void run() {
			ZXTD_HttpRequest httpRequest = new ZXTD_HttpRequest(Constant.Url.ALBUMS_HEAD_REQUEST_URL + "?type=" + 0 + "&userid=" + userId);
			HashMap<String, String> params = new HashMap<String, String>();
			String url = httpRequest.uploadImages(mBitmap, params);
			if(url != null){
				final Result result = new Result();
				result.put("imageUrl", url);
				Utils.saveImageCache( url, mBitmap, zxtd_ImageCacheDao.Instance());
				Constant.loginUser.setHeader(url);
				handler.post(new Runnable() {
					@Override
					public void run() {
						callBack.requestSuccess(Constant.RequestCode.MINE_MODIF_PORTRAIT, result);
					}
				});
			}else{
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						callBack.requestError(Constant.RequestCode.MINE_MODIF_PORTRAIT, Constant.ResponseCode.NET_ERROR);
					}
				});
			}
		}
	}
	
	private class PublishRunnable implements Runnable{
		private PublishBean mPublishBean;
		private ProgressRequestCallBack mRequestCallBack;
		private Handler mHandler;
		private int mode;
		public PublishRunnable(PublishBean publishBean, ProgressRequestCallBack requestCallBack, Handler handler, int mode){
			mPublishBean = publishBean;
			mRequestCallBack = requestCallBack;
			mHandler = handler;
			this.mode = mode;
		}
		public void run() {
			StringBuffer stringBuffer = new StringBuffer();
			List<String> imagePaths = mPublishBean.resolveImageUrls();
			if(imagePaths != null){
				for(int i = 0; i < imagePaths.size(); i ++){
					String path = imagePaths.get(i);
					mHandler.post(new MessageRunnable("正在上传第" + (i + 1) + "张图片", mRequestCallBack));
					Log.i(this.getClass().getName(), "imageName --> " + path);
					String url = "";
					if(!path.startsWith("http:")){
						ZXTD_HttpRequest httpRequest = new ZXTD_HttpRequest(Constant.Url.IMAGE_REQUEST_URL);
						url = httpRequest.uploadImages(path, 500, 500);
					}else{
						url = path + ";";
					}
					stringBuffer.append(url);
				}
			}
			mHandler.post(new MessageRunnable("正在上传内容", mRequestCallBack));
			if(mode == PublishActivity.EDIT_MODE){
				publishNewsComm(mPublishBean.title, "", mPublishBean.content, stringBuffer.toString(), mPublishBean.type.id, XmppUtils.getUserId() + "", mRequestCallBack, mHandler);
			}else{
				modifyPubNewsComm(mPublishBean.title, "", mPublishBean.content, stringBuffer.toString(), mPublishBean.type.id, mPublishBean.newsId, mRequestCallBack, mHandler);
			}
			
		}
	}
}
