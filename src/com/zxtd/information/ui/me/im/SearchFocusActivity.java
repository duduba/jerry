package com.zxtd.information.ui.me.im;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.zxtd.information.adapter.LocalFocusAdapter;
import com.zxtd.information.adapter.SearchUserAdapter;
import com.zxtd.information.manager.FansFocusManager;
import com.zxtd.information.manager.SessionManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.IMMessageBean;
import com.zxtd.information.bean.Session;
import com.zxtd.information.ui.custview.DampListView;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.custview.MyLetterListView;
import com.zxtd.information.ui.custview.MyLetterListView.OnTouchingLetterChangedListener;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.TimeUtils;

public class SearchFocusActivity extends BaseActivity 
	implements OnClickListener,ListView.OnItemClickListener{

	private DampListView listView;
	private MyLetterListView letterListView;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private TextView overlay;
    private LocalFocusAdapter adapter;
    private SearchUserAdapter searchAdapater;
    private EditText editQuery;
    private LoadingDialog dialog;
    private ImageView imgClearInput;
    private boolean isFirstLoad=true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_search_focus);
		initView();
		syncDataFromServer();
	}
	
	
	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.search_all).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText("私信通讯录");
		editQuery=(EditText) findViewById(R.id.mine_search_focusnickname);
		editQuery.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				String input = s.toString(); 
				if(!TextUtils.isEmpty(input)){
					overlay.setVisibility(View.GONE);
					letterListView.setVisibility(View.GONE);
					queryDataFromLocal(input);
				}else{
					if(null!=adapter){
						letterListView.setVisibility(View.VISIBLE);
						listView.setAdapter(adapter);
		                sections=adapter.getSections();
		            	alphaIndexer=adapter.getAlphaIndexer();
					}
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});
		
		imgClearInput=(ImageView) findViewById(R.id.img_clear_input);
		imgClearInput.setOnClickListener(this);
		
		listView=(DampListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		
		letterListView = (MyLetterListView)findViewById(R.id.MyLetterListView01);
	    letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
	    initOverlay();
	    listView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
						overlay.setVisibility(View.GONE);
					}
				}
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
					if(adapter!=null && adapter.getCount()>0){
						if(!isFirstLoad){
							overlay.setText(getAlpha(((FansFocusBean)adapter.getItem(firstVisibleItem)).getSortKey()));
							overlay.setVisibility(View.VISIBLE);
						}
						isFirstLoad=false;
					}
				}
			});
	    
	}
	
	
	private void queryDataFromLocal(final String nickName){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				FansFocusManager manager=new FansFocusManager();
				List<FansFocusBean> list=manager.searchFocus(nickName, SearchFocusActivity.this.getUserId());
				Message msg=handler.obtainMessage();
				msg.what=2;
				msg.obj=list;
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
					Toast("获取好友信息失败..");
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<FansFocusBean> list=(List<FansFocusBean>) msg.obj;
					if(list.size()==0){
						Toast("你暂时没有好友");
					}else{
						adapter=new LocalFocusAdapter(list, SearchFocusActivity.this);
						listView.setAdapter(adapter);
		                sections=adapter.getSections();
		            	alphaIndexer=adapter.getAlphaIndexer();
		            	//进入时，从服务端获取好友信息，并插入到本地
		            	saveDataToLocal(list);
					}
				}break;
				case 2:{
					@SuppressWarnings("unchecked")
					List<FansFocusBean> list=(List<FansFocusBean>) msg.obj;
					//if(list.size()==0){
					//	Toast("无结果");
					//}
					if(null==searchAdapater){
						searchAdapater=new SearchUserAdapter(SearchFocusActivity.this,list,false);
						listView.setAdapter(searchAdapater);
					}else{
						searchAdapater.setDataList(list);
						searchAdapater.notifyDataSetChanged();
						listView.setAdapter(searchAdapater);
					}
					overlay.setVisibility(View.GONE);
				}
			}
			if(null!=dialog && dialog.isShowing()){
				dialog.dismiss();
			}
		}
	};
	
	
	
	private void saveDataToLocal(final List<FansFocusBean> list){
		new Thread(){
			@Override
			public void run() {
				FansFocusManager manager=new FansFocusManager();
				manager.syncLocal(list);
			}
		}.start();
	}
	
	
	
	
	
	/**
	 * 获取关注好友信息
	 */
	private void syncDataFromServer(){
		dialog=new LoadingDialog(this, R.style.loaddialog);
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				FansFocusManager manager=new FansFocusManager();
				List<FansFocusBean> list=manager.getAllFocus();
				Message msg=handler.obtainMessage();
				if(null==list){
					msg.what=-1;
				}else{
					msg.what=1;
					msg.obj=list;
				}
				msg.sendToTarget();
			}
		}.start();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.img_clear_input:{
				editQuery.setText("");
			}break;
			case R.id.search_all:{
				Intent intent = new Intent(this,
						SearchUserActivity.class);
				startActivity(intent);
			}break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		FansFocusBean bean=(FansFocusBean) arg0.getAdapter().getItem(arg2);
		if(SessionManager.getInstance().checkSessionExtis(String.valueOf(bean.getUserId()), getUserId())){
			Session session=null;
			if(Constant.CACHESESSION.containsKey(String.valueOf(bean.getUserId()))){
				session=Constant.CACHESESSION.get(String.valueOf(bean.getUserId()));
			}else{
				session=SessionManager.getInstance().getSessionById(bean.getUserId());
			}
			forwardChatActivity(session);
		}else{
			int _id=SessionManager.getInstance().addSession(String.valueOf(bean.getUserId()), 
					bean.getNickName(), bean.getImg(), bean.getSign(), "", TimeUtils.getNow(), 0, getUserId());
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
		
	}
	
	private void forwardChatActivity(Session session){
		Intent newIntent=new Intent(SearchFocusActivity.this,ChatActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("session", session);
		IMMessageBean bean=getIntent().getParcelableExtra("msgBean");
		if(null!=bean){
			bundle.putParcelable("msgBean", bean);
		}
		newIntent.putExtras(bundle);
		startActivity(newIntent);
		finish();
	}
	
	
	
    private class LetterListViewListener implements OnTouchingLetterChangedListener{
		@Override
		public void onTouchingLetterChanged(final String s,float y,float x) {
			if(alphaIndexer!=null && alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				listView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
			} 
		}

		@Override
		public void onTouchingLetterEnd() {
			overlay.setVisibility(View.GONE);
		}	
    }
    
    
    private String getAlpha(String str){  
    	if(TextUtils.isEmpty(str))
    		 return "#"; 
        char c = str.trim().substring(0, 1).charAt(0);  
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");  
        if (pattern.matcher(c + "").matches()) {  
            return (c + "").toUpperCase();  
        } else {  
            return "#";  
        }  
    }
    
    private void initOverlay() {
    	WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    	LayoutInflater inflater = LayoutInflater.from(this);
    	overlay = (TextView) inflater.inflate(R.layout.overlay, null);
    	overlay.setVisibility(View.GONE); //165,-380
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
			200, 
			200,
			0,0,
			WindowManager.LayoutParams.TYPE_APPLICATION,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
			PixelFormat.TRANSLUCENT);
		windowManager.addView(overlay, lp);
		//addView(overlay, lp);
    }
    
    
}
