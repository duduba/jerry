package com.zxtd.information.ui.me;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zxtd.information.adapter.DistrictAdapter;
import com.zxtd.information.bean.District;
import com.zxtd.information.bean.Province;
import com.zxtd.information.manager.RegistManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;

public class DistrictActivity extends BaseActivity implements 
	OnClickListener,ListView.OnItemClickListener{

	private Province prov=null;
	private ListView listView;
	private LoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_regist_district);
		initView();
	}

	void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		
		TextView title=(TextView) findViewById(R.id.title);
		Bundle bundle=this.getIntent().getExtras();
		prov=bundle.getParcelable("prov");
		title.setText(prov.getProvName());
		
		listView=(ListView) findViewById(R.id.city);
		listView.setOnItemClickListener(this);
		
		loadData();
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
		dialog=new LoadingDialog(this, R.style.loaddialog);
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				List<District> list=RegistManager.newInstance().getDistricts(prov.getPrivId());
				Message msg=handler.obtainMessage();
				if(null==list || list.size()==0){
					msg.what=-1;
				}else{
					msg.what=1;
					msg.obj=list;
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
					Toast("获取数据失败");
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<District> list=(List<District>) msg.obj;
					DistrictAdapter adapter=new DistrictAdapter(DistrictActivity.this, list);
					listView.setAdapter(adapter);
				}break;
			}
			dialog.dismiss();
		}
	
	};
	
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		District district=(District) arg0.getItemAtPosition(arg2);
		Intent intent=new Intent(this,CityActivity.class);
		if(arg0.getCount()==1){
			intent.putExtra("cityName", district.getDistrictName());
		}else{
			intent.putExtra("cityName", prov.getProvName()+" "+district.getDistrictName());
		}
		setResult(RESULT_OK,intent);
		finish();
	}
	
}
