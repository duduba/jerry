package com.zxtd.information.util;

import java.io.File;

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutFeedParam;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import com.zxtd.information.application.ZXTD_Application;


public class ShareUtils {

	private Activity activity;
	private LoginCallback callBack;

	public void setCallBack(LoginCallback callBack) {
		this.callBack = callBack;
	}

	public interface LoginCallback {
		public void doSuccess(Object obj);

		public void doError(String errorMsg);
	}

	public ShareUtils(Activity act) {
		this.activity = act;
	}

	/**
	 * QQ登陆
	 */
	public void doQQLogin(final Tencent mTencent) {
		if (!mTencent.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				public void onComplete(JSONObject arg0) {
					// TODO Auto-generated method stub
					super.onComplete(arg0);
					try {
						boolean ready = mTencent.isSessionValid()
								&& mTencent.getOpenId() != null;
						if (ready) {
							callBack.doSuccess(arg0);
						} else {
							callBack.doError("请先登录获取openId");
						}
					} catch (Exception ex) {
						Utils.printException(ex);
					}
				}
			};
			mTencent.login(activity, Constant.SCOPE, listener);
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(JSONObject arg0) {
			
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * 登录新浪 OAUTH2.0
	 * 
	 * @param sinaweibo
	 */
	public void doSinaLogin(final Weibo sinaweibo,SsoHandler mSsoHandler) {
		/*sinaweibo.setupConsumerConfig(Constant.CONSUMER_KEY, Constant.SINA_KEY);
		sinaweibo.setRedirectUrl(Constant.REDIRECT_URL);
		sinaweibo.authorize(activity, new AuthDialogListener());
		*/
		
        mSsoHandler.authorize(new AuthDialogListener());
	}
	
	
    class AuthDialogListener implements WeiboAuthListener{
        @Override
        public void onComplete(Bundle values) {
        	String code = values.getString("code");
        	String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            Log.e(Constant.TAG, "ttttttttttttttttttttttttt: "+token+"      expires_in:"+expires_in);
            if(!TextUtils.isEmpty(token)){
            	 Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
                 if (accessToken.isSessionValid()){
                     Toast.makeText(activity, "认证成功", Toast.LENGTH_SHORT).show();
                     callBack.doSuccess(values);
                 }
            }else{
            	if(code != null){
            		callBack.doSuccess(values);
    	        	return;
            	}
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(activity,
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            callBack.doError(e.getMessage());
        }

        @Override
        public void onCancel() {
        	callBack.doError("Auth cancel");
            Toast.makeText(activity, "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	callBack.doError(e.getMessage());
            Toast.makeText(activity,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

	/*
	private class AuthDialogListener implements WeiboDialogListener {
		public void onComplete(Bundle values) {
			callBack.doSuccess(values);
		}

		public void onError(DialogError e) {
			callBack.doError("Auth error : " + e.getMessage());
		}

		public void onCancel() {
			callBack.doError("Auth cancel");
		}

		public void onWeiboException(WeiboException e) {
			callBack.doError("Auth exception : " + e.getMessage());
		}

	}*/

	/**
	 * renren
	 */
	public void doRenRenLogin() {
		final RennClient rennClient = RennClient.getInstance(activity);
		if(!rennClient.isLogin()){
			rennClient.init(Constant.RENREN_APPID, Constant.RENREN_APPKEY, Constant.RENREN_SECRETKEY);
			rennClient.setScope("read_user_blog read_user_photo read_user_status read_user_album "
					+ "read_user_comment read_user_share publish_blog publish_share "
					+ "send_notification photo_upload status_update create_album "
					+ "publish_comment publish_feed");
			rennClient.setTokenType("bearer");
			rennClient.setLoginListener(new LoginListener() {
				
				@Override
				public void onLoginSuccess() {
					callBack.doSuccess(rennClient);
				}
				
				@Override
				public void onLoginCanceled() {
					callBack.doError("取消登录！");
				}
			});
			rennClient.login(activity);
		}else{
			callBack.doSuccess(rennClient);
		}
		
	}
	/**
	 * 腾讯微博
	 * 
	 */
	public void doTencentLogin() {
		final Context context = activity.getApplicationContext();
		AuthHelper.register(activity, Constant.TENCENT_APP_ID, Constant.TENCENT_APP_SECKET, new OnAuthListener() {

			@Override
			public void onWeiBoNotInstalled() {
				Toast.makeText(activity, "onWeiBoNotInstalled", 1000)
						.show();
				Intent i = new Intent(activity,Authorize.class);
				activity.startActivityForResult(i, 1000);
			}

			@Override
			public void onWeiboVersionMisMatch() {
				Toast.makeText(activity, "onWeiboVersionMisMatch",
						1000).show();
				Intent i = new Intent(activity,Authorize.class);
				activity.startActivity(i);
			}

			@Override
			public void onAuthFail(int result, String err) {
				Toast.makeText(activity, "result : " + result, 1000)
						.show();
				callBack.doError("授权失败");
			}

			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				
				Util.saveSharePersistent(context, "ACCESS_TOKEN", token.accessToken);
				Util.saveSharePersistent(context, "EXPIRES_IN", String.valueOf(token.expiresIn));
				Util.saveSharePersistent(context, "OPEN_ID", token.openID);
				Util.saveSharePersistent(context, "REFRESH_TOKEN", "");
				Util.saveSharePersistent(context, "CLIENT_ID", Util.getConfig().getProperty("APP_KEY"));
				Util.saveSharePersistent(context, "AUTHORIZETIME",
				String.valueOf(System.currentTimeMillis() / 1000l));
				callBack.doSuccess(token);
			}
		});

		AuthHelper.auth(activity, "");
	} 
	/**
	 * 微信或朋友圈分享
	 * 
	 * @param scene
	 * <li> SendMessageToWX.Req.WXSceneTimeline&nbsp;&nbsp;分享到朋友圈
	 * <li> SendMessageToWX.Req.WXSceneSession&nbsp;&nbsp;分享到微信
	 */
	public static boolean doWXShare(IWXAPI iwxapi, int scene,String title, String description, String imgPath, String infoUrl){
		
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = infoUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;
		Bitmap thumb = null;
		if(Utils.isEmpty(imgPath)){
			thumb = BitmapFactory.decodeResource(ZXTD_Application.getMyContext().getResources(), com.zxtd.information.ui.R.drawable.ic_news);
		}else{
			File file = new File(imgPath);
			if (!file.exists()) {
				thumb = BitmapFactory.decodeResource(ZXTD_Application.getMyContext().getResources(), com.zxtd.information.ui.R.drawable.ic_news);
			}else{
				Bitmap bmp = BitmapFactory.decodeFile(imgPath);
				thumb = Bitmap.createScaledBitmap(bmp, 60, 60, true);
				bmp.recycle();
			}
			
		}
		 
		msg.thumbData = Utils.bmpToByteArray(thumb, true);
		
		//IWXAPI iwxapi = WXAPIFactory.createWXAPI(ZXTD_Application.getMyContext(), Constant.WECHAT_APP_ID, false);
		iwxapi.registerApp(Constant.WECHAT_APP_ID);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = scene;
		
		return iwxapi.sendReq(req);
	}
	/**
	 * 新浪微博分享
	 * @param accessToken
	 * @param content
	 * 
	 */
	
	public static void doSinaShare(Weibo weibo, String content, String imge, final RequestListener listener){
		final WeiboParameters bundle = new WeiboParameters();
        bundle.add("access_token", weibo.accessToken.getToken());
        bundle.add("status", content);
        String url = null;
        if(Utils.isEmpty(imge)){
        	url = "https://api.weibo.com/2/statuses/update.json";
        }else{
        	bundle.add("pic", imge);
        	url = "https://api.weibo.com/2/statuses/upload.json";
        }
        final String weiboUrl = url;
        new Thread(){
        	
        	public void run() {
        		AsyncWeiboRunner.request(weiboUrl, bundle, "POST", listener);
        		
        	};
        }.start();
        
	}
	
	/**
	 * 腾讯微博分享
	 * @param accessToken
	 * @param content
	 * @param callBack
	 */
	public static void doTencentShare(String accessToken, String content, String pic, HttpCallback callBack){
		AccountModel model = new AccountModel(accessToken);
		WeiboAPI api = new WeiboAPI(model);
		Location location = Util.getLocation(ZXTD_Application.getMyContext());
		double longitude = 0d;
		double latitude = 0d;
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
		}
		if(Utils.isEmpty(pic)){
			api.addWeibo(ZXTD_Application.getMyContext(), content, "json", longitude, latitude, 0, 0, callBack, null, BaseVO.TYPE_JSON);
		}else{
			api.addPicUrl(ZXTD_Application.getMyContext(), content, "json", longitude, latitude, pic, 0, 0, callBack, null, BaseVO.TYPE_JSON);
		}
		
	}
	
	/**
	 * 人人网分享
	 * @param client
	 * @param title
	 * @param message
	 * @param description
	 * @param url
	 * @param callback
	 */
	public static void doRenRenShare(RennClient client,String title, String newInfoUrl, String description, String message, String imageUrl, CallBack callback ){
		PutFeedParam param = new PutFeedParam();
		if(Utils.isEmpty(message) || Utils.isEmpty(message.trim())){
			message = "分享来自杂色";
		}
		param.setMessage(message);
		param.setTitle(title);
		param.setDescription(description);
		param.setActionName("杂色");
		param.setActionTargetUrl("http://www.zxtd.net/product.html");
		if(!Utils.isEmpty(imageUrl)){
			param.setImageUrl(imageUrl);
		}
		param.setTargetUrl(newInfoUrl);
		try {
			client.getRennService().sendAsynRequest(param, callback);
		} catch (RennException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * QQ空间分享
	 * @param tencent
	 * @param title
	 * @param url
	 * @param summary
	 * @param listener
	 */
	public static void doQQShare(Tencent tencent, String title, String url, String summary, String comment, String imgUrl, IRequestListener listener){
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("url", url);
		bundle.putString("summary", comment);
		bundle.putString("type", "4");
		bundle.putString("site", "杂色");
		if(!Utils.isEmpty(imgUrl)){
			bundle.putString("images", imgUrl);
		}
		bundle.putString("fromurl", "http://www.zxtd.net/index.html");
		tencent.requestAsync(Constants.GRAPH_ADD_SHARE, bundle, Constants.HTTP_POST, listener, null);
	}
}
