package com.zxtd.information.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zxtd.information.manager.CommentManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;

public class MyCommentActivity extends BaseActivity 
	implements OnClickListener,ListView.OnItemClickListener{

	private ListView listview;
	private LoadingDialog dialog;
	private int pageIndex=0;
	private TextView footerMore;
	private LinearLayout footerLoad;
	private boolean isFooterLoad=false;
	private int userId=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_mycollection_layout);
		userId=getIntent().getIntExtra("userId", -1);
		initView();
		loadData();
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText(getIntent().getStringExtra("userName")+"评论");
		listview=(ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		initFooter();
	}

	
	private void initFooter(){
		LinearLayout footer=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.listview_footer_load, null);
		footer.setBackgroundResource(R.drawable.personal_list_item_bg_normal);
		footerMore=(TextView) footer.findViewById(R.id.list_footer_more);
		footerLoad=(LinearLayout) footer.findViewById(R.id.list_footer_loading);
		footerMore.setOnClickListener(this);
		listview.addFooterView(footer);
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
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
				//CommentManager.getInstance().getComments(userId, pageIndex);
				/*Message msg=handler.obtainMessage();
				if(null==list){
					msg.what=-1;
				}else{
					msg.what=1;
					msg.obj=list;
				}
				msg.sendToTarget();
				*/
			}
			
		}.start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	
}
