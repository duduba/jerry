package com.zxtd.information.ui.me;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.zxtd.information.adapter.ImageAdapter;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.bean.Session;
import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.manager.FansFocusManager;
import com.zxtd.information.manager.SessionManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.AlignLeftGallery;
import com.zxtd.information.ui.custview.CustomerScrollView;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.custview.PersonalScrollView;
import com.zxtd.information.ui.custview.RotateAnimation;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.ui.me.im.ChatActivity;
import com.zxtd.information.ui.news.ShowWebImageActivity;
import com.zxtd.information.ui.post.MineCommentActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * 我的主页
 * */
public class OtherMainActivity extends BaseActivity implements 
		OnClickListener,AlignLeftGallery.IOnItemClickListener
		/*InterpolatedTimeListener,onTurnListener*/{

	AsyncImageLoader loader=new AsyncImageLoader();
	private ImageAdapter adapter=null;
	private LoadingDialog dialog;
	private int userId;
	
	private TextView txtTitle;
	private RoundAngleImageView imgHeader;
	private TextView txtNickName;
	private ImageView imgSex;
	private TextView txtBirth;
	private TextView txtLocation;
	private TextView txtWork;
	private TextView txtSign;
	
	private AlignLeftGallery myGallery;

	private TextView txtFansCount;
	private TextView txtFocusCount;
	private TextView txtPublicCount;
	private TextView txtCommentCount;
	private TextView txtCollectionCount;
	private LinearLayout btnAdd;
	private ImageView imgFocus;
	private TextView textFocus;
	private UserInfo info=null;
	private ProgressDialog proDialog=null;
	private static final int WRITE_IM_LETTER=97;
	private static final int ADD_CANCEL_FOCUS=98;
	
	private PersonalScrollView personalScrollView;
	private ImageView iv_personal_bg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_main);//mine_main  mine_other_main
		initView();
		loadData();
	}
	
	
	void initView(){
		Bundle bundle=getIntent().getExtras();
		txtTitle=(TextView) findViewById(R.id.title);
		if(bundle.containsKey("bean")){
			FansFocusBean bean=bundle.getParcelable("bean");
			txtTitle.setText(bean.getNickName());
			titleToCenter(txtTitle);
			userId=bean.getUserId();
		}
		
		findViewById(R.id.back).setOnClickListener(this);
		
		myGallery=(AlignLeftGallery) findViewById(R.id.myGallery);
		myGallery.setSelection(0);
		myGallery.setOnItemClickListener(this);
		myGallery.setSpacing(10); 
		
		imgHeader=(RoundAngleImageView) findViewById(R.id.avatar_imgbtn);
		imgHeader.setOnClickListener(this);	
		
		txtNickName=(TextView) findViewById(R.id.mine_other_nickname);
		imgSex=(ImageView) findViewById(R.id.mine_other_sex);
		txtBirth=(TextView) findViewById(R.id.mine_other_birth);
		txtLocation=(TextView) findViewById(R.id.mine_other_location);
		txtWork=(TextView) findViewById(R.id.mine_other_work);
		txtSign=(TextView) findViewById(R.id.mine_other_sign);
		
		findViewById(R.id.mine_other_fans).setOnClickListener(this);
		txtFansCount=(TextView) findViewById(R.id.mine_other_fans_count);
		findViewById(R.id.mine_other_focus).setOnClickListener(this);
		txtFocusCount=(TextView) findViewById(R.id.mine_other_focus_count);
		findViewById(R.id.mine_other_public).setOnClickListener(this);
		txtPublicCount=(TextView) findViewById(R.id.mine_other_public_count);
		findViewById(R.id.mine_other_comment).setOnClickListener(this);
		txtCommentCount=(TextView) findViewById(R.id.mine_other_comment_count);
		findViewById(R.id.mine_other_coll).setOnClickListener(this);
		txtCollectionCount=(TextView) findViewById(R.id.mine_other_coll_count);
		
		LinearLayout btnWrite=(LinearLayout)findViewById(R.id.mine_other_btn_write);
		btnWrite.setOnClickListener(this);
		btnAdd=(LinearLayout) findViewById(R.id.mine_other_btn_add);
		imgFocus=(ImageView) findViewById(R.id.mine_other_btn_focus_img);
		textFocus=(TextView) findViewById(R.id.mine_other_btn_focus_text);
		btnAdd.setOnClickListener(this);
		
		if(userId==XmppUtils.getUserId()){
			btnAdd.setVisibility(View.GONE);
			btnWrite.setVisibility(View.GONE);
		}
		
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
				imgHeader.startAnimation(animation);
			}
		});
	}


	
	private void initGalleryData(){
		List<Object> list=new ArrayList<Object>(10);
		list.addAll(info.getImgList());
		adapter=new ImageAdapter(list, this);
		myGallery.setAdapter(adapter);
	}
	

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.avatar_imgbtn:{
				if(null!=info){
					Intent intent=new Intent(OtherMainActivity.this,SingelPhotoShowActivity.class);
					intent.putExtra("imgUrl", info.getHeader());
					startActivity(intent);
				}
			}break;
			case R.id.mine_other_fans:{
				if(null==info){
					return;
				}
				Intent intent=new Intent(OtherMainActivity.this,FansAndFocusActivity.class);
				intent.putExtra("type", 0);
				intent.putExtra("from", "other");
				intent.putExtra("userId", userId);
				intent.putExtra("userName", info.getNickName()+"的");
				startActivity(intent);
			}break;
			case R.id.mine_other_focus:{
				if(null==info){
					return;
				}
				Intent intent=new Intent(OtherMainActivity.this,FansAndFocusActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("from", "other");
				intent.putExtra("userId", userId);
				intent.putExtra("userName", info.getNickName()+"的");
				startActivity(intent);
			}break;
			case R.id.mine_other_public:{
				if(null==info){
					return;
				}
				Intent intent=new Intent(OtherMainActivity.this,MyPublicActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("userName", info.getNickName()+"的");
				startActivity(intent);
			}break;
			case R.id.mine_other_comment:{
				if(null==info){
					return;
				}
				Intent intent=new Intent(OtherMainActivity.this,MineCommentActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("userName", info.getNickName()+"的");
				startActivity(intent);
			}break;
			case R.id.mine_other_coll:{
				if(null==info){
					return;
				}
				Intent intent=new Intent(OtherMainActivity.this,MyCollectionActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("userName", info.getNickName()+"的");
				startActivity(intent);
			}break;
			case R.id.mine_other_btn_write:{
				if(null!=info){
					writeLatter();
				}
			}break;
			case R.id.mine_other_btn_add:{
				if(null==info){
					return;
				}
				if(XmppUtils.getUserId()!=-1){
					String msg="正在添加关注..";
					if(info.isHasAttention()){
						msg="正在取消关注..";
					}
					proDialog=OtherMainActivity.this.showProgress(-1, "", msg);
					proDialog.show();
					//取消关注
					new Thread(){
						@Override
						public void run() {
							super.run();
							FansFocusManager manager=new FansFocusManager();
							int what=-1;
							if(!info.isHasAttention()){
								what=manager.addFocus(getUserId(), info.getUserId(), info.getNickName());
							}else{
								what=manager.cancelFocus(getUserId(), info.getUserId(),0);
							}
							Message msg=handler.obtainMessage();
							msg.what=what;
							msg.sendToTarget();
						}
					}.start();
				}else{
					gotoLogin(ADD_CANCEL_FOCUS);
				}
			}break;
			 
		}
	}

	
	/**
	 * 写私信
	 */
	private void writeLatter(){
		if(XmppUtils.getUserId()!=-1){
			if(SessionManager.getInstance().checkSessionExtis(String.valueOf(info.getUserId()), getUserId())){
				Session session=null;
				if(Constant.CACHESESSION.containsKey(String.valueOf(info.getUserId()))){
					session=Constant.CACHESESSION.get(String.valueOf(info.getUserId()));
				}else{
					session=SessionManager.getInstance().getSessionById(info.getUserId());
				}
				forwardChatActivity(session);
			}else{
				int _id=SessionManager.getInstance().addSession(String.valueOf(info.getUserId()), 
						info.getNickName(), info.getHeader(), info.getSign(), "", TimeUtils.getNow(), 0, getUserId());
				if(_id>0){
					Session session=SessionManager.getInstance().getSessionById(_id);
					if(null!=session){
						Constant.CACHESESSION.put(session.getSessionName(), session);
						Intent intent=new Intent(Constant.REFRESH_SESSION);
						intent.putExtra("type", "add");
						intent.putExtra("session", session);
						sendBroadcast(intent);
						//加入缓存
						Constant.CACHESESSION.put(session.getSessionName(), session); 
						forwardChatActivity(session);
					}
				}
			}
		}else{
			gotoLogin(WRITE_IM_LETTER);
		}
	}
	
	private void gotoLogin(int requestCode){
		Toast.makeText(this, "您还未登陆,请先登录..", Toast.LENGTH_LONG).show();
		Intent intent=new Intent(this,LoginActivity.class);
		intent.putExtra("from", "search");
		startActivityForResult(intent, requestCode);
	}
	
	
	private void forwardChatActivity(Session session){
		Intent newIntent=new Intent(OtherMainActivity.this,ChatActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("session", session);
		newIntent.putExtras(bundle);
		startActivity(newIntent);
		finish();
	}
	
	
	
	
	/*
    private View.OnTouchListener touchListener=new View.OnTouchListener(){
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {  
				case MotionEvent.ACTION_DOWN:  
		        	//Log.v(Constant.TAG, "在down时候记录当前位密密麻麻密密麻麻妈妈置"); 
		             startY = (int) event.getY();
		             Log.e("news", startY+" ssssssssssssssssss");
		          break;  
		          case MotionEvent.ACTION_UP:  
		        	  MineActivity.this.release();
		          break; 
		          case MotionEvent.ACTION_MOVE:  
		                int tempY = (int) event.getY();  
		                int moveY=tempY-startY;
		                Log.e("news", moveY+"mmmmmmmmmmmmmmmmmmmm");
		                RelativeLayout.LayoutParams params=
		        				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		                params.setMargins(0, -(marginTop-moveY), 0, 0);
		                imgBg.setLayoutParams(params);
		                
		                break;  
	        }                  
			return false;
		}
		
	};
	
	public void runAnimation() {

		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f);
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f);
		right.setDuration(25000);
		left.setDuration(25000);
		right.setFillAfter(true);
		left.setFillAfter(true);

		right.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				imgBg.startAnimation(left);
			}
		});
		left.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				imgBg.startAnimation(right);
			}
		});
		imgBg.startAnimation(right);
	}

	private void release(){
		int screenHeight=getScreenSize()[1];
		RelativeLayout.LayoutParams params=
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		marginTop=screenHeight/3*2;
		params.setMargins(0,-marginTop , 0, 0);
		imgBg.setLayoutParams(params);
	}
	*/

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==ADD_CANCEL_FOCUS){
				loadData();
			}
		}
	}


	private void loadData(){
		dialog=new LoadingDialog(this, R.style.loaddialog);
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				UserInfo info=UserInfoManager.getInstance().getUserInfo(userId,OtherMainActivity.this.getUserId(), false);
				Message msg=handler.obtainMessage();
				if(null==info){
					msg.what=-1;
				}else{
					msg.what=10;
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
				case -2:{
					boolean isConn=Utils.isNetworkConn();
					if(isConn){
						Toast.makeText(OtherMainActivity.this, "程序接口 错误", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(OtherMainActivity.this, "网络不稳定或已断开", Toast.LENGTH_LONG).show();
					}
				}break;
				case -1:{
					OtherMainActivity.this.checkNerWork();
				}break;
				//已关注
				case 0:{
					
				}
				//已互相关注
				case 1:{
					if(info.isHasAttention()){//取消关注后
						Toast("取消关注成功..");
						info.setHasAttention(false);
						imgFocus.setImageResource(R.drawable.userinfo_relationship_indicator_plus);
						textFocus.setText("添加关注");
						textFocus.setTextColor(Color.parseColor("#32963C"));
						BCnotifyDataChange(0,info.getUserId());
					}else{//添加关注后
						Toast("添加关注成功..");
						info.setHasAttention(true);
						imgFocus.setImageResource(R.drawable.userinfo_relationship_indicator_tick_unfollow);
						textFocus.setText("取消关注");
						textFocus.setTextColor(Color.parseColor("#5b5a5a"));
						BCnotifyDataChange(1,info.getUserId());
					}
				}break;
				case 10:{
					info=(UserInfo) msg.obj;
					initGalleryData();
					String nickName=info.getNickName();
					txtTitle.setText(nickName);
					titleToCenter(txtTitle);
					txtNickName.setText(!Utils.isEmpty(nickName) && nickName.length()>10 ? nickName.substring(0, 10)+".." : nickName);
					Drawable drawable=loadImage(info.getHeader());
					if(null!=drawable){
						imgHeader.setImageDrawable(drawable);  
						imgHeader.setScaleType(ScaleType.FIT_XY);
					}
					txtLocation.setText("所在地:"+info.getArea());
					txtWork.setText("职业:"+info.getWork());
					if("0".equals(info.getSex())){
						imgSex.setImageResource(R.drawable.lbs_male);
					}
					String age="保密";
					String birth=info.getBirth();
					if(!TextUtils.isEmpty(birth) && !"null".equals(birth)){
						int oldYear=Integer.valueOf(birth.split("-")[0]);
						Calendar cal=Calendar.getInstance();
						int nowYear=cal.get(Calendar.YEAR);
						age=(nowYear-oldYear)+"岁";
					}
					txtBirth.setText("年龄:"+age);
					txtSign.setText(info.getSign());
					txtFansCount.setText(String.valueOf(info.getFansCount()));
					txtFocusCount.setText(String.valueOf(info.getAttentionCount()));
					txtPublicCount.setText(String.valueOf(info.getPublishCount()));
					txtCommentCount.setText(String.valueOf(info.getCommentCount()));
					txtCollectionCount.setText(String.valueOf(info.getCollectionCount()));
					if(info.isHasAttention()){
						imgFocus.setImageResource(R.drawable.userinfo_relationship_indicator_tick_unfollow);
						textFocus.setText("取消关注");
						textFocus.setTextColor(Color.parseColor("#5b5a5a"));
					}
				}break;
			}
			if(dialog!=null)
				dialog.dismiss();
			if(proDialog!=null)
				proDialog.dismiss();
		}
	};
	
	
	/**
	 * 广播通知数据改变
	 * type 1 添加关注
	 * type 0取消关注
	 * 
	 */
	private void BCnotifyDataChange(int type,int userId){
		Intent intent=new Intent(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED);
		intent.putExtra("from", 0);
		intent.putExtra("type", type);
		intent.putExtra("isOpenfire", false);
		intent.putExtra("userId", userId);
		sendBroadcast(intent);
	}
	
    private Drawable loadImage(String filePath){
   	 	return loader.loadDrawable(filePath,false, new ImageCallback(){
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				if(null!=imageDrawable){
					imgHeader.setImageDrawable(imageDrawable);  
					imgHeader.setScaleType(ScaleType.FIT_XY);
				}
			}
	    });
   }


	@Override
	public void onItemClick(int position){
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
			Intent intent=new Intent(OtherMainActivity.this,ShowWebImageActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString(Constant.BundleKey.IMGE_DATA, json);
			bundle.putInt(Constant.BundleKey.IMAGE_INDEX, position);
			intent.putExtras(bundle);
			startActivity(intent);
		}catch(Exception ex){
			Utils.printException(ex);
		}
	}


	/*
	@Override
	public void onTurn() {
		// TODO Auto-generated method stub
		RotateAnimation animation = new RotateAnimation();
		animation.setFillAfter(true);
		animation.setInterpolatedTimeListener(this);
		imgHeader.startAnimation(animation);
	}


	@Override
	public void interpolatedTime(float interpolatedTime) {
		// TODO Auto-generated method stub
		// 监听到翻转进度过半时，更新图片内容，
		if (interpolatedTime > 0.5f) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					drawable_id[current_id]);
			image_header.setImageBitmap(bitmap);
			
		}		
	}*/
	
}
