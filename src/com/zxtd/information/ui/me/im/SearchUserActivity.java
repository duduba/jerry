package com.zxtd.information.ui.me.im;

import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zxtd.information.adapter.SearchUserAdapter;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.manager.SearchUserManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.DampListView;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.ui.me.LoginActivity;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.util.Constant;

public class SearchUserActivity extends BaseActivity 
	implements OnClickListener,ListView.OnItemClickListener{

	DampListView listView;
	int pageIndex=0;
	private TextView footerMore;
	LinearLayout footerLoad;
	boolean isFooterLoad=false;
	TextView footerMorisFooterLoade;
	String queryParam="";
	EditText editParam;
	LoadingDialog dialog;
	SearchUserAdapter adapter;
	LinearLayout footer;
	private static final int LOGIN_REQUEST_CODE=87;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hiddleTitleBar();
		setContentView(R.layout.mine_search_user);
		initView();
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText("搜索用户");
		editParam=(EditText) findViewById(R.id.mine_search_focusnickname);
		findViewById(R.id.img_query).setOnClickListener(this);
		
		listView=(DampListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		
		footer=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.listview_footer_load, null);
		footer.setBackgroundResource(R.drawable.personal_list_item_bg_normal);
		footerMore=(TextView) footer.findViewById(R.id.list_footer_more);
		footerLoad=(LinearLayout) footer.findViewById(R.id.list_footer_loading);
		footerMore.setOnClickListener(this);
		footer.setBackgroundResource(R.drawable.list_selector);
		listView.addFooterView(footer);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.img_query:{
				doSearch();
			}break;
		}
	}

	
	private void doSearch(){
		if(null!=adapter){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		pageIndex=0;
		isFooterLoad=false;
		queryParam=editParam.getText().toString().trim();
		if(TextUtils.isEmpty(queryParam)){
			Toast("请输入用户昵称查询");
			if(adapter!=null){
				footer.setVisibility(View.GONE);
			}
		}else{
			queryUser();
		}
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2<adapter.getCount()){
			FansFocusBean bean=(FansFocusBean) adapter.getItem(arg2);
			Intent intent=new Intent(SearchUserActivity.this,OtherMainActivity.class);
			Bundle bundle=new Bundle();
			bundle.putParcelable("bean", bean);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
	
	private void queryUser(){
		if(!isFooterLoad){
			dialog=new LoadingDialog(this, R.style.loaddialog);
			dialog.show();
		}
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				List<FansFocusBean> list=SearchUserManager.getInstance().searchUsers(queryParam, pageIndex);
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
	
	
	 Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					SearchUserActivity.this.checkNerWork();
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<FansFocusBean> list=((List<FansFocusBean>) msg.obj);
					if(!list.isEmpty()){
						footer.setVisibility(View.VISIBLE);
					}else{
						footer.setVisibility(View.GONE);
						Toast("没有检索到用户");
					}
					pageIndex++;
					if(null==adapter){
						adapter=new SearchUserAdapter(SearchUserActivity.this,list,true);
						listView.setAdapter(adapter);
					}else{
						adapter.addData(list);
						adapter.notifyDataSetChanged();
						
						footerMore.setVisibility(View.VISIBLE);
						footerLoad.setVisibility(View.GONE);
						isFooterLoad=false;
					}
					int size=list.size();
					if(size<Constant.PAGESIZE){
						footerMore.setText("已显示全部");
						footerMore.setEnabled(false);
					}else{
						footerMore.setText("查看更多");
						footerMore.setEnabled(true);
					}
				}break;
			}
			if(null!=dialog && dialog.isShowing()){
				dialog.dismiss();
			}
		}
	};
	
	
	public void gotoLogin(){
		Toast.makeText(this, "您还未登陆,请先登录..", Toast.LENGTH_LONG).show();
		Intent intent=new Intent(this,LoginActivity.class);
		intent.putExtra("from", "search");
		startActivityForResult(intent, LOGIN_REQUEST_CODE);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==LOGIN_REQUEST_CODE){
				doSearch();
			}
		}
	}

}
