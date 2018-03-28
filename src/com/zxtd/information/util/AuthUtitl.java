package com.zxtd.information.util;

import java.io.IOException;

import org.json.JSONObject;

import com.renn.rennsdk.RennClient;
import com.tencent.tauth.Tencent;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.util.AuthUtitl.OnAuthListener.AuthType;
import com.zxtd.information.util.ShareUtils.LoginCallback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AuthUtitl {
	public interface OnAuthListener {
		
		/**
		 * 
		 * @author Administrator
		 *<li>SINA 新浪微博
		 *<li>TENCENT 腾讯微博
		 *<li>RENN 人人网
		 *<li>QQ qq空间
		 */
		public enum AuthType{
			SINA, TENCENT, RENN, QQ
		}
		/**
		 * 认证失败
		 * @param type
		 */
		void fail(AuthType type);
		/**
		 * 认证成功
		 * @param type
		 */
		void success(AuthType type);
	}

	public static Handler handler = null;

	public static void showToast(String text) {
		Toast.makeText(ZXTD_Application.getMyContext(), text,
				Toast.LENGTH_SHORT).show();
	}

	public static void showAycToast(final String text) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ZXTD_Application.getMyContext(), text,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * sina oauth2.0 登陆
	 */
	public static void doSinaOAuthLogin(final Activity activity, Weibo weibo, final SsoHandler ssoHandler,
			final OnAuthListener authListener) {
		
		ShareUtils share=new ShareUtils(activity);
		 share.setCallBack(new LoginCallback(){
			@Override
			public void doSuccess(Object obj) {
				authListener.success(AuthType.SINA);
				Bundle values=(Bundle) obj;
				final String code = values.getString("code");
				final String token = values.getString("access_token");
				if(!TextUtils.isEmpty(token)){
			        String expires_in = values.getString("expires_in");
			        final String uid  = values.getString("uid");
					SharedPreferences pref = activity.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_APPEND);
	        		Editor editor = pref.edit();
	        		editor.putString("SINA_uid", uid);
	        		editor.putString("SINA_token",token );
	        		editor.putString("SINA_expires_in", expires_in);
	        		editor.commit();

				}else{
					if(!TextUtils.isEmpty(code)){
						new Thread(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();
								final WeiboParameters parameters=new WeiboParameters();
								parameters.add("client_id", Constant.CONSUMER_KEY);
								parameters.add("client_secret",Constant.SINA_KEY);
								parameters.add("grant_type", "authorization_code");
								parameters.add("redirect_uri", Constant.REDIRECT_URL);
								parameters.add("code", code);
								final String url="https://api.weibo.com/oauth2/access_token";
								AsyncWeiboRunner.request(url, parameters, "POST", new RequestListener(){
									@Override
									public void onComplete(String response) {
										Log.e("response", response);
										try{
											JSONObject json=new JSONObject(response);
											final String access_token = json.getString("access_token");
									        String expires_in = json.getString("expires_in");
									        final String uid  = json.getString("uid");
											SharedPreferences pref = activity.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_APPEND);
							        		Editor editor = pref.edit();
							        		editor.putString("SINA_uid", uid);
							        		editor.putString("SINA_token",access_token );
							        		editor.putString("SINA_expires_in", expires_in);
							        		editor.commit();
											
							
										}catch(Exception ex){
											Utils.printException(ex);
										}
									}
									@Override
									public void onIOException(IOException e) {
										// TODO Auto-generated method stub
										Log.e("IOException", e.getMessage());
									}

									@Override
									public void onError(WeiboException e) {
										// TODO Auto-generated method stub
										Log.e("WeiboException", e.getMessage());
									}
									
								});
							}
						}.start();
					}
				}
        		showAycToast("授权成功");
			}

			@Override
			public void doError(String errorMsg) {
				authListener.fail(AuthType.SINA);
				Log.e(Constant.TAG, errorMsg);
				showAycToast("授权失败");
			}
		 });
		 share.doSinaLogin(weibo,ssoHandler);
	}

	// 腾讯微博认证
	public static void doTencentOAuthLogin(Activity activity,
			final OnAuthListener authListener) {
		ShareUtils shareUtils = new ShareUtils(activity);
		shareUtils.setCallBack(new LoginCallback() {

			@Override
			public void doSuccess(Object obj) {
				authListener.success(AuthType.TENCENT);
			}

			@Override
			public void doError(String errorMsg) {
				showToast(errorMsg);
				authListener.fail(AuthType.TENCENT);
			}
		});
		shareUtils.doTencentLogin();
	}

	// 人人登陆认证
	public static void doRennOAuthLogin(Activity activity,
			final OnAuthListener authListener) {
		ShareUtils shareUtils = new ShareUtils(activity);
		shareUtils.setCallBack(new LoginCallback() {

			@Override
			public void doSuccess(Object obj) {
				authListener.success(AuthType.RENN);
				showAycToast("授权成功");
			}

			@Override
			public void doError(String errorMsg) {
				showAycToast(errorMsg);
				authListener.fail(AuthType.RENN);
				showAycToast("授权失败");
			}
		});
		shareUtils.doRenRenLogin();
	}

	// QQ登陆认证
	public static void doQQLogin(Tencent tencent ,final Activity activity,
			final OnAuthListener authListener) {
		ShareUtils shareUtils = new ShareUtils(activity);
		shareUtils.setCallBack(new LoginCallback() {

			@Override
			public void doSuccess(Object obj) {
				try {
					JSONObject values = (JSONObject) obj;
					SharedPreferences shareds = activity.getSharedPreferences(
							Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
					Editor editor = shareds.edit();
					editor.putString("QQ_ret", values.getString("ret"));
					editor.putString("QQ_pay_token",
							values.getString("pay_token"));
					editor.putString("QQ_pf", values.getString("pf"));
					editor.putString("QQ_expires_in",
							values.getString("expires_in"));
					editor.putString("QQ_openid", values.getString("openid"));
					editor.putString("QQ_pfkey", values.getString("pfkey"));
					editor.putString("QQ_access_token",
							values.getString("access_token"));
					editor.commit();
				} catch (Exception ex) {
					Utils.printException(ex);
				}
				authListener.success(AuthType.QQ);
				showAycToast("授权成功");
			}

			@Override
			public void doError(String errorMsg) {
				showToast(errorMsg);
				authListener.fail(AuthType.QQ);
				showAycToast("授权失败");
			}
		});
		shareUtils.doQQLogin(tencent);
	}
	//检测新浪微博
	public static boolean checkSinaAuth(Context context){
		
		SharedPreferences shareds=context.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
		String accessToken = shareds.getString("SINA_token", null);
		String expiresIn = shareds.getString("SINA_expires_in", null);
		Oauth2AccessToken accessToken2 = new Oauth2AccessToken();
		accessToken2.setToken(accessToken);
		if(!Utils.isEmpty(expiresIn)){
			accessToken2.setExpiresIn(expiresIn);
		}
		return accessToken2.isSessionValid();

	}
	//检测腾讯微博
	public static boolean checkTencentAuth(Context context){
		String accessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
		return !Utils.isEmpty(accessToken);
	}
	//检测人人网
	public static boolean checkRennAuth(Context context){
		RennClient client = RennClient.getInstance(context);
		return client.isLogin();
	}
	
	public static boolean checkQQAuth(Context context){
		SharedPreferences shareds= context.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
		String token = shareds.getString("QQ_access_token", null);
//		String expires_in = shareds.getString("QQ_expires_in", null);
//		String openId = shareds.getString("QQ_openid", null);
//		Tencent tencent = Tencent.createInstance(Constant.APP_ID, context);
		return !Utils.isEmpty(token);
	}
}
