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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxtd.information.adapter.CollectionAdapter;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.NewsInfo;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.MyCollectionManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

import com.zxtd.information.ui.custview.FilpperListvew;
import com.zxtd.information.ui.custview.FilpperListvew.FilpperDeleteListener;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

public class MyCollectionActivity extends BaseActivity implements 
	OnClickListener,FilpperListvew.OnItemLongClickListener,FilpperListvew.OnItemClickListener{

	private LoadingDialog dialog;
	private int pageIndex=0;
	private TextView footerMore;
	private LinearLayout footerLoad;
	private boolean isFooterLoad=false;
	private FilpperListvew listview;
	private CollectionAdapter adapter=null;
	private int width;
	private ProgressDialog proDialog=null;
	private int selectIndex=-1;
	private int userId=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_mycollection_layout);
		userId=getIntent().getIntExtra("userId", -1);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		initView();
		loadData();
		
		IntentFilter filter=new IntentFilter(Constant.REFRESH_COLLECTION_DATA);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		String userTitle=getIntent().getStringExtra("userName");
		txtTitle.setText(userTitle+"收藏");
		
		listview=(FilpperListvew) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		if(userTitle.equals("我的") && userId==XmppUtils.getUserId()){
			listview.setOnItemLongClickListener(this);
			listview.setFilpperDeleteListener(listener);
		}
		
		initFooter();
	}

	
	private BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Constant.REFRESH_COLLECTION_DATA)){
				int newId=Integer.valueOf(intent.getStringExtra("newsId"));
				if(adapter!=null){
					Iterator<NewsInfo> it=adapter.getDataList().iterator();
					while(it.hasNext()){
						NewsInfo info=it.next();
						if(info.getNewsId()==newId){
							it.remove();
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
	};
	
	
	
	private void initFooter(){
		LinearLayout footer=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.listview_footer_load, null);
		footer.setBackgroundResource(R.drawable.personal_list_item_bg_normal);
		footerMore=(TextView) footer.findViewById(R.id.list_footer_more);
		footerLoad=(LinearLayout) footer.findViewById(R.id.list_footer_loading);
		footerMore.setOnClickListener(this);
		footer.setBackgroundResource(R.drawable.list_selector);
		listview.addFooterView(footer);
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.list_footer_more:{
				footerMore.setVisibility(View.GONE);
				footerLoad.setVisibility(View.VISIBLE);
				isFooterLoad=true;
				loadDataWithPage();
			}break;
		}
	}
	
	
	private void loadData(){
		if(!isFooterLoad){
			dialog=new LoadingDialog(this, R.style.loaddialog);
			dialog.show();
		}
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(Utils.isNetworkConn() && pageIndex==0 && XmppUtils.getUserId()==userId && 
						CacheManager.LOCALVERSION.get(Constant.COLLECTION_VERSION_KEY)<CacheManager.CACHEVERSION.get(Constant.COLLECTION_VERSION_KEY)){
					boolean isSuccess=MyCollectionManager.getInstance().clearCollection();
					if(isSuccess){
						isSuccess=MyCollectionManager.getInstance().synchronizedData();
						if(isSuccess){
							CacheManager.getInstance().modifyVersion(Constant.COLLECTION_VERSION_KEY, CacheManager.CACHEVERSION.get(Constant.COLLECTION_VERSION_KEY));
						}
					}
				}
				loadDataWithPage();
			}
			
		}.start();
	}
	
	
	private void loadDataWithPage(){
		List<NewsInfo> list=MyCollectionManager.getInstance().getCollection(userId, pageIndex);
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
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					MyCollectionActivity.this.checkNerWork();
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<NewsInfo> list=(List<NewsInfo>) msg.obj;
					int count=list.size();
					pageIndex++;
					if(null==adapter){
						adapter=new CollectionAdapter(MyCollectionActivity.this,list);
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
					int resultCode=(Integer) msg.obj;
					switch(resultCode){
						case 0:{
							Toast("删除失败");
						}break;
						case 1:{
							NewsInfo info=(NewsInfo) adapter.getItem(selectIndex);
							boolean isFlag=MyCollectionManager.getInstance().deleteLocalCollection(info.getNewsId(),info.getNewsType());
							if(isFlag){
								Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
								intent.putExtra("type", 1);
								intent.putExtra("data", -1);
								sendBroadcast(intent);
							}
							Animation animation=AnimationUtils.loadAnimation(MyCollectionActivity.this, R.anim.left_to_out);
			    			animation.setAnimationListener(new AnimationListener() {
			    				public void onAnimationStart(Animation animation) {
			    				}
			    				public void onAnimationRepeat(Animation animation) {				
			    				}
			    				public void onAnimationEnd(Animation animation) {
			    					adapter.remove(selectIndex);
									adapter.notifyDataSetChanged();
			    				}
			    			});
			    			listview.getChildAt(selectIndex).startAnimation(animation);
			    			animation.reset();
							Toast("删除成功");
						}break;
					}
				}break;
			}
			if(null!=proDialog){
				proDialog.dismiss();
			}
			if(null!=dialog && dialog.isShowing())
				dialog.dismiss();
		}
	};

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(arg2<adapter.getCount()){
			selectIndex=arg2;
			final NewsInfo news=(NewsInfo) adapter.getItem(arg2);
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("删除收藏");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					proDialog=MyCollectionActivity.this.showProgress(-1, "", "正在删除..");
					proDialog.show();
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							int resultCode=MyCollectionManager.getInstance().deleteColl(MyCollectionActivity.this.getUserId(), news.getNewsId(),news.getNewsType());
							Message msg=handler.obtainMessage();
							if(resultCode==-1){
								msg.what=-1;
							}else{
								msg.what=2;
								msg.obj=resultCode;
							}
							msg.sendToTarget();
						}
					}.start();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		return false;
	}

	
	int deleteIndex=-1;
	
	public void setDeleteIndex(int deleteIndex) {
		this.deleteIndex = deleteIndex;
	}


	private FilpperDeleteListener listener=new FilpperDeleteListener() {
		public void filpperDelete(float xPosition,float yPosition) {
			//listview中要有item，否则返回
			if(listview.getChildCount() == 0)
				return ;
			//根据坐标获得滑动删除的item的index
		    int index =  listview.pointToPosition((int)xPosition, (int)yPosition);
		    if(index!=AdapterView.INVALID_POSITION && index<adapter.getCount()){
		    	 //int firstVisiblePostion = listview.getFirstVisiblePosition();
				 if(deleteIndex!=-1){
				    index=deleteIndex;
				 }
				 NewsInfo info=(NewsInfo) adapter.getItem(index);
				 boolean flag=!info.isSelected();
				 if(flag){
					  deleteIndex=index;
				 }else{
					deleteIndex=-1;
				 }
				 info.setSelected(flag);
				 adapter.notifyDataSetChanged();
		    }
		    //一下两步是获得该条目在屏幕显示中的相对位置，直接根据index删除会空指針异常。因为listview中的child只有当前在屏幕中显示的才不会为空
		   
		    /*
		    View view = listview.getChildAt(index - firstVisiblePostion);
			TranslateAnimation tranAnimation = new TranslateAnimation(0,width,0,0);
			tranAnimation.setDuration(500);
			tranAnimation.setFillAfter(true);
			view.startAnimation(tranAnimation);
			//当动画播放完毕后，删除。否则不会出现动画效果（自己试验的）。
			tranAnimation.setAnimationListener(new AnimationListener(){
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					//删除一个item
					adapter.remove(index);
				    adapter.notifyDataSetChanged();
				}
			});
			*/
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg2<adapter.getCount()){
			NewsInfo info=(NewsInfo) adapter.getItem(arg2);
			Intent intent=new Intent(this,NewInfoActivity.class);
			NewBean newBean = new NewBean();
			newBean.newId =String.valueOf(info.getNewsId());
			newBean.postCount = String.valueOf(info.getNewReplyCount());
			newBean.infoUrl =info.getNewsUrl();
			//CurrentManager.newInstance().setCurrentBean(newBean);
			Utils.netFriendClickedItem.add(newBean.newId);
			intent.putExtra(Constant.BundleKey.NEWBEAN, newBean);
			intent.putExtra(Constant.BundleKey.FLAG, info.getNewsType()==0 ? Constant.Flag.NORMAL_TYPE : Constant.Flag.NET_FRIEND_TYPE);
			intent.putExtra("from", "selfcollection");
			startActivity(intent);
		}
	}
	
}
