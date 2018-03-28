package com.zxtd.information.ui.me;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetUserParam;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import com.zxtd.information.adapter.AutoTextViewAdapter;
import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.LoginManager;
import com.zxtd.information.manager.MyCollectionManager;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.manager.RegistManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.MainActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ResponseParam;
import com.zxtd.information.util.ShareUtils;
import com.zxtd.information.util.ShareUtils.LoginCallback;
import com.zxtd.information.util.Utils;

public class LoginActivity extends BaseActivity implements OnClickListener{

	private Tencent mTencent;
	private Weibo sinaweibo;
    private static Oauth2AccessToken accessToken;
    private AutoCompleteTextView editMail;
    private EditText editPwd;
    private ProgressDialog dialog;
    private AutoTextViewAdapter adapter;
    private static final int STATE_USER_INFO = 1;
	
   // private Renren rr;
    private MineModifyPwdDialog modifyDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hiddleTitleBar();
		setContentView(R.layout.mine_login);
		initView();
	}

	void initView(){
		LinearLayout inputLayout=(LinearLayout) findViewById(R.id.login_input_layout);
		inputLayout.getBackground().setAlpha(100);
		
		Button btnBack=(Button) findViewById(R.id.back);
		String from=this.getIntent().getStringExtra("from");
		if("main".equals(from)){
			btnBack.setVisibility(View.GONE);
		}else{
			btnBack.setVisibility(View.VISIBLE);
			btnBack.setOnClickListener(this);
		}
		
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText("用户登录");
		
		LinearLayout qqLayout=(LinearLayout) findViewById(R.id.qq_login_layout);
		qqLayout.setOnClickListener(this);
		LinearLayout renrenLayout=(LinearLayout) findViewById(R.id.renren_login_layout);
		renrenLayout.setOnClickListener(this);
		LinearLayout sinaLayout=(LinearLayout) findViewById(R.id.sina_login_layout);
		sinaLayout.setOnClickListener(this);
		
		editMail=(AutoCompleteTextView) findViewById(R.id.edit_email);
		editMail.setThreshold(1);
		adapter = new AutoTextViewAdapter(this);
		editMail.setAdapter(adapter);
		
		editMail.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				String input = s.toString();  
		        adapter.mList.clear();  
		        autoAddEmails(input);
		        adapter.notifyDataSetChanged();  
		        editMail.showDropDown();  
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
		});
		
		editPwd=(EditText) findViewById(R.id.edit_pwd);
		editToHideKeyBoard(editPwd);

		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_regist).setOnClickListener(this);
		findViewById(R.id.link_forgetpwd).setOnClickListener(this);
	}

	
	/**
	 * 自动填充邮箱列表
	 * @param input
	 */
	private void autoAddEmails(String input) {
		String autoEmail = "";
        if (input.length() > 0) {  
            for (int i = 0; i < Constant.EMAIL_SUFFIX.length; ++i) {  
            	if(input.contains("@")) {//包含“@”则开始过滤
            		String filter = input.substring(input.indexOf("@") + 1 , input.length());//获取过滤器，即根据输入“@”之后的内容过滤出符合条件的邮箱
            		if(Constant.EMAIL_SUFFIX[i].contains(filter)) {//符合过滤条件
            			autoEmail = input.substring(0, input.indexOf("@")) + Constant.EMAIL_SUFFIX[i];//用户输入“@”之前的内容加上自动填充的内容即为最后的结果
            			adapter.mList.add(autoEmail);
            		}
            	}else {
            		autoEmail = input + Constant.EMAIL_SUFFIX[i];
            		adapter.mList.add(autoEmail);
            	}
            }  
        } 
	}
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.qq_login_layout:{
				doQQLogin();
			}break;
			case R.id.sina_login_layout:{
				//doSinaSSOLogin();
				doSinaOAuthLogin();
			}break;
			case R.id.renren_login_layout:{
				doRenRenLogin();
			}break;
			case R.id.btn_login:{
				doZaseLogin();
			}break;
			case R.id.btn_regist:{
				Intent intent=new Intent(this,RegistActivity.class);
				startActivity(intent);
			}break;
			case R.id.link_forgetpwd:{
				if(null==modifyDialog){
					modifyDialog=new MineModifyPwdDialog(this);
				}
				modifyDialog.show();
			}break;
		}
	}
	
	
	/**
	 * 杂色账号登陆
	 */
	void doZaseLogin(){
		final String email=editMail.getText().toString().trim();
		final String password=editPwd.getText().toString().trim();
		final int accountType=0;
		if(TextUtils.isEmpty(email)){
			Toast("请输入你的杂色账号");
			return;
		}
		if(TextUtils.isEmpty(password)){
			Toast("请输入你的账号密码");
			return;
		}
		String regex="^[a-zA-Z0-9][\\w]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern pattern=Pattern.compile(regex);
		if(!pattern.matcher(email).matches()){
			Toast("输入的电子邮件地址不合法");
			return;
		}
		
		dialog=showProgress(-1, "", "登陆中...");
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				UserInfo info=LoginManager.getInstance().doLogin(email, password, accountType);
				Message msg=handler.obtainMessage();
				if(null==info){
					msg.what=-1;
				}else{
					msg.what=1;
					msg.obj=info;
				}
				msg.sendToTarget();
			}
		}.start();
		
	}
	
	
	@SuppressLint("HandlerLeak")
	private  Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					LoginActivity.this.checkNerWork();
				}break;
				case 1:{
					hideDialog();
					UserInfo info=(UserInfo) msg.obj;
					if(info.getUserId()!=0){
						notifyLoginAndSaveUserId(info.getUserId());
					}else{
						Toast("用户名或密码错误");
					}
				}break;
				case 2:{
					hideDialog();
					ResponseParam param=(ResponseParam) msg.obj;
				    if(param.isSuccess()){
				    	notifyLoginAndSaveUserId(param.getReturnId());
				    }else{
				    	String reason="登录失败";
				    	if("0".equals(param.getFailureReason())){
				    		reason+=",账号已被注册.";
				    	}
				    	Toast(reason);
				    }
				}break;
				case 3:{
					Toast("初始化版本信息出错");
					finishLogin();
				}break;
				case 4:{
					finishLogin();
				}break;
				case 5:{
					Toast("获取服务端版本出错");
					finishLogin();
				}break;
				case 6:{
					Toast(msg.obj.toString());
					finishLogin();
				}break;
			}
			hideDialog();
		}
	};
	
	
	private void hideDialog(){
		if(null!=dialog && dialog.isShowing()){
			dialog.dismiss();
		}
	}
	
	private void finishLogin(){
		Intent intent=new Intent(Constant.UPDATE_MINE_TABHOST_ITEM);
		intent.putExtra("action","login");
		sendBroadcast(intent);
		String from=getIntent().getStringExtra("from");
		if(!TextUtils.isEmpty(from) && !from.equals("main")){
			setResult(RESULT_OK);
			finish();
		}
	}
	
	
	private void notifyLoginAndSaveUserId(int userId){
		if(null!=Constant.loginUser){
			Constant.loginUser.setUserId(userId);
		}
		SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor=shareds.edit();
		editor.putInt("userId", userId);
		editor.commit();
		
		//同步数据
		dialog=showProgress(-1, "","正在同步数据..");
		dialog.show();
		new Thread(){
			@Override
			public void run(){
				if(CacheManager.getInstance().initVersions()){
					boolean flag=CacheManager.getInstance().getVersions();
					CacheManager.getInstance().getLocalVersion();
					if(flag && !CacheManager.LOCALVERSION.isEmpty()){
						//同步发布数据
						int client_public_version=CacheManager.LOCALVERSION.get(Constant.PUBLIC_VERSION_KEY);
						int server_public_version=CacheManager.CACHEVERSION.get(Constant.PUBLIC_VERSION_KEY);
						if(client_public_version<server_public_version){
							//删除手机上发布数据
							boolean isSuccess=PublicManager.getInstance().clearPublic();
							if(isSuccess){
								//获取所有服务端发布数据 插入本地数据库
								isSuccess=PublicManager.getInstance().synchronizedData();
								//更新本地版本号
								if(isSuccess){
									isSuccess=CacheManager.getInstance().modifyVersion(Constant.PUBLIC_VERSION_KEY, server_public_version);
									if(!isSuccess){
										handlerError("同步发布版本号出错");
									}
								}else{
									handlerError("同步发布数据出错");
								}
							}else{
								handlerError("清空本地发布库出错");
							}
						}
						//同步收藏数据
						int client_collection_version=CacheManager.LOCALVERSION.get(Constant.COLLECTION_VERSION_KEY);
						int server_collection_version=CacheManager.CACHEVERSION.get(Constant.COLLECTION_VERSION_KEY);
						if(client_collection_version<server_collection_version){
							//删除手机上发布数据
							boolean isSuccess=MyCollectionManager.getInstance().clearCollection();
							if(isSuccess){
								//获取所有服务端收藏数据 插入本地数据库
								isSuccess=MyCollectionManager.getInstance().synchronizedData();
								//更新本地版本号
								if(isSuccess){
									isSuccess=CacheManager.getInstance().modifyVersion(Constant.COLLECTION_VERSION_KEY, server_collection_version);
									if(!isSuccess){
										handlerError("同步收藏版本号出错");
									}
								}else{
									handlerError("同步收藏数据出错");
								}
							}else{
								handlerError("清空本地收藏库出错");
							}
						}
						handler.sendEmptyMessage(4);
					}else{
						handler.sendEmptyMessage(5);
					}	
				}else{
					handler.sendEmptyMessage(3);
				}
				
			}
		}.start();
	}
	
	
	private void handlerError(String error){
		Message msg=handler.obtainMessage();
		msg.what=6;
		msg.obj=error;
		msg.sendToTarget();
	}
	
	
	/**
	 * QQ登陆
	 */
	void doQQLogin(){
		/*
		mTencent = Tencent.createInstance(Constant.APP_ID, this);
		if (!mTencent.isSessionValid()) {
			IUiListener listener=new BaseUiListener(){
				@Override
				protected void doComplete(JSONObject values) {
					// TODO Auto-generated method stub
					super.doComplete(values);
					SharedPreferences shareds=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
					Editor editor=shareds.edit();
					try{
						editor.putString("QQ_ret", values.getString("ret"));
						editor.putString("QQ_pay_token", values.getString("pay_token"));
						editor.putString("QQ_pf", values.getString("pf"));
						editor.putString("QQ_expires_in", values.getString("expires_in"));
						editor.putString("QQ_openid", values.getString("openid"));
						editor.putString("QQ_pfkey", values.getString("pfkey"));
						editor.putString("QQ_access_token", values.getString("access_token"));
						editor.commit();
						boolean ready = mTencent.isSessionValid()&& mTencent.getOpenId() != null;
						if(ready) {
					        mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
					                  Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", false), null);
					    }else{
					    	Toast("请先登录获取openId");
					    }
					}catch(Exception ex){
						Utils.printException(ex);
					}
					
				}
			};
			mTencent.login(this, Constant.SCOPE, listener);
        }else {
	            mTencent.logout(this);
	        }
		*/
		
		//公用接口
		mTencent = Tencent.createInstance(Constant.APP_ID, this);
		ShareUtils share=new ShareUtils(this);
		share.setCallBack(new LoginCallback(){
			@Override
			public void doSuccess(Object obj) {
				// TODO Auto-generated method stub
				try{
					/*
					JSONObject values=(JSONObject) obj;
					SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
					Editor editor=shareds.edit();
					editor.putString("QQ_ret", values.getString("ret"));
					editor.putString("QQ_pay_token", values.getString("pay_token"));
					editor.putString("QQ_pf", values.getString("pf"));
					editor.putString("QQ_expires_in", values.getString("expires_in"));
					editor.putString("QQ_openid", values.getString("openid"));
					editor.putString("QQ_pfkey", values.getString("pfkey"));
					editor.putString("QQ_access_token", values.getString("access_token"));
					editor.commit();
					*/
					mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
			                  Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", false), null);
				}catch(Exception ex){
					Utils.printException(ex);
				}
		    }

			@Override
			public void doError(String errorMsg) {
				// TODO Auto-generated method stub
				Toast(errorMsg);
			}
		});
		share.doQQLogin(mTencent);
	}

	
	/**
	 * renren
	 */
	private void doRenRenLogin(){
		
//		if(null==rr){
//			rr = new Renren(Constant.RENREN_APPKEY, Constant.RENREN_SECRETKEY, Constant.RENREN_APPID, LoginActivity.this);
//		}
		
		//老接口
		/*
		rr.authorize(this, new RenrenAuthListener() {
			public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
				Toast("认证错误");
			}
			
			public void onComplete(Bundle values) {
				Toast("access_token====="
						+ values.get("access_token")+"   USERID:  "+rr.getCurrentUid());
				//expires_in
				SharedPreferences shareds=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
				Editor editor=shareds.edit();
				editor.putString("RR_access_tokenn", values.getString("access_token"));
				editor.putString("RR_expires_in", values.get("expires_in").toString());
				editor.commit();
				
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						AsyncRenren asyncRenren = new AsyncRenren(rr);
						long uid=rr.getCurrentUid();
						if(uid==0){
							uid=22273771;
						}
						UsersGetInfoRequestParam param = new UsersGetInfoRequestParam(new String[]{String.valueOf(uid)});
						asyncRenren.getUsersInfo(param, renrenListener);
					}
					
				}.start();
			}

			
			public void onCancelLogin() {
				Toast("取消登录");
			}

			public void onCancelAuth(Bundle values) {
				Toast("取消认证");
			}
		});*/
		
		
		
		//公用接口
//		ShareUtils share=new ShareUtils(this);
//		share.setCallBack(new LoginCallback(){
//
//			@Override
//			public void doSuccess(Object obj) {
//				// TODO Auto-generated method stub
//				Bundle values=(Bundle) obj;
//				SharedPreferences shareds=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
//				Editor editor=shareds.edit();
//				editor.putString("RR_access_tokenn", values.getString("access_token"));
//				editor.putString("RR_expires_in", values.get("expires_in").toString());
//				editor.commit();
//				
//				new Thread(){
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						super.run();
//						AsyncRenren asyncRenren = new AsyncRenren(rr);
//						long uid=rr.getCurrentUid();
//						if(uid==0){
//							uid=22273771;
//						}
//						UsersGetInfoRequestParam param = new UsersGetInfoRequestParam(new String[]{String.valueOf(uid)});
//						asyncRenren.getUsersInfo(param, renrenListener);
//					}
//					
//				}.start();
//			}
//
//			@Override
//			public void doError(String errorMsg) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		share.doRenRenLogin(rr);
		

		ShareUtils share=new ShareUtils(this);
		share.setCallBack(new LoginCallback(){
			@Override
			public void doSuccess(Object obj) {
				// TODO Auto-generated method stub
				RennClient rennClient=(RennClient) obj;
				GetUserParam param3 = new GetUserParam();
	            param3.setUserId(rennClient.getUid());
                 if (dialog == null) {
                	 dialog = new ProgressDialog(LoginActivity.this);
                	 dialog.setCancelable(true);
                	 dialog.setTitle("请等待");
                	 dialog.setIcon(android.R.drawable.ic_dialog_info);
                	 dialog.setMessage("正在获取信息");
                	 dialog.show();
                 }
                 try {
                	 rennClient.getRennService().sendAsynRequest(param3, new CallBack() {    
                         @Override
                         public void onSuccess(RennResponse response) {
                        	 Log.e(Constant.TAG, response.toString());
                        	 int id=0;
                        	 String nickName="";
                        	 String img="";
                        	 String location="保密";
                        	 String birth="";
			                 int gendar=2;
			                 String work="保密"; 
                        	 try {
								JSONObject obj=response.getResponseObject();
								id=obj.getInt("id");
								nickName=obj.getString("name");
			                	JSONObject baseObj=obj.getJSONObject("basicInformation");
			                	if(null!=baseObj){
			                		String sex=baseObj.getString("sex");
			                		if(sex.equals("MALE")){
				                		gendar=0;
				                	}else if(sex.equals("FEMALE")){
				                		gendar=1;
				                	}
			                		
			                		Object tempObj=baseObj.get("homeTown");
			                		Log.e(Constant.TAG, "测试： "+tempObj+"     "+(null==tempObj)+"     "+("null".equals(tempObj.toString().trim())));
			                		if(null!=tempObj && !"null".equals(tempObj.toString().trim())){
			                			JSONObject homeObj=baseObj.getJSONObject("homeTown");
			                			location=homeObj.getString("province")+" "+homeObj.getString("city");
			                		}
			                		birth=baseObj.getString("birthday");
			                	}
			                	if(!"null".equals(obj.get("work").toString().trim())){
			                		JSONArray workArray=obj.getJSONArray("work");
				                	if(workArray!=null && workArray.length()>0){
				                		JSONObject workObj=workArray.getJSONObject(0);
				                		Object jobObj=workObj.get("job");
				                		if(null!=jobObj && !"null".equals(jobObj.toString().trim())){
				                			work=workObj.getJSONObject("job").getString("jobDetail");
				                		}
				                	//String work=obj.getJSONArray("work")[1].getJSONObject("job").getString("jobDetail");
				                	}
			                	}

			                	JSONArray imgArray=obj.getJSONArray("avatar");
			                	if(null!=imgArray && imgArray.length()>0){
			                		img=imgArray.getJSONObject(2).getString("url");
			                	}
			                	
			                	doRegistAndLogin(String.valueOf(id), nickName, "", 3, img, location, gendar,"",work,birth);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Utils.printException(e);
								doRegistAndLogin(String.valueOf(id), nickName, "", 3, img, location, gendar,"",work,birth);
							}
                             Toast.makeText(LoginActivity.this, "获取成功", Toast.LENGTH_SHORT).show();  
                             if (dialog != null) {
                            	 dialog.dismiss();
                            	 dialog = null;
                             }                           
                         }
                         
                         @Override
                         public void onFailed(String errorCode, String errorMessage) {
                        	 Log.e(Constant.TAG,"eeeeeeeeeeeeeeeeeeeeee: "+errorCode+":"+errorMessage);
                             Toast.makeText(LoginActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                             if (dialog != null) {
                            	 dialog.dismiss();
                            	 dialog = null;
                             }                            
                         }
                     });
                 } catch (RennException e1) {
                     // TODO Auto-generated catch block
                     Utils.printException(e1);
                 }
			}

			@Override
			public void doError(String errorMsg) {
				// TODO Auto-generated method stub
				Log.e(Constant.TAG, errorMsg);
			}
		});
		share.doRenRenLogin();
	}
	
	
//	private AbstractRequestListener<UsersGetInfoResponseBean> renrenListener = new AbstractRequestListener<UsersGetInfoResponseBean>() {
//		@Override
//		public void onComplete(final UsersGetInfoResponseBean bean) {
//			runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					Log.e(Constant.TAG, "RRRRRRRRRRRRRRRRRRRRRR: "+bean.toString());
//				}
//			});
//		}
//
//		@Override
//		public void onRenrenError(final RenrenError renrenError) {
//			runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//				}
//			});
//		}
//
//		@Override
//		public void onFault(final Throwable fault) {
//			runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//				}
//			});
//		}
//	};
	
	/**
	 * sina sso 登陆
	 */
	
	void doSinaSSOLogin(){
		 /*
		 sinaweibo = Weibo.getInstance(Constant.CONSUMER_KEY, Constant.REDIRECT_URL);
		 LoginActivity.accessToken = AccessTokenKeeper.readAccessToken(this);
	        if (LoginActivity.accessToken.isSessionValid()) {
	            Weibo.isWifi = Utility.isWifi(this);
	            try {
	                Class sso = Class.forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
	            } catch (ClassNotFoundException e) {
	                // e.printStackTrace();
	                Log.i(Constant.TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

	            }

	            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss",Locale.CHINA)
	                    .format(new java.util.Date(LoginActivity.accessToken
	                            .getExpiresTime()));
	            Toast("access_token 仍在有效期内,无需再次登录: \naccess_token:"
	                    + LoginActivity.accessToken.getToken() + "\n有效期：" + date);
	        } else {
	        	Toast("使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
	        }
		 mSsoHandler = new SsoHandler(this, sinaweibo);
         mSsoHandler.authorize(new AuthDialogListener());
         */
		
		
	}


	/**
	 * sina oauth2.0 登陆
	 */
	
	private SsoHandler mSsoHandler=null;
	void doSinaOAuthLogin(){
		 sinaweibo = Weibo.getInstance(Constant.CONSUMER_KEY, Constant.REDIRECT_URL);
		 Weibo.setApp_secret(Constant.SINA_KEY);
		 mSsoHandler = new SsoHandler(this, sinaweibo);

		 ShareUtils share=new ShareUtils(this);
		 share.setCallBack(new LoginCallback(){
			@Override
			public void doSuccess(Object obj) {
				// TODO Auto-generated method stub
				dialog=LoginActivity.this.showProgress(-1, "", "正在登陆..");
				dialog.show();
				Bundle values=(Bundle) obj;
				final String code = values.getString("code");
				final String token = values.getString("access_token");
				if(!TextUtils.isEmpty(token)){
			        String expires_in = values.getString("expires_in");
			        final String uid  = values.getString("uid");
					SharedPreferences pref = LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_APPEND);
	        		Editor editor = pref.edit();
	        		editor.putString("SINA_uid", uid);
	        		editor.putString("SINA_token",token );
	        		editor.putString("SINA_expires_in", expires_in);
	        		editor.commit();
					new Thread(){
						@Override
						public void run() {
							WeiboParameters bundle = new WeiboParameters();
							bundle.add("uid", uid);
							bundle.add("access_token", token);
					        AsyncWeiboRunner.request("https://api.weibo.com/2/users/show.json", bundle, "GET", new WeiboRequestListener(STATE_USER_INFO));
						}
					}.start();
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
											SharedPreferences pref = LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_APPEND);
							        		Editor editor = pref.edit();
							        		editor.putString("SINA_uid", uid);
							        		editor.putString("SINA_token",access_token );
							        		editor.putString("SINA_expires_in", expires_in);
							        		editor.commit();
											
											WeiboParameters bundle = new WeiboParameters();
											bundle.add("uid", uid);
											bundle.add("access_token", access_token);
									        AsyncWeiboRunner.request("https://api.weibo.com/2/users/show.json", bundle, "GET", new WeiboRequestListener(STATE_USER_INFO));
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
				
			
				
			}

			@Override
			public void doError(String errorMsg) {
				// TODO Auto-generated method stub
				Log.e(Constant.TAG, errorMsg);
			}
		 });
		 share.doSinaLogin(sinaweibo,mSsoHandler);
		/* SharedPreferences mShared=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
		 String token = mShared.getString("SINA_token", null);
		 String expires_in = mShared.getString("SINA_expires_in", null);
		 String	mUserId = mShared.getString("SINA_uid", null);
		 if (token != null && expires_in != null) {
			//sinaweibo.setupConsumerConfig(Constant.CONSUMER_KEY, Constant.SINA_KEY);
			//sinaweibo.setRedirectUrl( Constant.REDIRECT_URL);
			AccessToken accessToken = new AccessToken(token,Constant.SINA_KEY);
			accessToken.setExpiresIn(expires_in);
			sinaweibo.setAccessToken(accessToken);
			
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("uid", mUserId);
	        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(sinaweibo);
	        weiboRunner.request(LoginActivity.this, Weibo.URL_USERS_SHOW, bundle, Utility.HTTPMETHOD_GET, new WeiboRequestListener(STATE_USER_INFO));
		}else{	 
		}*/
		 
		 
		 //老接口
		 /*
		 sinaweibo.setupConsumerConfig(Constant.CONSUMER_KEY, Constant.SINA_KEY);
		 sinaweibo.setRedirectUrl( Constant.REDIRECT_URL);
		 sinaweibo.authorize(LoginActivity.this, new AuthDialogListener());
		 */
		 
		 
		 
		 //公用接口
		 /*
		 ShareUtils share=new ShareUtils(this);
		 share.setCallBack(new LoginCallback(){
			@Override
			public void doSuccess(Object obj) {
				// TODO Auto-generated method stub
				Bundle values=(Bundle) obj;
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				final String uid  = values.getString("uid");
				
				AccessToken accessToken = new AccessToken(token, Constant.SINA_KEY);
				accessToken.setExpiresIn(expires_in);
				sinaweibo.setAccessToken(accessToken);
				//将用户 id token expines_in 存起来
				SharedPreferences shareds=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
				Editor editor=shareds.edit();
				editor.putString("SINA_uid", uid);
				editor.putString("SINA_token", token);
				editor.putString("SINA_expires_in", expires_in);
				editor.commit();
				
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						
						WeiboParameters bundle = new WeiboParameters();
						bundle.add("uid", uid);
				        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(sinaweibo);
				        weiboRunner.request(LoginActivity.this, Weibo.URL_USERS_SHOW, bundle, Utility.HTTPMETHOD_GET, new WeiboRequestListener(STATE_USER_INFO));
					}
				}.start();
			}

			@Override
			public void doError(String errorMsg) {
				// TODO Auto-generated method stub
				Toast(errorMsg);
			}
			 
		 });
		 share.doSinaLogin(sinaweibo);
		 */
	}
	
	
	
	
	
	
	
	
	  private class BaseUiListener implements IUiListener {
	        public void onComplete(JSONObject response) {
	            doComplete(response);
	        }

	        protected void doComplete(JSONObject values) {

	        }

	        
	        public void onError(UiError e) {
	            Log.e(Constant.TAG,"onError:  "+"code:" + e.errorCode + ", msg:"
	                    + e.errorMessage + ", detail:" + e.errorDetail);
	        }

	        public void onCancel() {
	        	Toast("onCancel");
	        }
	    }
	  
	  
	  	/*
	    class AuthDialogListener implements WeiboDialogListener {
	    	public void onComplete(Bundle values) {
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				final String uid  = values.getString("uid");
				
				AccessToken accessToken = new AccessToken(token, Constant.SINA_KEY);
				accessToken.setExpiresIn(expires_in);
				sinaweibo.setAccessToken(accessToken);
				//将用户 id token expines_in 存起来
				SharedPreferences shareds=LoginActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
				Editor editor=shareds.edit();
				editor.putString("SINA_uid", uid);
				editor.putString("SINA_token", token);
				editor.putString("SINA_expires_in", expires_in);
				editor.commit();
				
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						
						WeiboParameters bundle = new WeiboParameters();
						bundle.add("uid", uid);
				        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(sinaweibo);
				        weiboRunner.request(LoginActivity.this, Weibo.URL_USERS_SHOW, bundle, Utility.HTTPMETHOD_GET, new WeiboRequestListener(STATE_USER_INFO));
					}
				}.start();
			
			}

			public void onError(DialogError e) {
				Toast.makeText(getApplicationContext(),
						"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
			}

			public void onCancel() {
				Toast.makeText(getApplicationContext(), "Auth cancel",
						Toast.LENGTH_LONG).show();
			}

			public void onWeiboException(WeiboException e) {
				Toast.makeText(getApplicationContext(),
						"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
						.show();
				Utils.printException(e);
			}

	    }*/
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);

	        /**
	         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
	         */
	        if(null!=mTencent){
	        	if (!mTencent.onActivityResult(requestCode, resultCode, data)){
	        	}
	        }
	        
	        if(null!=mSsoHandler){
	        	mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	        }
	    }
	    

	    
	    private class BaseApiListener implements IRequestListener {
	    	
	        private String mScope = "all";
	        private Boolean mNeedReAuth = false;

	        public BaseApiListener(String scope, boolean needReAuth) {
	            mScope = scope;
	            mNeedReAuth = needReAuth;
	        }

	        public void onComplete(final JSONObject response, Object state) {
	            doComplete(response, state);
	        }

	        protected void doComplete(JSONObject response, Object state) {
	            try {
	                int ret = response.getInt("ret");
	                if (ret == 100030) {
	                    if (mNeedReAuth) {
	                        Runnable r = new Runnable() {
	                            public void run() {
	                                mTencent.reAuth(LoginActivity.this, mScope, new BaseUiListener());
	                            }
	                        };
	                        LoginActivity.this.runOnUiThread(r);
	                    }
	                }else if(ret==0){
	                	String nickName=response.getString("nickname");
	                	String img=response.getString("figureurl_qq_2");
	                	if(TextUtils.isEmpty(img)){
	                		img=response.getString("figureurl_qq_1");
	                	}	
	                	int gendar=2;
	                	String sex=response.getString("gender");
	                	if(sex.equals("男")){
	                		gendar=0;
	                	}else if(sex.equals("女")){
	                		gendar=1;
	                	}
	                	doRegistAndLogin(mTencent.getOpenId(), nickName, "", 2, img, "", gendar,"","","");
	                }
	                
	                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
	                // 如果cgi返回的值没有token, 则会清空原来的token
	                // String token = response.getString("access_token");
	                // String expire = response.getString("expires_in");
	                // String openid = response.getString("openid");
	                // mTencent.setAccessToken(token, expire);
	                // mTencent.setOpenId(openid);
	            } catch (JSONException e) {
	                e.printStackTrace();
	                Log.e("toddtest", response.toString());
	            }

	        }

	        
	        public void onIOException(final IOException e, Object state) {
	        	 Log.e(Constant.TAG,"IRequestListener.onIOException:"+e.getMessage());
	        }

	        
	        public void onMalformedURLException(final MalformedURLException e,
	                Object state) {
	        	 Log.e(Constant.TAG,"IRequestListener.onMalformedURLException"+e.toString());
	        }

	        
	        public void onJSONException(final JSONException e, Object state) {
	        	 Log.e(Constant.TAG,"IRequestListener.onJSONException:"+e.getMessage());
	        }

	        
	        public void onConnectTimeoutException(ConnectTimeoutException arg0,
	                Object arg1) {
	        	 Log.e(Constant.TAG,"IRequestListener.onConnectTimeoutException:"+arg0.getMessage());

	        }

	        
	        public void onSocketTimeoutException(SocketTimeoutException arg0,
	                Object arg1) {
	        	 Log.e(Constant.TAG,"IRequestListener.SocketTimeoutException:"+ arg0.getMessage());
	        }

	        
	        public void onUnknowException(Exception arg0, Object arg1) {
	        	 Log.e(Constant.TAG,"IRequestListener.onUnknowException:"+ arg0.getMessage());
	        }

	        
	        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
	        	 Log.e(Constant.TAG,"IRequestListener.HttpStatusException:"+ arg0.getMessage());
	        }

	        
	        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
	        	 Log.e(Constant.TAG,"IRequestListener.onNetworkUnavailableException:"+ arg0.getMessage());
	        }
	    }
	    
	    
	    
	    
		/**
		 * sina 
		  * @ClassName WeiboRequestListener
		  * @Description 请求结果 回调
		  * @author aceway-liwei
		  * @date 2012-11-21 上午10:41:14
		  *
		 */
	    
	    
		class WeiboRequestListener implements RequestListener{
			int state;
			public WeiboRequestListener(int state) {
				this.state = state;
			}
			
			
			public void onComplete(String response) {
				switch (state) {
				case STATE_USER_INFO:
					try {
						JSONObject jsonRoot = new JSONObject(response);
						String id=jsonRoot.optString("idstr");
						String screen_name=jsonRoot.optString("screen_name");
						String location=jsonRoot.optString("location");
						String description=jsonRoot.optString("description");
						String profile_image_url=jsonRoot.optString("avatar_large");
						if(TextUtils.isEmpty(profile_image_url)){
							profile_image_url=jsonRoot.optString("profile_image_url");
						}
						int gendar=2;
						String sex=jsonRoot.optString("gender");
						if(sex.equals("m")){
							gendar=0;
						}else if(sex.equals("f")){
							gendar=1;
						}
						Log.e(Constant.TAG, "uuuuuuuuuuuuuuuuuuuuuuuuu:"+ response);
						doRegistAndLogin(id,screen_name,"",1,profile_image_url,location,gendar,description,"","");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}

			public void onError(WeiboException e) {
				switch (state) {
				case STATE_USER_INFO:
					//Toast.makeText(LoginActivity.this, "获取用户信息失败 "+e.getMessage(), Toast.LENGTH_SHORT).show();
					Utils.printException(e);
					break;
				default:
					break;
				}
			}
			
			public void onIOException(IOException e) {
				switch (state) {
				case STATE_USER_INFO:
					Utils.printException(e);
					break;
				default:
					break;
				}
			}
			
		}
	    
	    
	    
	    /**
	     * 第三方登录
	     */
	    private void doRegistAndLogin(final String email,final String nickName,final String password,final int accountType,
	    		final String img,final String location,final int gender,final String sign,final String work,final String birth){
	    	
	    	new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					ResponseParam param=RegistManager.newInstance().doRegist(email, nickName, password,accountType,img,location,gender,sign,work,birth,getImsi());
					Message msg=handler.obtainMessage();
					if(null==param){
						msg.what=-1;
					}else{
						msg.what=2;
						msg.obj=param;
					}
					msg.sendToTarget();
				}
        	}.start();
	    }
	    
	    
	    @Override
	  	public boolean onMenuOpened(int featureId, Menu menu) {
	  		sendBroadcast(new Intent(MainActivity.MENU_CLICK_ACTION));
	  		return false;
	  	}
	    
	    
}
