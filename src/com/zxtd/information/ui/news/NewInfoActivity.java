package com.zxtd.information.ui.news;

import java.util.Map;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.NewsInfo;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.listener.OnLeftOrRightListener;
import com.zxtd.information.manager.CurrentManager;
import com.zxtd.information.manager.MyCollectionManager;
import com.zxtd.information.parse.json.ParseInfoImages;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.MainActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.LoginActivity;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.ui.post.CommentActivity;
import com.zxtd.information.ui.post.PostListActivity;
import com.zxtd.information.ui.video.Fullscreen;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ShareUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.NewInfoMenuDialog;
import com.zxtd.information.view.NewListViewCreater;
import com.zxtd.information.view.ShareNewsDialog;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewInfoActivity extends BaseActivity implements OnClickListener,
		DialogInterface.OnClickListener, IWXAPIEventHandler {
	private WebView newWebView;
	private Button btnBack;
	private Button btnPostCount;
	private Button btnMore;
	private LinearLayout newPostEdit;
	private LinearLayout zxtdLoadingView;
	private RelativeLayout netErrorView;
	private FrameLayout newInfoView;
	private RelativeLayout titleBar;
	private String flag = "";
	private String imagesJson = null;

	private CurrentManager currentManager;
	private NewInfoMenuDialog infoMenuDialog;
	private ShareNewsDialog shareNewsDialog;

	private int userId = -1;
	private boolean isCollected = false;
	
	private boolean isPushNew = false;
	
	private Handler handler;
	
	public static final int POST_TOAST = 1;
	private static final int LOGIN_CODE = 110;
	
	private ParseInfoImages infoImages;
	private IWXAPI iwxapi;

	private OnClickListener netConnectClick = new OnClickListener() {

		public void onClick(View v) {
			zxtdLoadingView.setVisibility(View.VISIBLE);
			netErrorView.setVisibility(View.GONE);
			newWebView.reload();
		}
	};

	private OnClickListener wlanSettingClick = new OnClickListener() {

		public void onClick(View v) {
			Utils.jumpToWlanSetting(NewInfoActivity.this);
		}
	};

	private OnLeftOrRightListener leftOrRightListener = new OnLeftOrRightListener() {

		@Override
		public boolean onLeftOrRight(View view, EventType eventType) {
			if (eventType == EventType.RIGHT) {
				 back();
			} else if (eventType == EventType.LEFT) {
				Intent intent = new Intent(NewInfoActivity.this,
						PostListActivity.class);
				intent.putExtra(Constant.BundleKey.NEWID,
						currentManager.getCurrentNewId());
				intent.putExtra(Constant.BundleKey.FLAG, flag);
				NewInfoActivity.this.startActivityForResult(intent, 0);
			}
			return true;
		}

	};
	
	private DialogInterface.OnClickListener shareItemClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == 2 || which == 3){
				String imgUrl = null;
				if(infoImages != null && infoImages.getImageBeans() != null && infoImages.getImageBeans().size() > 0){
					imgUrl = infoImages.getImageBeans().get(0).imageUrl;
				}
				doWXShare(which == 2 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline, imgUrl);
			}else{
				Intent intent = new Intent(NewInfoActivity.this, ShareActivity.class);
				intent.putExtra(Constant.BundleKey.IMGE_DATA, imagesJson);
				intent.putExtra(Constant.BundleKey.WHICH_CHANNEL, which);
				NewInfoActivity.this.startActivity(intent);
			}
			
		}
	};
	
	//分享到微信
		private void doWXShare(int scene, String imgUrl){
			NewBean newBean = (NewBean)CurrentManager.newInstance().getBean();
			if(!iwxapi.isWXAppInstalled()){
				Toast("没有安装微信客户端");
				return;
			}
			if(newBean != null){
				String content = "";
				if(!Utils.isEmpty(newBean.newContent) && newBean.newContent.length() > 200){
					content = newBean.newContent.substring(0, 200);
				}else{
					content = newBean.newContent;
				}
				ShareUtils.doWXShare(iwxapi, scene,newBean.newTitle, content, zxtd_ImageCacheDao.Instance().getImageCacheFile(imgUrl), newBean.infoUrl);
			}else{
					Toast("当前新闻是空的");
			}
				
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_info);
		initData();
		initView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
		iwxapi.handleIntent(getIntent(), this);
	}
	
	private void initData() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			flag = bundle.getString(Constant.BundleKey.FLAG);
			Bean bean = bundle.getParcelable(Constant.BundleKey.NEWBEAN);
			if (bean != null) {
				CurrentManager.newInstance().setCurrentBean(bean);
			}
			
			if(bundle.containsKey("isPushNew")){
				isPushNew = bundle.getBoolean("isPushNew", false);
			}
			
		}
		Utils.initDisplayMetrics(this);
		Utils.isHasCoverImage = false;
		infoMenuDialog = new NewInfoMenuDialog(this);
		shareNewsDialog = new ShareNewsDialog(this);
		handler = new Handler();
		userId = getUserId();
		iwxapi = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APP_ID, false);
		
		iwxapi.handleIntent(getIntent(), this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		btnBack = (Button) this.findViewById(R.id.back);
		btnPostCount = (Button) this.findViewById(R.id.btn_new_info_comments);
		newPostEdit = (LinearLayout) this.findViewById(R.id.new_post_edit);
		zxtdLoadingView = (LinearLayout) this.findViewById(R.id.loading_view);
		netErrorView = (RelativeLayout) this.findViewById(R.id.net_error);
		titleBar = (RelativeLayout) this.findViewById(R.id.title_bar);
		btnMore = (Button) this.findViewById(R.id.btn_more);
		TextView btnWlanSetting = (TextView) this
				.findViewById(R.id.btn_wlan_setting);
		newInfoView = (FrameLayout) this.findViewById(R.id.new_info_view);
		newWebView = new WebView(ZXTD_Application.getMyContext());
		newWebView.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		newInfoView.addView(newWebView, 0);

		
		if (Constant.Flag.POSTER_TYPE.equals(flag)) {
			newPostEdit.setVisibility(View.GONE);
			btnPostCount.setVisibility(View.GONE);
		}
		btnBack.setOnClickListener(this);
		btnPostCount.setOnClickListener(this);
		newPostEdit.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		netErrorView.setOnClickListener(netConnectClick);
		btnWlanSetting.setOnClickListener(wlanSettingClick);
		infoMenuDialog.setOnClickListener(this);
		shareNewsDialog.setOnClickListener(shareItemClickListener);
		newWebView.clearCache(true);
		newWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		newWebView.getSettings().setJavaScriptEnabled(true);
		newWebView.setScrollBarStyle(0);
		newWebView.refreshDrawableState();
		newWebView.addJavascriptInterface(new JavascriptInterface(this),
				"imagelistner");
		newWebView.getSettings().setPluginsEnabled(true);
		newWebView.setOnTouchListener(leftOrRightListener);
		newWebView.setBackgroundColor(0xfff0f0f0);

		newWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				view.getSettings().setBlockNetworkImage(false);

				if (Constant.Flag.NET_FRIEND_TYPE.equals(flag)) {
					// addNetFriendImageClickListner();
				} else {
					// addImageClickListner();
				}
				loaded();
				Log.i(NewInfoActivity.class.getName(), "url:" + url);
				addJs();
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				loading();
				Map<String, String> param = Utils.getUrlParam(url);
				setNewIdTag(param.get("paramID"), url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				netErrorView.setVisibility(View.VISIBLE);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (isOpenWithWebView(url)) {
					return super.shouldOverrideUrlLoading(view, url);
				} else {
					Uri uri = Uri.parse(url); // url为你要链接的地址
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);

					return true;
				}
			}
		});
		setInfo();
	}

	public void onClick(View v) {
		if (v == btnBack) {
			back();
		} else if (v == btnPostCount) {
			Intent intent = new Intent(this, PostListActivity.class);
			intent.putExtra(Constant.BundleKey.NEWID,
					currentManager.getCurrentNewId());
			intent.putExtra(Constant.BundleKey.FLAG, flag);
			this.startActivityForResult(intent, 0);
		} else if (v == newPostEdit) {
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra(Constant.BundleKey.NEWID,
					currentManager.getCurrentNewId());
			intent.putExtra(Constant.BundleKey.FLAG, flag);
			this.startActivityForResult(intent, 0);
		} else if (v == btnMore) {
			int[] location = new int[2];
			titleBar.getLocationOnScreen(location);
			infoMenuDialog.setDiloagLocation(location[0], location[1]
					+ titleBar.getHeight());
			infoMenuDialog.show();
		}
	}

	// js通信接口
	public class JavascriptInterface {
		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void openImage(String img, String i) {
			int index = Integer.parseInt(i);
			if(index == -1){
				imagesJson = img;
				infoImages = new ParseInfoImages();
				if(!Utils.isEmpty(imagesJson)){
					infoImages.parse(imagesJson);
					if(infoImages.getImageBeans().size() > 0){
						String imgUrl = infoImages.getImageBeans().get(0).imageUrl;
						new zxtd_AsyncImageLoader().loadCacheDrawable(imgUrl, null, 0, new ImageListCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable, View view, int position) {
								
							}
						}, zxtd_ImageCacheDao.Instance());
					}
				}
			}else{
				Intent intent = new Intent();
				intent.putExtra(Constant.BundleKey.IMAGE_INDEX, Integer.parseInt(i));
				intent.putExtra(Constant.BundleKey.IMGE_DATA, img);
				intent.setClass(context, ShowWebImageActivity.class);
				context.startActivity(intent);
			}
			
		}

		/**
		 * window.imagelistner.playVideo(String url)
		 * 
		 * @param url
		 *            视频地址
		 */
		public void playVideo(String url) {
			Log.i("zxtd_video", "playVideo---url:" + url);
			String filePath = url;
			Intent fullPlay = new Intent(context, Fullscreen.class);
			Bundle iBundle = new Bundle();
			iBundle.putString("url", filePath);
			fullPlay.putExtras(iBundle);
			context.startActivity(fullPlay);
		}
		//设置评论数
		public void setPostCount(String count){
			final String postCount = count;
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					btnPostCount.setText(postCount + "跟帖");
					currentManager.setPostCount(postCount);
				}
			});
		}
		//打开别人主页
		public void openOtherMain(String userId){
			try {
				if(Utils.isEmpty(userId) || "-1".equals(userId)){
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(NewInfoActivity.this, "该用户不是注册用户", Toast.LENGTH_SHORT).show();
						}
					});
				}else{
					int otherUserId = Integer.parseInt(userId);
					FansFocusBean bean = new FansFocusBean();
					bean.setUserId(otherUserId);
					Intent intent = new Intent(NewInfoActivity.this, OtherMainActivity.class);
					intent.putExtra("bean", bean);
					NewInfoActivity.this.startActivity(intent);
				}
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		public void setNewsInfo(String title, String content){
			//System.out.println(title + "\n" + content);
			NewBean newBean = (NewBean)currentManager.getBean();
			if(newBean == null){
				return;
			}
			newBean.newTitle = title;
			newBean.newContent = content;
		}
		
		public void downloadVideo(String url) {
			Log.e("zxtd_video", "method downloadVideo do not fullfill yet!!!");
			// Intent download = new Intent(context,
			// DownloadManagerActivity.class);
			// Bundle iBundle = new Bundle();
			// iBundle.putString("url", url);
			// download.putExtras(iBundle);
			// context.startActivity(download);
		}
	}
	
	private void addJs(){
		String js = "javascript:(function(){	" +
									"var couObj = document.getElementById(\"ppostcount\");	" +
									"window.imagelistner.setPostCount(couObj.value);" +
									"var iTitleObj = document.getElementById('ITITLE');	" +
									"var iContentObj = document.getElementById('ICONTENT');" +
									"var pTitleObj = document.getElementById('PTITLE');" +
									"var pContentObj = document.getElementById('PCONTENT');" +
									"if (iTitleObj && iContentObj) {" +
										"window.imagelistner.setNewsInfo( iTitleObj.innerText, iContentObj.innerText);" +
									"}else if(pTitleObj && pContentObj){" +
										"window.imagelistner.setNewsInfo( pTitleObj.innerText, pContentObj.innerText);" +
									"}" +
								"})()";
		newWebView.loadUrl(js);
	}

	private boolean isOpenWithWebView(String url) {
		if (Utils.isEmpty(url)) {
			return true;
		}
		Map<String, String> param = Utils.getUrlParam(url);
		if (param.containsKey("browseway")
				&& "zaseclient".equals(param.get("browseway"))) {
			return true;
		}
		return false;
	}

	private void setNewIdTag(String value, String infoUrl) {
		String newId = currentManager.getCurrentNewId();
		if (Utils.isEmpty(newId) || !newId.equals(value)) {
			NewBean newBean = new NewBean();
			newBean.newId = value;
			newBean.infoUrl = infoUrl;
			currentManager.setCurrentBean(newBean);
		}
		isCollected = Utils.hasCollent(currentManager.getCurrentNewId(), getType(flag));
		infoMenuDialog.setCollected(isCollected);
	}

	//跳到登陆界面
	private void onLogin(){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("from", "other");
			this.startActivityForResult(intent, LOGIN_CODE);
		}
	
	private void setViews() {

		newWebView.getSettings().setBlockNetworkImage(true);
		if (Constant.Flag.POSTER_TYPE.equals(flag)) {
			newWebView.loadUrl(currentManager.getInfoUrl() + "&width="
					+ Utils.getDisplayMetrics().widthPixels + "&fontsize="
					+ Utils.fontSize + "&noimg="
					+ NewListViewCreater.getNoImage() + "&version=" + ZXTD_Application.versionName);
		} else {
			newWebView.loadUrl(currentManager.getInfoUrl() + "&width="
					+ Utils.getDisplayMetrics().widthPixels + "&postcount="
					+ currentManager.getPostCount() + "&fontsize="
					+ Utils.fontSize + "&noimg="
					+ NewListViewCreater.getNoImage() + "&version=" + ZXTD_Application.versionName);
		}

	}

	private void setInfo() {
		currentManager = CurrentManager.newInstance();
		loading();
		setViews();
	}

	private void loading() {
		zxtdLoadingView.setVisibility(View.VISIBLE);
	}

	private void loaded() {
		zxtdLoadingView.setVisibility(View.GONE);

	}

	private void back() {
		if (newWebView.canGoBack()) {
			newWebView.goBack();
		} else {
			if(isPushNew && !Utils.isInit){
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isPushNew", isPushNew);
				intent.putExtra(Constant.BundleKey.FLAG, flag);
				this.startActivity(intent);
				this.finish();
			}else{
				this.finish();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == LOGIN_CODE){
			if(resultCode == RESULT_OK){
				userId = this.getUserId();
				Toast("登陆成功！");
			}else{
				Toast("登陆失败！");
			}
		}
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		Bundle bundle = data.getExtras();
		if (bundle != null) {
			String postCount = bundle.getString(Constant.BundleKey.POST_COUNT);
			if (!Utils.isEmpty(postCount)) {
				btnPostCount.setText(postCount + "跟帖");
				currentManager.setPostCount(postCount);
			}
		}
	}
	
	/**
	 * 
	 * @param newBean
	 * @param type
	 * <li> 1	新闻
	 * <li> 2	网友
	 */
	ProgressDialog dialog=null;
	private void cacheCollect(final NewBean newBean, final int type ){
		dialog=showProgress(-1, "", "正在添加收藏..");
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				int resultCode=MyCollectionManager.getInstance().addServerCollection(newBean.newId,type);
				if(resultCode==1){
					NewsInfo info=new NewsInfo();
					info.setNewsId(Integer.valueOf(newBean.newId));
					info.setNewsTitle(newBean.newTitle);
					info.setNewReplyCount(Integer.valueOf(newBean.postCount));
					String content=newBean.newContent;
					if(TextUtils.isEmpty(content)){
						content="";
					}else{
						if(content.length()>40){
							content=content.substring(0,40)+"...";
						}else{
							content+="...";
						}	    
					}
					info.setNewSummary(content);
					info.setNewsType(type);
					info.setNewsUrl(newBean.infoUrl);
					boolean isSuccess=MyCollectionManager.getInstance().addLocalCollection(info);
					if(isSuccess){
						cacheHandler.sendEmptyMessage(1);
					}else{
						cacheHandler.sendEmptyMessage(2);
					}
				}else if(resultCode==-1){
					cacheHandler.sendEmptyMessage(-1);
				}else{
					cacheHandler.sendEmptyMessage(2);
				}
			}
		}.start();
		
	}
	
	
	private Handler cacheHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					NewInfoActivity.this.checkNerWork();
				}break;
				case 1:{
					Utils.addCollent(currentManager.getCurrentNewId(), getType(flag));
					isCollected = true;
					infoMenuDialog.setCollected(isCollected);
					Toast("添加收藏成功");
					
					//通知主页和我的发布页面
					Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
					intent.putExtra("type", 1);
					intent.putExtra("data", 1);
					sendBroadcast(intent);
				}break;
				case 2:{
					Toast("添加收藏失败");
				}break;
				case 3:{
					Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
					intent.putExtra("type", 1);
					intent.putExtra("data", -1);
					sendBroadcast(intent);
					Toast("取消收藏成功");
					Utils.delCollent(currentManager.getCurrentNewId(), getType(flag));
					isCollected = false;
					infoMenuDialog.setCollected(isCollected);

					String from=NewInfoActivity.this.getIntent().getStringExtra("from");
					if("selfcollection".equals(from)){
						NewBean bean=NewInfoActivity.this.getIntent().getParcelableExtra(Constant.BundleKey.NEWBEAN);
						
						Intent newIntent=new Intent(Constant.REFRESH_COLLECTION_DATA);
						newIntent.putExtra("newsId", bean.newId);
						NewInfoActivity.this.sendBroadcast(newIntent);
					}
					
				}break;
				case 4:{
					Toast("取消收藏失败");
				}break;
			}
			if(null!=dialog && dialog.isShowing()){
				dialog.dismiss();
			}
		}
	};
	
	
	
	private void cancelCollect(final String newId,final int type){
		dialog=showProgress(-1, "", "正在取消收藏..");
		dialog.show();
		new Thread(){
			@SuppressLint("HandlerLeak")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				int resultCode=MyCollectionManager.getInstance().deleteColl(NewInfoActivity.this.getUserId() ,Integer.valueOf(newId) ,type);
				Message msg=cacheHandler.obtainMessage();
				if(resultCode==-1){
					msg.what=-1;
				}else{
					if(resultCode==1){
						MyCollectionManager.getInstance().deleteLocalCollection(Integer.valueOf(newId),type);
						msg.what=3;
					}else{
						msg.what=4;
					}
				}
				msg.sendToTarget();
			}
		}.start();
	}
	
	private int getType(String flag){
		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
			return 1;
		}else{
			return 0;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case 0:
			shareNewsDialog.show();
			break;
		case 1:
			NewBean newBean = (NewBean)currentManager.getBean();
			if(newBean == null){
				Toast("新闻没有加载完成！");
				return;
			}
			if(userId == -1){
				onLogin();
				return;
			}
			int type = getType(flag);
			if(!Utils.hasCollent(newBean.newId, type)){
				cacheCollect(newBean, type);
			}else{
				cancelCollect(newBean.newId,type);
			}
			break;
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		int result = 0;
		
		switch (arg0.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Toast(result);
	}

}
