package com.zxtd.information.ui.me;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zxtd.information.adapter.ImageAdapter;
import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.manager.AlbumsManager;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.MessageManager;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.MainActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.AlignLeftGallery;
import com.zxtd.information.ui.custview.CustomerScrollView;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.custview.PersonalScrollView;
import com.zxtd.information.ui.custview.RotateAnimation;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.ui.me.im.SessionsActivity;
import com.zxtd.information.ui.news.ShowWebImageActivity;
import com.zxtd.information.ui.post.MineCommentActivity;
import com.zxtd.information.ui.setting.ModifUserInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import com.zxtd.information.view.PictureDialog;

public class MineNewActivity extends BaseActivity implements 
	OnClickListener,AlignLeftGallery.IOnItemClickListener
	,AlignLeftGallery.OnItemLongClickListener
	/*,InterpolatedTimeListener,onTurnListener*/ {

	private AlignLeftGallery myGallery;
	private ImageAdapter adapter=null;
	private PictureDialog pictureDialog;
	private Uri currentImageUri = null;
	private static final int REQUEST_CODE_CAMERA=100;
	private static final int REQUEST_CODE_ALBUMS=101;
	private static final int REQUEST_CODE_UPLOAD_ALBUMS=102;
	private static final int MODIFY_USER_INFORMATION=103;
	private static final int CLEAR_PUBLIC_REDPOINT=104;
	
	private LoadingDialog dialog;
	private UserInfo info=null;
	//控件
	AsyncImageLoader loader=new AsyncImageLoader();
	private TextView txtNickName;
	private RoundAngleImageView header=null;
	private TextView txtSign;
	private TextView txtFansCount;
	private ImageView imgFansHasNew;
	private TextView txtFoucsCount;
	private ImageView imgFocusHasNew;
	private TextView txtLocation;
	private TextView txtWork;
	private TextView txtSex;
	private TextView txtAge; 
	private TextView txtpublicCount;
	private ImageView imgHasNewPublic;
	private TextView txtCommentCount;
	private ImageView imgHasNewComment;
	private TextView txtCollCount;
	private ImageView imgHasNewColl;
	private TextView txtImMsgCount;
	private ImageView imgHasNewImMsg;
	private ProgressDialog proDialog;
	private boolean isSecondBack=false;
	private ImageView imgAccountType;
	
	
	private PersonalScrollView personalScrollView;
	private ImageView iv_personal_bg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_main_new);//mine_main_new  mine_self_main
		loader.setSize(new ImageSize(100,100));
		initView();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		
		//缓存数据改变通知
		IntentFilter cacheFilter=new IntentFilter();
		cacheFilter.addAction(Constant.NOTIFY_CACHE_DATA_CHANGED);
		cacheFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(cacheReceiver, cacheFilter);
		
		IntentFilter imFilter=new IntentFilter();
		imFilter.addAction(Constant.REFRESH_SESSION);
		imFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(imReceiver, imFilter);
		
		newLoadData();
		
		/*
		String baseDire=Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/Dcode/code.jpg";
		if(new File(baseDire).exists()){
			String text=DimensionalCodeUtils.resolveDCodeImage(baseDire);
			Log.e(Constant.TAG, "二维码数据: "+text);
		}else{
			DimensionalCodeUtils.createDCodeImage("www.baidu.com");
		}
		*/
	}
	
	
	
	void initView(){
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText(R.string.my_home);
		
		Button btnCompose=(Button) findViewById(R.id.btn_compose);
		btnCompose.setOnClickListener(this);
		
		findViewById(R.id.mine_main_modify).setOnClickListener(this);
		
	
		
		LinearLayout fansLayout=(LinearLayout) findViewById(R.id.mine_main_fans);
		fansLayout.setOnClickListener(this);
		
		LinearLayout focusLayout=(LinearLayout) findViewById(R.id.mine_main_focus);
		focusLayout.setOnClickListener(this);
		
		findViewById(R.id.mine_main_mypublic).setOnClickListener(this);
		findViewById(R.id.mine_main_mycomment).setOnClickListener(this);
		findViewById(R.id.mine_main_mycollection).setOnClickListener(this);
		findViewById(R.id.mine_main_myletter).setOnClickListener(this);
		
		myGallery=(AlignLeftGallery) findViewById(R.id.myGallery);
		myGallery.setOnItemLongClickListener(this);
		myGallery.setSelection(0);
		myGallery.setOnItemClickListener(this);
		myGallery.setSpacing(10);
		
		pictureDialog = new PictureDialog(this);
		pictureDialog.setOnClickListener(clickListener);
		
		txtNickName=(TextView) findViewById(R.id.mine_self_nickName);
		imgAccountType=(ImageView) findViewById(R.id.mine_self_accountype);
		header=(RoundAngleImageView) findViewById(R.id.mine_self_header);
		header.setOnClickListener(this);	
		txtSign=(TextView) findViewById(R.id.mine_self_sign);
		txtFansCount=(TextView) findViewById(R.id.mine_self_fanscount);
		imgFansHasNew=(ImageView) findViewById(R.id.mine_self_fanscounthasnew);
		txtFoucsCount=(TextView) findViewById(R.id.mine_self_focuscount);
		imgFocusHasNew=(ImageView) findViewById(R.id.mine_self_focushasnew);
		txtLocation=(TextView) findViewById(R.id.mine_self_location);
		txtWork=(TextView) findViewById(R.id.mine_self_work);
		txtSex=(TextView) findViewById(R.id.mine_self_sex);
		txtAge=(TextView) findViewById(R.id.mine_self_age);
		
		txtpublicCount=(TextView) findViewById(R.id.mine_self_publiccount);
		imgHasNewPublic=(ImageView) findViewById(R.id.mine_self_hasnewpublic);
		txtCommentCount=(TextView) findViewById(R.id.mine_self_commentcount);
		imgHasNewComment=(ImageView) findViewById(R.id.mine_self_hasnewcomment);
		txtCollCount=(TextView) findViewById(R.id.mine_self_collectioncount);
		imgHasNewColl=(ImageView) findViewById(R.id.mine_self_hasnewcollection);
		txtImMsgCount=(TextView) findViewById(R.id.mine_self_immsgcount);
		imgHasNewImMsg=(ImageView) findViewById(R.id.mine_self_hasnewim);
		
		/*
		personalScrollView=(PersonalScrollView) findViewById(R.id.scroll_layout);
		personalScrollView.setVerticalScrollBarEnabled(false);
		personalScrollView.setHorizontalScrollBarEnabled(false);
		iv_personal_bg=(ImageView) findViewById(R.id.iv_personal_bg);
		personalScrollView.setTurnListener(this);
		personalScrollView.setImageView(iv_personal_bg);
		*/
		
		CustomerScrollView scrollView=(CustomerScrollView) findViewById(R.id.scroll_layout);
		scrollView.setVerticalScrollBarEnabled(false);
		scrollView.setHorizontalScrollBarEnabled(false);
		scrollView.setTurnListener(new CustomerScrollView.onTurnListener(){
			@Override
			public void onTurn() {
				// TODO Auto-generated method stub
				RotateAnimation animation = new RotateAnimation();
				animation.setFillAfter(true);
				header.startAnimation(animation);
			}
		});
	}


	private void initGalleryData(){
		List<Object> list=new ArrayList<Object>(8);
		list.addAll(info.getImgList());
		if(list.size()<Constant.MINE_PHOTO_SIZE){
			list.add(getResources().getDrawable(R.drawable.upload_new_photo));
		}
		adapter=new ImageAdapter(list, this);
		myGallery.setAdapter(adapter);
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.btn_compose:{
				Utils.jumpPubNews(this);
			}break;
			case R.id.mine_self_header:{
				if(null!=info){
					Intent intent=new Intent(MineNewActivity.this,SingelPhotoShowActivity.class);
					intent.putExtra("imgUrl", info.getHeader());
					startActivity(intent);
				}
			}break;
			case R.id.mine_main_modify:{
				Intent intent=new Intent(this,ModifUserInfoActivity.class);
				startActivityForResult(intent,MODIFY_USER_INFORMATION);
			}break;
			case R.id.mine_main_fans:{
				if(info.isHasNewFans()>0){
					boolean isFlag=UserInfoManager.getInstance().removeUserRedPoint("fansHasNew");
					if(isFlag){
						info.setHasNewFans(0);
						Constant.loginUser=info;
						sendNoReadCastReceiver();
					}
				}
				Intent intent=new Intent(MineNewActivity.this,FansAndFocusActivity.class);
				intent.putExtra("type", 0);
				intent.putExtra("from", "self");
				intent.putExtra("userId", getUserId());
				intent.putExtra("userName", "我的");
				startActivity(intent);
				imgFansHasNew.setVisibility(View.GONE);
			}break;
			case R.id.mine_main_focus:{
				Intent intent=new Intent(MineNewActivity.this,FansAndFocusActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("from", "self");
				intent.putExtra("userId", getUserId());
				intent.putExtra("userName", "我的");
				startActivity(intent);
			}break;
			case R.id.mine_main_mypublic:{
				Intent intent=new Intent(MineNewActivity.this,MyPublicActivity.class);
				intent.putExtra("userId",getUserId());
				intent.putExtra("userName", "我的");
				//startActivity(intent);
				startActivityForResult(intent,CLEAR_PUBLIC_REDPOINT);
			}break;
			case R.id.mine_main_mycomment:{
				if(info.isHasNewComment()>0){
					boolean flag=UserInfoManager.getInstance().removeUserRedPoint("hasNewComment");
					if(flag){
						imgHasNewComment.setVisibility(View.GONE);
						info.setHasNewComment(0);
						Constant.loginUser=info;
						sendNoReadCastReceiver();
					}
				}
				Intent intent=new Intent(MineNewActivity.this,MineCommentActivity.class);
				intent.putExtra("userId", getUserId());
				intent.putExtra("userName", "我的");
				startActivity(intent);
			}break;
			case R.id.mine_main_mycollection:{
				Intent intent=new Intent(MineNewActivity.this,MyCollectionActivity.class);
				intent.putExtra("userId", getUserId());
				intent.putExtra("userName", "我的");
				startActivity(intent);
			}break;
			case R.id.mine_main_myletter:{
				Intent intent=new Intent(MineNewActivity.this,SessionsActivity.class);
				startActivity(intent);
			}break;
		}
	}



	public void onItemClick(int position){
			Object obj=adapter.getItem(position);
			if(obj instanceof Drawable){
				pictureDialog.show();
			}else{
				Iterator<Object> it=adapter.getImageUrls().iterator();
				JSONArray array=new JSONArray();
				try{
					while(it.hasNext()){
						Object temp=it.next();
						if(temp instanceof NewImageBean){
							NewImageBean bean=(NewImageBean) temp;
							JSONObject jsonObj=new JSONObject();
							jsonObj.put("imageUrl", bean.getImageUrl());
							jsonObj.put("describe", bean.getDescribe());
							array.put(jsonObj);
						}
					}
					String json=array.toString();
					Intent intent=new Intent(MineNewActivity.this,ShowWebImageActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString(Constant.BundleKey.IMGE_DATA, json);
					bundle.putInt(Constant.BundleKey.IMAGE_INDEX, position);
					bundle.putBoolean("isCache", true);
					intent.putExtras(bundle);
					startActivity(intent);
				}catch(Exception ex){
					Utils.printException(ex);
				}
			}
	}

	
	private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 1:
				doTakePhoto();
				break;
			case 0:
				doPickPhotoFromGallery();
				break;
			}
		}
	};

	
	//从相机中获取
		private void doTakePhoto(){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file= new File(Constant.SDCARD_CAMERA_PATH, getPhotoFileName());
			currentImageUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
			this.startActivityForResult(intent, REQUEST_CODE_CAMERA);
		}
	
		
		public static String getPhotoFileName() {
	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
	        return dateFormat.format(date) + ".jpg";
		}
		
		//从相册中获取
		private void doPickPhotoFromGallery(){
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			this.startActivityForResult(intent, REQUEST_CODE_ALBUMS);
		}
		
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode != RESULT_OK){
				return;
			}
			String imagePath = null;
			if( requestCode == REQUEST_CODE_ALBUMS){
				imagePath = getPathFromPhoto(data);
				if(!Utils.isEmpty(imagePath)){
					uploadPhoto(imagePath);
				}
				
			}else if(requestCode == REQUEST_CODE_CAMERA){
				imagePath = getPathFromCamera();
				if(!Utils.isEmpty(imagePath)){
					uploadPhoto(imagePath);
				}
			}else if(requestCode==REQUEST_CODE_UPLOAD_ALBUMS){
				NewImageBean bean=data.getExtras().getParcelable("imgBean");
				adapter.add(bean);
				adapter.notifyDataSetChanged();
			}else if(requestCode==MODIFY_USER_INFORMATION){
				info=Constant.loginUser;
				setBaseValue();
			}else if(requestCode==CLEAR_PUBLIC_REDPOINT){
				int count=data.getIntExtra("noReadCount", 0);
				if(count==0){
					imgHasNewPublic.setVisibility(View.GONE);
					info.setHasNewPublish(0);
					Constant.loginUser=info;
					sendNoReadCastReceiver();
				}
			}
		}
		
	
		private void uploadPhoto(String imagePath){
			Intent intent=new Intent(MineNewActivity.this,ShowUploadActivity.class);
			intent.putExtra("filepath", imagePath);
			intent.putExtra("title", "相册上传");
			startActivityForResult(intent, REQUEST_CODE_UPLOAD_ALBUMS);
		}
		
		
		private String getPathFromCamera(){
			//this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, currentImageUri));
			return currentImageUri.getPath();
		}
		
		private String getPathFromPhoto(Intent data){
			if(data != null){
				Uri uri = data.getData();
				String[] projection = {MediaStore.Images.Media.DATA };
				Cursor cursor = this.managedQuery(uri, projection, null, null, null);
				cursor.moveToFirst();
				String path = cursor.getString(0);
				return path;
			}
			return null;
		}
		
		
		
		/**
		 * 加入缓存的个人主页
		 */
		private void newLoadData(){
			if(Utils.isNetworkConn()){
				if(CacheManager.CACHEVERSION.isEmpty()){
					new Thread(){
						@Override
						public void run() {
							CacheManager.getInstance().getLocalVersion();
							boolean isSuccess=CacheManager.getInstance().getVersions();
							Log.e(Constant.TAG, "获取用户版本号："+isSuccess+"   "+CacheManager.CACHEVERSION.size());
							handler.sendEmptyMessage(isSuccess ? 9 : 10);
						}
					}.start();
				}else{
					if(CacheManager.LOCALVERSION.isEmpty() || 
							CacheManager.LOCALVERSION.get(Constant.USER_VERSION_KEY)<CacheManager.CACHEVERSION.get(Constant.USER_VERSION_KEY)){
						loadData(1);
					}else{
						loadLocalData();
					}
				}
			}else{
				loadLocalData();
			}
	
		}
		
		
		/**
		 * 加在本地缓存数据
		 */
		private void loadLocalData(){
			dialog=new LoadingDialog(this, R.style.loaddialog);
			dialog.show();
			new Thread(){
				@Override
				public void run(){
					UserInfo userInfo=UserInfoManager.getInstance().getLocalUserInfo();
					Message msg=handler.obtainMessage();
					if(null==userInfo){
						msg.what=-1;
					}else{
						msg.what=8;
						msg.obj=userInfo;
					}
					msg.sendToTarget();
				}
			}.start();
		}
		
		
		/**
		 * 加载个人中心数据
		 */
		private void loadData(final int what){
			dialog=new LoadingDialog(this, R.style.loaddialog);
			dialog.show();
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					UserInfo info=UserInfoManager.getInstance().getUserInfo(MineNewActivity.this.getUserId(),-1, true);
					Message msg=handler.obtainMessage();
					if(null==info){
						msg.what=-1;
					}else{
						msg.what=what;
						msg.obj=info;
					}
					msg.sendToTarget();
				}
			}.start();
		}
		
		
		
		private Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
					case -1:{
						MineNewActivity.this.checkNerWork();
					}break;
					case 1:{
						info=(UserInfo) msg.obj;
						setValues();
						synchronousData();
					}break;
					case 2:{
						int deleteIndex=Integer.valueOf(msg.obj.toString());
						Toast("删除成功");
						
						NewImageBean bean=(NewImageBean) adapter.getItem(deleteIndex);
						AlbumsManager.getInstance().deleteImg(bean);
						
						adapter.remove(deleteIndex);
						adapter.notifyDataSetChanged();	
					}break;
					case 3:{
						Toast("删除失败");
					}break;
					case 4:{
						String headUrl=msg.obj.toString();
						info.setHeader(headUrl);
						Constant.loginUser=info;
						saveHeaderPreferences();
						Drawable drawable=loadImage(headUrl);
						if(null!=drawable){
							header.setImageDrawable(drawable); 
						}
						UserInfoManager.getInstance().setHead(headUrl);
						Toast("设置头像成功");
					}break;
					case 5:{
						Toast("设置头像失败");
					}break;
					case 6:{
						SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
						Editor editor=shareds.edit();
						editor.putBoolean("xmppRegisted", true);
						editor.commit();
					}break;
					case 7:{
						if(!Constant.isXmppLogin){
							Toast("注册服务器 失败");
							new Handler().postDelayed(new Runnable(){
								@Override
								public void run() {
									// TODO Auto-generated method stub
									registXmppServer();
								}
							}, 20000);
						}
					}break;
					case 8:{
						info=(UserInfo) msg.obj;
						setValues();
					}break;
					case 9:{
						if(CacheManager.CACHEVERSION.isEmpty()){
							loadData(11);
						}else{
							newLoadData();
						}
					}break;
					case 10:{
						loadData(11);
						//Toast("获取服务端版本号失败");
					}break;
					case 11:{
						info=(UserInfo) msg.obj;
						setValues();
					}break;
				}
				if(dialog!=null)
					dialog.dismiss();
				if(null!=proDialog)
					proDialog.dismiss();
			}
		};
		
		
		/**
		 * 
		 */
		private void synchronousData(){
			new Thread(){
				@Override
				public void run() {
					UserInfoManager.getInstance().addOrModifyUserInfo(info);
				}
			}.start();
		}
		
		
		private void setBaseValue(){
			txtNickName.setText(info.getNickName());
			Drawable drawable=loadImage(info.getHeader());
			if(null!=drawable){
				header.setImageDrawable(drawable);
			}
			txtSign.setText(info.getSign());
			txtLocation.setText(info.getArea());
			txtWork.setText(info.getWork());
			int temp=2;
			if(!TextUtils.isEmpty(info.getSex()) && !"null".equals(info.getSex())){
				temp=Integer.valueOf(info.getSex());
			}
			String sex=temp==0?"男":temp==1?"女":"保密";
			txtSex.setText(sex);
			
			String age="保密";
			String birth=info.getBirth();
			if(!TextUtils.isEmpty(birth) && !"null".equals(birth)){
				int oldYear=Integer.valueOf(birth.split("-")[0]);
				Calendar cal=Calendar.getInstance();
				int nowYear=cal.get(Calendar.YEAR);
				age=(nowYear-oldYear)+"岁";
			}
			txtAge.setText(age);
		}
		
		private void setValues(){
			Constant.loginUser=info;
			saveOpenfireAccount();
			initGalleryData();
			registXmppServer();
			setAccountTypeImg();
			setBaseValue();
			txtFansCount.setText(String.valueOf(info.getFansCount()));
			
			imgFansHasNew.setVisibility(info.isHasNewFans()>0 ? View.VISIBLE : View.GONE);
			txtFoucsCount.setText(String.valueOf(info.getAttentionCount()));
			imgFocusHasNew.setVisibility(View.GONE);
	
			txtpublicCount.setText(String.valueOf(info.getPublishCount()));
			imgHasNewPublic.setVisibility(info.getHasNewPublish()>0 ? View.VISIBLE : View.GONE);
			txtCommentCount.setText(String.valueOf(info.getCommentCount()));
			imgHasNewComment.setVisibility(info.isHasNewComment()>0 ? View.VISIBLE : View.GONE);
			txtCollCount.setText(String.valueOf(info.getCollectionCount()));
			imgHasNewColl.setVisibility(View.GONE);
			txtImMsgCount.setText(String.valueOf(info.getLetterCount()));
			imgHasNewImMsg.setVisibility(info.getLetterCount() >0 ? View.VISIBLE : View.GONE);
		}
		
		
	    private Drawable loadImage(String filePath){
	    	 return loader.loadDrawable(filePath,true, new ImageCallback(){
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						// TODO Auto-generated method stub
						if(null!=imageDrawable){
							header.setImageDrawable(imageDrawable);  
							header.setScaleType(ScaleType.FIT_XY);
						}
					}
		        });
	    };
	    
	    
	    private void sendNoReadCastReceiver(){
	    	int temp=info.isHasNewFans()+info.getHasNewPublish()+info.isHasNewComment()+info.getLetterCount();
			Intent newIntent=new Intent(Constant.UPDATE_TOTAL_NO_READ);
			newIntent.putExtra("count", temp);
			sendBroadcast(newIntent);
	    }
	    
	    
	    @Override
		public boolean onMenuOpened(int featureId, Menu menu) {
			this.sendBroadcast(new Intent(MainActivity.MENU_CLICK_ACTION));
			return false;
		}
	    
	    private BroadcastReceiver cacheReceiver=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if(Constant.NOTIFY_CACHE_DATA_CHANGED.equals(intent.getAction())){
					int type=intent.getIntExtra("type", 0); //0 发布 1收藏 2评论
					int data=intent.getIntExtra("data", 0);
					if(type==0){
						int count=info.getPublishCount()+data;
						info.setPublishCount(count);
						Constant.loginUser=info;
						txtpublicCount.setText(String.valueOf(count));
					}else if(type==1){
						int count=info.getCollectionCount()+data;
						info.setCollectionCount(count);
						Constant.loginUser=info;
						txtCollCount.setText(String.valueOf(count));
					}else if(type==2){
						int count=info.getCommentCount()+data;
						info.setCommentCount(count);
						Constant.loginUser=info;
						txtCommentCount.setText(String.valueOf(count));
					}		
				}
			}
	    };
	    
	    
	    private BroadcastReceiver imReceiver=new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if(Constant.REFRESH_SESSION.equals(intent.getAction())){
					int count=MessageManager.getInstance().getTotalNoReadCount();
					Log.e(Constant.TAG, "未读消息数量为:"+count);
					if(count>0){
						imgHasNewImMsg.setVisibility(View.VISIBLE);
					}else{
						imgHasNewImMsg.setVisibility(View.GONE);
					}
					txtImMsgCount.setText(String.valueOf(count));
					info.setLetterCount(count);
					Constant.loginUser=info;
					
					UserInfoManager.getInstance().setIMMessageCount(count);
					sendNoReadCastReceiver();
				}
			}
	    };
	    
	    
		private BroadcastReceiver receiver=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action=intent.getAction();
				if(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED.equals(action)){
					boolean isOpenfire=intent.getBooleanExtra("isOpenfire", false);
					if(isOpenfire){
						int type=intent.getIntExtra("type", 0);
						switch(type){
							case 0:{
								int data=intent.getIntExtra("data", 0);
								boolean isShow=intent.getBooleanExtra("isNotify", true);
								info.setHasNewFans(isShow ? 1:0);
								info.setFansCount(data);
								Constant.loginUser=info;
								txtFansCount.setText(String.valueOf(data));							
								imgFansHasNew.setVisibility(isShow ? View.VISIBLE : View.GONE);
							}break;
							case 1:{
								String ids=intent.getStringExtra("ids");
								boolean isShow=intent.getBooleanExtra("isNotify", true);
								info.setHasNewPublish(isShow ? 1:0);
								Constant.loginUser=info;
								imgHasNewPublic.setVisibility(isShow ? View.VISIBLE : View.GONE);
								//保存红点
								UserInfoManager.getInstance().setRedPoint("publicHasNew");
								PublicManager.getInstance().setPublicRedPoint(ids);
							}break;
							case 2:{
								boolean isShow=intent.getBooleanExtra("isNotify", true);
								info.setHasNewComment(isShow ? 1:0);
								Constant.loginUser=info;
								imgHasNewComment.setVisibility(isShow ? View.VISIBLE : View.GONE);
								UserInfoManager.getInstance().setRedPoint("hasNewComment");
							}break;
						}
						sendNoReadCastReceiver();
					}else{
						int t=intent.getIntExtra("type", 0);
						int focus=Integer.valueOf(txtFoucsCount.getText().toString());
						if(t==0){
							//取消
							focus--;
						}else if(t==1){
							focus++;
						}
						info.setAttentionCount(focus);
						txtFoucsCount.setText(String.valueOf(focus));
					}
				}
			}
		};

		
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3){
			if(null!=adapter && adapter.getCount()>0){
				Object obj=adapter.getItem(arg2);
				if(obj instanceof NewImageBean){
					final int delIndex=arg2;
					final NewImageBean bean=(NewImageBean) obj;
					AlertDialog.Builder builder=new AlertDialog.Builder(this);
					builder.setTitle("操作");
					builder.setItems(new String[]{"删除图片","设为头像"}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which){
							switch(which){
								case 0:{
									proDialog=MineNewActivity.this.showProgress(-1, "", "正在删除..");
									proDialog.show();
									new Thread(){
										@Override
										public void run() {
											super.run();
											int resultCode=UserInfoManager.getInstance().deletAlbum(bean.getImgId(), 1);
											Message msg=handler.obtainMessage();
											if(resultCode==1){
												msg.what=2;
												msg.obj=delIndex;
											}else{
												msg.what=3;
											}
											msg.sendToTarget();
										}
									}.start();
								}break;
								case 1:{
									proDialog=MineNewActivity.this.showProgress(-1, "", "正在设置头像");
									proDialog.show();
									new Thread(){
										@Override
										public void run() {
											// TODO Auto-generated method stub
											super.run();
											String headUrl=UserInfoManager.getInstance().setHead(MineNewActivity.this.getUserId(), bean.getImgId());
											Message msg=handler.obtainMessage();
											if(!TextUtils.isEmpty(headUrl)){
												msg.what=4;
												msg.obj=headUrl;
											}else{
												msg.what=5;
											}
											msg.sendToTarget();
										}
									}.start();
								}break;
							}
						}
					});
					
					builder.create().show();
				}
				
			}
			return false;
		}
	    
		
	    @Override
		public boolean onKeyDown(int keyCode, KeyEvent event){
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
		
	    
	    /**
	     * 设置账号类型图片
	     */
	    private void setAccountTypeImg(){
	    	switch(info.getAccountType()){
	    		case 0:{
	    			//imgAccountType.setImageResource(R.drawable.mine_sina);
	    		}break;
	    		case 1:{
	    			imgAccountType.setImageResource(R.drawable.mine_sina);
	    		}break;
	    		case 2:{
	    			imgAccountType.setImageResource(R.drawable.mine_qq);
	    		}break;
	    		case 3:{
	    			imgAccountType.setImageResource(R.drawable.renren);
	    		}break;
 	    	}
	    }
	    
	    
	    /**
	     * 注册xmpp服务器
	     */
	    private void registXmppServer(){
	    	SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
	    	boolean isRegist=shareds.getBoolean("xmppRegisted", false) || shareds.getBoolean("xmpp"+getUserId(), false);
	    	if(!isRegist){
	    		new Thread(){
					@Override
					public void run() {
						super.run();
						try{
							if(Utils.isNetworkConn() && !XmppUtils.getSimpleConnection().isAuthenticated()){
								String result=XmppUtils.regist(info);
								if("success".equals(result) || "exist".equals(result)){
									handler.sendEmptyMessage(6);
								}else{
									handler.sendEmptyMessage(7);
								}
							}
						}catch(Exception ex){
							Utils.printException(ex);
						}
					}
	    		}.start();
	    	}
	    }
	    
	    
	    /**
	     * 保存openfire账号信息
	     */
	    private void saveOpenfireAccount(){
	    	SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
	    	Editor editor=shareds.edit();
	    	editor.putString("header", info.getHeader());
			//editor.putInt("userId", info.getUserId());
			editor.putString("xmpppassword", info.getPassword());
			editor.commit();
	    }
	    
	    /**
	     * 用户修改头像后保存头像
	     */
	    private void saveHeaderPreferences(){
	    	SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
	    	Editor editor=shareds.edit();
	    	editor.putString("header", info.getHeader());
			editor.commit();
	    }


		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			unregisterReceiver(receiver);
			unregisterReceiver(cacheReceiver);
			unregisterReceiver(imReceiver);
		}


		
		
		/*
		@Override
		public void onTurn() {
			// TODO Auto-generated method stub
			RotateAnimation animation = new RotateAnimation();
			animation.setFillAfter(true);
			animation.setInterpolatedTimeListener(this);
			header.startAnimation(animation);
		}



		@Override
		public void interpolatedTime(float interpolatedTime) {
			// TODO Auto-generated method stub
			if (interpolatedTime > 0.5f) {
				
			}
		}
	    */
}
