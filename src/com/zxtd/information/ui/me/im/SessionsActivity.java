package com.zxtd.information.ui.me.im;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zxtd.information.adapter.SessionAdapter;
import com.zxtd.information.bean.Session;
import com.zxtd.information.bean.SessionComparator;
import com.zxtd.information.manager.SessionManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.util.Constant;

public class SessionsActivity extends BaseActivity implements OnClickListener
	,ListView.OnItemClickListener,ListView.OnItemLongClickListener{

	private ListView listView;
	private LoadingDialog dialog;
	public SessionAdapter adapter=null;
	private LinearLayout deleteLayout=null;
	private Button btnSure=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_im_sessions);
		initView();
		loadData();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constant.REFRESH_SESSION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver, filter);
	}

	
	private void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		Button btnWrite=(Button) findViewById(R.id.write);
		btnWrite.setOnClickListener(this);
		listView=(ListView) findViewById(R.id.sessions);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		
		deleteLayout=(LinearLayout) findViewById(R.id.mine_session_delete_layout);
		btnSure=(Button)findViewById(R.id.btnsure);
		btnSure.setOnClickListener(this);
		btnSure.setClickable(false);
		findViewById(R.id.btncancel).setOnClickListener(this);
	}


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.write:{
				Intent intent=new Intent(SessionsActivity.this,SearchFocusActivity.class);
				startActivity(intent);
			}break;
			case R.id.btnsure:{
				dialog=new LoadingDialog(this, R.style.loaddialog);
				dialog.show();
				new Thread(){
					@Override
					public void run() {
						super.run();
						List<Session> removeList=new ArrayList<Session>();
						Iterator<Session> it=adapter.getDataList().iterator();
						while(it.hasNext()){
							Session session=it.next();
							if(session.isChecked()){
								removeList.add(session);
							}
						}
						int  noReadCount=SessionManager.getInstance().deleteSession(removeList);
						int what=noReadCount>0 ? 2 : -1;
						handler.sendEmptyMessage(what);
					}
				}.start();
			}break;
			case R.id.btncancel:{
				cancelDelete();
			}break;
		}
	}

	
	private void cancelDelete(){
		if(null!=adapter){
			Iterator<Session> it=adapter.getDataList().iterator();
			while(it.hasNext()){
				Session session=it.next();
				if(session.isChecked()){
					session.setChecked(false);
				}
			}
			adapter.setShowCheckBox(false);
			adapter.notifyDataSetChanged();
		}
		deleteLayout.setVisibility(View.GONE);
	}
	

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Session session=(Session) arg0.getItemAtPosition(arg2);
		if(session.getNoReadMsgCount()>0){
			boolean isSuccess=SessionManager.getInstance().clearNoRead(session.getSessionId());
			if(isSuccess){
				session.setNoReadMsgCount(0);
				adapter.notifyDataSetChanged();
				
				Intent intent=new Intent(Constant.REFRESH_SESSION);
				sendBroadcast(intent);
			}
		}
		Intent intent=new Intent(SessionsActivity.this,ChatActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("session", session);
		intent.putExtras(bundle);
		startActivity(intent);
	}


	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		final Session session=(Session) adapter.getItem(arg2);
		builder.setTitle(session.getNickName());
		String[] items=null;
		if(arg2>1){
			items=new String[]{"删除会话","会话置顶"};
		}else{
			items=new String[]{"删除会话"};
		}
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				boolean flag=false;
				if(arg1==0){
					 List<Session> list=new ArrayList<Session>();
					 list.add(session);
					 int noReadCount=SessionManager.getInstance().deleteSession(list);
					 if(noReadCount!=-1){
						 //发广播通知
						 flag=true;
						 Intent intent=new Intent(Constant.REFRESH_SESSION);
						 sendBroadcast(intent);
					 }
				}else{
					 flag=SessionManager.getInstance().updateSort(session.getSessionId(), SessionsActivity.this.getUserId());
				}
				if(flag){
					loadData();
				}
			}
		});
		builder.create().show();
		return false;
	}
	
	
	/*
	 *获取数据 
	 */
	private void loadData(){
		dialog=new LoadingDialog(this, R.style.loaddialog);
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				List<Session> list=SessionManager.getInstance().getSessions(SessionsActivity.this.getUserId());
				Message msg=handler.obtainMessage();
				msg.what=1;
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
					Toast("删除失败");
					cancelDelete();
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<Session> list=(List<Session>) msg.obj;
					if(list.isEmpty()){
						Toast("点击通讯录，快给你的好友发私信吧！");
					}
					if(null==adapter){
						adapter=new SessionAdapter(SessionsActivity.this, list);
						listView.setAdapter(adapter);
					}else{
						adapter.setDataList(list);
						adapter.notifyDataSetChanged();
					}
				}break;
				case 2:{
					Iterator<Session> it=adapter.getDataList().iterator();
					while(it.hasNext()){
						Session session=it.next();
						if(session.isChecked()){
							it.remove();
						}
					}
					adapter.setShowCheckBox(false);
					adapter.notifyDataSetChanged();
					deleteLayout.setVisibility(View.GONE);
				}break;
				
			}
			dialog.dismiss();
		}
		
	};
	
	
	BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(Constant.REFRESH_SESSION.equals(intent.getAction())){
				String type=intent.getStringExtra("type");
				Session session=intent.getParcelableExtra("session");
				if(null!=adapter){
					if("add".equals(type)){
						adapter.getDataList().add(0, session);
						//adapter.notifyDataSetChanged();
					}else if("clear".equals(type)){
						Iterator<Session> it=adapter.getDataList().iterator();
						while(it.hasNext()){
							Session s=it.next();
							if(s.getSessionName().equals(session.getSessionName())){
								s.setNoReadMsgCount(0);
								break;
							}
						}
						//adapter.notifyDataSetChanged();
					}else if("update".equals(type)){
						Iterator<Session> it=adapter.getDataList().iterator();
						while(it.hasNext()){
							Session s=it.next();
							if(s.getSessionName().equals(session.getSessionName())){
								s.setNoReadMsgCount(session.getNoReadMsgCount());
								s.setLastMsg(session.getLastMsg());
								s.setLastSendTime(session.getLastSendTime());
								//SessionManager.getInstance().updateLastTime(session.getSessionId(), session.getLastSendTime());
								break;
							}
						}
						//adapter.notifyDataSetChanged();
					}
					//排序
					java.util.Collections.sort(adapter.getDataList(), new SessionComparator());
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}


	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if(null!=adapter && !adapter.getDataList().isEmpty()){
			SessionManager.getInstance().setNoReadCount(adapter.getDataList());
		}
		super.finish();
	}
	
	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
			menu.add(0, 1, 0,"删除").setIcon(R.drawable.lib_upgrade_stop_bg);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			if(null!=adapter){
				adapter.setShowCheckBox(true);
				adapter.notifyDataSetChanged();
				deleteLayout.setVisibility(View.VISIBLE);
			}
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	
	public void setButtonText(int checkCount){
		if(checkCount>0 && !btnSure.isClickable()){
			btnSure.setClickable(true);
		}else if(checkCount==0 && btnSure.isClickable()){
			btnSure.setClickable(false);
		}
		btnSure.setText("确定("+checkCount+")");
	}
	
}
