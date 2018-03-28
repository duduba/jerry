package com.zxtd.information.ui;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tencent.weibo.sdk.android.component.sso.tools.MD5Tools;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.zxtd.information.db.zxtd_CollectCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageCallback;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.service.XMPPService;
import com.zxtd.information.ui.me.LoginActivity;
import com.zxtd.information.ui.me.MineNewActivity;
import com.zxtd.information.ui.netfriend.NetFriendActivity;
import com.zxtd.information.ui.news.NewActivity;
import com.zxtd.information.ui.setting.SettingActivity;
import com.zxtd.information.util.AuthUtitl;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ListenFactory;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.ListenFactory.OnGlobalListener;
import com.zxtd.information.util.XmppUtils;
import com.zxtd.information.view.MainMenu;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity{
	public static final String MENU_CLICK_ACTION = "menu_click_action";
	private MainMenu mainMenu;
	private MenuReceiver mMenuReceiver;
	private Handler handler = new Handler();
	private boolean isSecondBack = false;
	private TabHost tabHost=null;
	private TabSpec mineTabSpec=null;
	private View mineView;
	private static final String TAG = "com.zxtd.information.ui.MainActivity";
	
	private ImageView mCoverImage;
	private static String screenWidth = null;
	private static String screenHeight = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.isInit = true;
        mCoverImage = (ImageView) this.findViewById(R.id.app_cover_image);
        
        tabHost = this.getTabHost();
        addTab();
        init();
        
//        getSignInfo();
        
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.UPDATE_MINE_TABHOST_ITEM);
        filter.addAction(Constant.UPDATE_TOTAL_NO_READ);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);
        
        loadCacheVersion();
        loadLocalVersion();
        bindXmppService();
    }
    
    private void setTotalNoRead(int count){
    	TextView txtHasNoRead=(TextView) mineView.findViewById(R.id.mine_all_noread);
    	 if(count==0){
    		 txtHasNoRead.setVisibility(View.GONE);
         }else{
         	txtHasNoRead.setVisibility(View.VISIBLE);
         	txtHasNoRead.setText(String.valueOf(count));
         }
    }
    
    
	private void loadCacheVersion(){
		if(Utils.isNetworkConn() && CacheManager.CACHEVERSION.isEmpty() && XmppUtils.getUserId()!=-1){
			new Thread(){
				@Override
				public void run() {
					CacheManager.getInstance().getVersions();
				}
			}.start();
		}
	}
	
	private void loadLocalVersion(){
		if(CacheManager.LOCALVERSION.isEmpty() && XmppUtils.getUserId()!=-1){
			CacheManager.getInstance().getLocalVersion();
		}
	}
	
	
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra("isPushNew")){
			String flag = intent.getStringExtra(Constant.BundleKey.FLAG);
			int tab = Constant.Flag.NET_FRIEND_TYPE.equals(flag) ? 1 : 0;
			     
			if(tab != this.getTabHost().getCurrentTab()){
				getTabHost().setCurrentTab(tab);
			}
			Log.i(this.getClass().getName(), "推送消息flag：" + flag);
		}
	}

	BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(Constant.UPDATE_MINE_TABHOST_ITEM.equals(arg1.getAction())){
				tabHost.setCurrentTab(0);
				String action=arg1.getStringExtra("action");
				if("login".equals(action)){
					mineTabSpec.setContent(new Intent(MainActivity.this,MineNewActivity.class));
				}else{
					Intent intent= new Intent(MainActivity.this,LoginActivity.class);
					intent.putExtra("from", "main");
					mineTabSpec.setContent(intent);
					setTotalNoRead(0);
				}
				tabHost.invalidate();
				tabHost.setCurrentTab(2);
			}else if(Constant.UPDATE_TOTAL_NO_READ.equals(arg1.getAction())){
				int count=arg1.getIntExtra("count", 0);
				setTotalNoRead(count);
			}
		}
    };
    
    /**
     * 添加tab按钮
     * */
   
    
    private void addTab(){
    	//新闻列表
    	Intent newIntent = new Intent(this, NewActivity.class);
    	TabSpec newTabSpec = tabHost.newTabSpec("NewActivity");
    	newTabSpec.setContent(newIntent);
    	newTabSpec.setIndicator(createTabSpecView(R.drawable.tab_news_selector));
    	
    	//网友列表
    	Intent netFriendIntent = new Intent(this, NetFriendActivity.class);
    	TabSpec netFriendTabSpec = tabHost.newTabSpec("NetFriendActivity");
    	netFriendTabSpec.setContent(netFriendIntent);
    	netFriendTabSpec.setIndicator(createTabSpecView(R.drawable.tab_netfriend_selector));
    	
    	//我的主页
    	Intent mineIntent=null;
    	if(XmppUtils.getUserId()!=-1){
    		mineIntent= new Intent(this,MineNewActivity.class);
    	}else{
    		mineIntent= new Intent(this,LoginActivity.class);//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		mineIntent.putExtra("from", "main");
    	}
    	mineTabSpec= tabHost.newTabSpec("MineActivity");
    	mineTabSpec.setContent(mineIntent);
    	
    	mineView=createNewTabSpecView(R.drawable.tab_mine_selector);
    	
    	mineTabSpec.setIndicator(mineView);
    	//设置管理
    	Intent settingIntent = new Intent(this, SettingActivity.class);
    	TabSpec settingTabSpec = tabHost.newTabSpec("SettingActivity");
    	settingTabSpec.setContent(settingIntent);
    	settingTabSpec.setIndicator(createTabSpecView(R.drawable.tab_setting_selector));
    	
    	tabHost.addTab(newTabSpec);
    	tabHost.addTab(netFriendTabSpec);
    	tabHost.addTab(mineTabSpec);
    	tabHost.addTab(settingTabSpec);
    	
    }
    
    /**
     * 创建tab视图
     * */
    private View createNewTabSpecView(int resId){
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View view = inflater.inflate(R.layout.tab_spec_view_new, null);
    	ImageView icon = (ImageView) view.findViewById(R.id.tab_widgit_icon);
    	//TextView txtHasNoRead=(TextView) view.findViewById(R.id.mine_all_noread);
    	icon.setBackgroundResource(resId);
    	view.setLayoutParams(params);
    	return view;
    }
    
    /**
     * 创建tab视图
     * */
    private View createTabSpecView(int resId){
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View view = inflater.inflate(R.layout.tab_spec_view, null);
    	ImageView icon = (ImageView) view.findViewById(R.id.tab_widgit_icon);
    	icon.setBackgroundResource(resId);
    	view.setLayoutParams(params);
    	return view;
    }
    
    
    
    private void init(){
    	
    	ListenFactory.newInstance().addListener("tabChange", globalListener);
    	
    	Utils.initDisplayMetrics(this);
    	Utils.getBarHeight(this);
    	
    	isPush();
    	initImageMode();
    	Utils.changePushState(this);
    	
    	MobclickAgent.updateOnlineConfig(this);
    	MobclickAgent.openActivityDurationTrack(true);
    	UmengUpdateAgent.setUpdateOnlyWifi(false);
    	UmengUpdateAgent.update(this);	
    	UmengUpdateAgent.setDownloadListener(new UmengDownloadListener(){
    		@Override
    	    public void OnDownloadStart() {
    	    }

    	    @Override
    	    public void OnDownloadUpdate(int progress) {
    	    }

    	    @Override
    	    public void OnDownloadEnd(int result, String file) {
    	        Toast.makeText(MainActivity.this, "下载路径：" + file , Toast.LENGTH_SHORT).show();
    	    }                   
		});
    	
    	getSystemWidthHigh();
		setInitCoverImageShow();
    	
    	zxtd_CollectCacheDao cacheDao = new zxtd_CollectCacheDao();
    	
    	Utils.collectSets = cacheDao.getCollectKeys();
    	
    	mainMenu = new MainMenu(this);
		mainMenu.setOnMenuItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mainMenu.dismiss();
				menuItemClick(arg2);
			}
		});
		mMenuReceiver = new MenuReceiver();
		IntentFilter filter = new IntentFilter(MENU_CLICK_ACTION);
		this.registerReceiver(mMenuReceiver, filter);
		
		if(Utils.isHasCoverImage){
			final Animation coverHideAnim = AnimationUtils.loadAnimation(this, R.anim.app_cover_hide);
			coverHideAnim.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation arg0) {
				}
				
				public void onAnimationRepeat(Animation arg0) {
				}
				public void onAnimationEnd(Animation arg0) {
					mCoverImage.setVisibility(View.GONE);
				}
			});
			handler.postDelayed(new Runnable() {
				public void run() {
					mCoverImage.startAnimation(coverHideAnim);
				}
			}, 3000);
		}else{
			mCoverImage.setVisibility(View.GONE);
		}
		
		AuthUtitl.handler = new Handler();
    }
    
    
    private void isPush(){
		String isPush = Utils.load(this, Constant.DataKey.ISPUSH);
		if(Utils.isEmpty(isPush)){
			Utils.isPush = true;
		}else{
			Utils.isPush =  Boolean.parseBoolean(isPush);
		}
	}
    
    private void initImageMode(){
		String imageMode = Utils.load(this, Constant.DataKey.IMAGE_MODE);
		if(Utils.isEmpty(imageMode)){
			Utils.isImageMode = true;
		}else{
			Utils.isImageMode =  Boolean.parseBoolean(imageMode);
		}
	}
    
    @SuppressLint("SimpleDateFormat")
	private void setInitCoverImageShow() {
		String tempTime = Utils.load(this, Constant.CoverDataKey.TIME);
		if(!Utils.isEmpty(tempTime)){
			String[] times = tempTime.substring(0, 10).split("-");
			int netTime = Integer.parseInt(times[0]+times[1]+times[2]);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			int sysTime = Integer.parseInt(format.format(new Date(System.currentTimeMillis())));
			Log.i(TAG, "------------------------netTime:"+ netTime);
			Log.i(TAG, "------------------------systemTime:"+ sysTime);
			if(netTime >= sysTime) {
				String filePath = Utils.load(this, Constant.CoverDataKey.COVERIMAGEPATH);
				if(!Utils.isEmpty(filePath)) {
					zxtd_AsyncImageLoader imageLoader = new zxtd_AsyncImageLoader();
					ImageSize imageSize = new ImageSize(Integer.parseInt(screenWidth), Integer.parseInt(screenHeight));
					imageLoader.loadLocalDrawable(filePath, null, new ImageCallback() {
						public void imageLoaded(Drawable imageDrawable, View view) {
							if(imageDrawable != null) {
								mCoverImage.setImageDrawable(imageDrawable);
								Log.i(TAG, "---------setImageDrawable-------");
							}else {
								setDefaultCoverImage();
							}
						}
					}, imageSize);
				}else {
					setDefaultCoverImage();
				}
			}else {
				setDefaultCoverImage();
			}
		}else {
			setDefaultCoverImage();
		}
	}

	private void setDefaultCoverImage() {
		mCoverImage.setImageResource(R.drawable.init_image);
	}
	
	private void getSystemWidthHigh() {
		String[] keys = {Constant.CoverDataKey.SCREENWIDTH, Constant.CoverDataKey.SCREENHEIGHT};
		String[] data = Utils.load(this, keys);
		if((!Utils.isEmpty(data[0])) && (!Utils.isEmpty(data[1]))) {
			screenWidth = data[0];
			screenHeight = data[1];
		}else {
			Map<String, String> map = new HashMap<String, String>();
			screenWidth =  String.valueOf(Utils.getDisplayMetrics().widthPixels);
			screenHeight = String.valueOf(Utils.getDisplayMetrics().heightPixels);
			map.put(Constant.CoverDataKey.SCREENWIDTH, screenWidth);
			map.put(Constant.CoverDataKey.SCREENHEIGHT, screenHeight);
			Utils.save(this, map);
		}
	}
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mMenuReceiver);
		unregisterReceiver(receiver);
		XmppUtils.closeConnection();
		unbindService(connection); 
		System.exit(0);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("menu");
	    return super.onCreateOptionsMenu(menu);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isSecondBack){
				this.finish();
			}else{
				Toast.makeText(this, "再点一次，退出程序！", Toast.LENGTH_SHORT).show();
				handler.postDelayed(new Runnable() {
					
					public void run() {
						isSecondBack = false;
					}
				}, 6000);
				isSecondBack = true;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
  //菜单点击项
  	public void menuItemClick(int which){
  		switch (which) {
          case 0:
        	  if(this.getTabHost().getCurrentTab() != 3){
        		  this.getTabHost().setCurrentTab(3);
        	  }
          	break;
          case 1:
          	this.finish();
          	break;
          case 2:
//          	startActivity(new Intent(this, DownloadManagerActivity.class));
          	break;
          default:
          	break;
  		}
  	}
  	
  	public void showMenu(){
  		if(mainMenu == null){
  			return;
  		}
  		if(mainMenu.isShowing()){
  			mainMenu.dismiss();
  		}else{
  			mainMenu.showAtLocation(this.findViewById(R.id.parent_view), Gravity.BOTTOM, 0, 0);
  		}
  	}
  	
  	private class MenuReceiver extends BroadcastReceiver{

  		@Override
  		public void onReceive(Context context, Intent intent) {
  			showMenu();
  		}
  	}
  	
  	private OnGlobalListener globalListener = new OnGlobalListener() {
		
		@Override
		public void onChangeState(Object... objects) {
			int len = objects.length;
			int index = (Integer)objects[0];
			if(index != getTabHost().getCurrentTab()){
				getTabHost().setCurrentTab(index);
			}
			if(len > 1){
				int type = (Integer)objects[1];
				ListenFactory.newInstance().changeState("changeNetFriendType", type);
			}
		}
	};
	
	

	/**
	 * 启动IM服务
	 */
	private XMPPService xmppService; 
	
	private void bindXmppService(){
		Intent intent = new Intent(MainActivity.this,XMPPService.class);
		bindService(intent,connection, Context.BIND_AUTO_CREATE);
	}
	
	 private ServiceConnection connection = new ServiceConnection() { 
	        public void onServiceDisconnected(ComponentName name) { 
	        	xmppService = null; 
	        } 
	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) { 
	        	xmppService = ((XMPPService.MyBinder) service).getMyService();
	            xmppService.execute(serviceHandler);
	        } 
	  }; 
	
	  
	  private Handler serviceHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
					case -1:{
						//Toast.makeText(MainActivity.this, "登录openfire失败", Toast.LENGTH_LONG).show();
					}break;
					case 1:{
						//Toast.makeText(MainActivity.this, "登录openfire成功", Toast.LENGTH_LONG).show();
						new Handler().postDelayed(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								int count=UserInfoManager.getInstance().getNoReadCount();
							     setTotalNoRead(count);
							}
						}, 2000);
						 
					}break;
				}
			}
	  };
	  
	  
	  
	  @SuppressLint("NewApi")
	private void getSignInfo(){
		  String pubKey = "";
		  try {
			  PackageInfo packageInfo=getPackageManager().getPackageInfo("com.zxtd.information.ui", PackageManager.GET_SIGNATURES);
			  Signature[] signs = packageInfo.signatures;
			  CertificateFactory certFactory = CertificateFactory.getInstance("X.509");	  
			  X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signs[0].toByteArray()));
			  pubKey = Base64.encodeToString(cert.getEncoded(), Base64.DEFAULT);
			  pubKey = MD5Tools.toMD5(pubKey);
			} catch (Exception e) {
				Utils.printException(e);
			} finally{
				if(!Constant.ZASE_APP_PUBLIC_KEY.equals(pubKey)){
					finish();
					System.exit(0);
				}
			}
	  }
	  
	  
	  
}
