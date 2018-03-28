package com.zxtd.information.ui.me;

import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zxtd.information.adapter.PublicAdapter;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.PublicBean;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.CurrentManager;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.ui.pub.PublishActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class MyPublicActivity extends BaseActivity implements 
		OnClickListener,ListView.OnItemClickListener,ListView.OnItemLongClickListener{

	private LoadingDialog dialog;
	private int pageIndex=0;
	private TextView footerMore;
	private LinearLayout footerLoad;
	private boolean isFooterLoad=false;
	private ListView listview;
	public PublicAdapter adapter;
	private int currentPosition=-1;
	private int userId=-1;
	private ProgressDialog proDialog;
	private String userTitle="";
	private int selectIndex=-1;
	
	private static final int MODIFY_PUBLIC_DATA=100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_mypublic_layout);
		userId=getIntent().getIntExtra("userId", -1);
		initView();
		loadData();
		
		if(userId==XmppUtils.getUserId()){
			//缓存数据改变通知
			IntentFilter cacheFilter=new IntentFilter();
			cacheFilter.addAction(Constant.NOTIFY_CACHE_DATA_CHANGED);
			cacheFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
			registerReceiver(cacheReceiver, cacheFilter);
		}
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_compose).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		
		userTitle=getIntent().getStringExtra("userName");
		txtTitle.setText(userTitle+"发布");
		
		listview=(ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		if(userTitle.equals("我的") && userId==XmppUtils.getUserId()){
			listview.setOnItemLongClickListener(this);	
		}
		initFooter();
	}

	
	private void initFooter(){
		LinearLayout footer=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.listview_footer_load, null);
		footer.setBackgroundResource(R.drawable.personal_list_item_bg_normal);
		footerMore=(TextView) footer.findViewById(R.id.list_footer_more);
		footerLoad=(LinearLayout) footer.findViewById(R.id.list_footer_loading);
		footerMore.setOnClickListener(this);
		footer.setBackgroundResource(R.drawable.list_selector);
		listview.addFooterView(footer);
	}
	
	
	private void loadData(){
		if(!isFooterLoad){
			dialog=new LoadingDialog(this, R.style.loaddialog);
			dialog.show();
		}
		new Thread(){
			@Override
			public void run(){
				if(Utils.isNetworkConn() && pageIndex==0 && XmppUtils.getUserId()==userId && 
						CacheManager.LOCALVERSION.get(Constant.PUBLIC_VERSION_KEY)<CacheManager.CACHEVERSION.get(Constant.PUBLIC_VERSION_KEY)){
					boolean isSuccess=PublicManager.getInstance().clearPublic();
					if(isSuccess){
						isSuccess=PublicManager.getInstance().synchronizedData();
						if(isSuccess){
							CacheManager.getInstance().modifyVersion(Constant.PUBLIC_VERSION_KEY, CacheManager.CACHEVERSION.get(Constant.PUBLIC_VERSION_KEY));
						}
					}
				}
				loadDataWithPage();
			}
			
		}.start();
	}
	
	
	private void loadDataWithPage(){
		List<PublicBean> list=PublicManager.getInstance().getPublicInfo(userId, pageIndex);
		Message msg=handler.obtainMessage();
		if(null==list){
			msg.what=-1;
		}else{
			msg.what=1;
			msg.obj=list;
		}
		msg.sendToTarget();
	}
	
	
	
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case -1:{
					MyPublicActivity.this.checkNerWork();
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<PublicBean> list=(List<PublicBean>) msg.obj;
					int count=list.size();
					pageIndex++;
					if(null==adapter){
						adapter=new PublicAdapter(MyPublicActivity.this,list);
						listview.setAdapter(adapter);
					}else{
						adapter.addData(list);
						adapter.notifyDataSetChanged();
						
						footerMore.setVisibility(View.VISIBLE);
						footerLoad.setVisibility(View.GONE);
						isFooterLoad=false;
					}
					if(count<Constant.PAGESIZE){
						footerMore.setText("已显示全部");
						footerMore.setEnabled(false);
					}else{
						footerMore.setText("查看更多");
						footerMore.setEnabled(true);
					}
				}break;
				case 2:{
					final PublicBean bean=(PublicBean) msg.obj; 
					boolean isSuccess=PublicManager.getInstance().deleteLocal(bean);
					if(isSuccess){
						adapter.getDataList().remove(bean);
    					adapter.notifyDataSetChanged();
    					
						Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
						intent.putExtra("type", 0);
						intent.putExtra("data", -1);
						sendBroadcast(intent);
					}
					/*
					Log.e(Constant.TAG, "ssssssssssssssssssssssssssssssssssssss: "+selectIndex);
					Animation animation=AnimationUtils.loadAnimation(MyPublicActivity.this, R.anim.left_to_out);
	    			animation.setAnimationListener(new AnimationListener() {
	    				public void onAnimationStart(Animation animation) {
	    				}
	    				public void onAnimationRepeat(Animation animation) {				
	    				}
	    				public void onAnimationEnd(Animation animation) {
	    					
	    				}
	    			});
	    			listview.getChildAt(selectIndex).startAnimation(animation);
	    			animation.reset();
	    			*/
					Toast("删除成功");
				}break;
				case 3:{
					Toast("删除发布失败");
				}break;
			}
			if(null!=dialog && dialog.isShowing())
				dialog.dismiss();
			if(null!=proDialog && proDialog.isShowing())
				proDialog.dismiss();
		}	
	};
	
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.btn_compose:{
				Utils.jumpPubNews(this);
			}break;
			case R.id.list_footer_more:{
				footerMore.setVisibility(View.GONE);
				footerLoad.setVisibility(View.VISIBLE);
				isFooterLoad=true;
				loadDataWithPage();
			}break;
		}
	}

	
	@Override
	public void finish() {
		if(userTitle.equals("我的") && userId==XmppUtils.getUserId()){
			int count=PublicManager.getInstance().getNoReadCount();
			if(count==0){
				UserInfoManager.getInstance().removeUserRedPoint("publicHasNew");	
			}
			Intent intent=new Intent();
			intent.putExtra("noReadCount", count);
			setResult(RESULT_OK,intent);
		}
		super.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		/*Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(40);
		if(currentPosition==arg2){
			currentPosition=-1;
		}else{
			currentPosition=arg2;
		}
		adapter.setCurrentPosition(currentPosition);
		adapter.notifyDataSetChanged();
		*/
		if(arg2<adapter.getCount()){
			PublicBean bean=(PublicBean) adapter.getItem(arg2);
			if(bean.isHasNewReplay()){
				boolean flag=PublicManager.getInstance().setIsReaded(bean.getNewsId());
				if(flag){
					bean.setHasNewReplay(false);
					adapter.notifyDataSetChanged();
				}
			}
			Intent intent=new Intent(MyPublicActivity.this,NewInfoActivity.class);
			NewBean newBean = new NewBean();
			newBean.newId =String.valueOf(bean.getNewsId());
			newBean.postCount = String.valueOf(bean.getReplayCount());
			newBean.infoUrl =bean.getNewsUrl();
			CurrentManager.newInstance().setCurrentBean(newBean);
			Utils.netFriendClickedItem.add(newBean.newId);
			intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.NET_FRIEND_TYPE);
			startActivity(intent);
		}
	}
	
	
	
    private BroadcastReceiver cacheReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(Constant.NOTIFY_CACHE_DATA_CHANGED.equals(intent.getAction())){
				int type=intent.getIntExtra("type", 0); //0 发布 1收藏
				int data=intent.getIntExtra("data", 0);
				if(type==0 && data==1){
					pageIndex=0;
					if(null!=adapter){
						adapter.clear();
						adapter.notifyDataSetChanged();
					}
					loadData();
				}		
			}
		}
    };

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(userId==XmppUtils.getUserId()){
			unregisterReceiver(cacheReceiver);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if(arg2<adapter.getCount()){
			selectIndex=arg2;
			final PublicBean bean=(PublicBean) adapter.getItem(arg2);
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("操作");
			String[] items=new String[]{"编辑","删除"};
			builder.setItems(items, new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch(which){
						case 0:{
							Intent intent=new Intent(MyPublicActivity.this,PublishActivity.class);
							intent.putExtra("id", bean.getNewsId());
							startActivityForResult(intent,MODIFY_PUBLIC_DATA);
						}break;
						case 1:{
							deleteWeibo(bean);
						}break;
					}
				}
			});
			builder.create().show();
		}
		return false;
	}
	
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==MODIFY_PUBLIC_DATA){
				PublicBean bean=data.getParcelableExtra("publicBean");
				Iterator<PublicBean> it=adapter.getDataList().iterator();
				while(it.hasNext()){
					PublicBean temp=it.next();
					if(temp.getNewsId()==bean.getNewsId()){
						temp.setNewsTitle(bean.getNewsTitle());
						temp.setChannel(bean.getChannel());
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
	}

	/**
	 * 删除
	 * @param bean
	 */
	private void deleteWeibo(final PublicBean bean){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("确定删除吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				proDialog=new ProgressDialog(MyPublicActivity.this);
				proDialog.setCancelable(true);
				proDialog.setIndeterminate(false);
				proDialog.setMessage("正在删除..");
				proDialog.show();
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						int resultCode=PublicManager.getInstance().doDelete(bean.getNewsId());
						Message msg=handler.obtainMessage();
						if(resultCode==1){
							msg.what=2;
							msg.obj=bean;
						}else{
							msg.what=3;
						}
						msg.sendToTarget();
					}				
				}.start();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.create().show();
	}
    
    
}
