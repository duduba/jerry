package com.zxtd.information.ui.me;

import java.util.List;

import com.zxtd.information.adapter.CityAdapter;
import com.zxtd.information.bean.City;
import com.zxtd.information.manager.RegistManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HotCityLayout extends RelativeLayout implements ListView.OnItemClickListener{

	private Context context;
	private LoadingDialog dialog;
	private RegistManager manager;
	
	/*
	private int type;
	public void setType(int type) {
		this.type = type;
	}
	*/

	private ListView listView;
	
	public HotCityLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		LayoutParams relativeParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		View view=LayoutInflater.from(context).inflate(R.layout.minea_city_layout, null);
		
		listView=(ListView) view.findViewById(R.id.city);
		listView.setOnItemClickListener(this);
		this.addView(view,relativeParams);
		
		manager=RegistManager.newInstance();
	}
	
	
	public void loadData(){
		dialog=new LoadingDialog(context, R.style.loaddialog);
		dialog.show();
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				List<City> list=manager.getHotCity();
				//if(type==1){
				//	list=manager.getHotCity();
				//}else if(type==2){
				//	list=manager.getProvince();
				//}
				Message msg=handler.obtainMessage();
				if(null==list || list.size()==0){
					msg.what=-1;
				}//else{
				//	msg.what=type;
				//}
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
					Toast.makeText(context, "获取数据失败", Toast.LENGTH_LONG).show();
				}break;
				case 1:{
					List<City> list=(List<City>) msg.obj;
					CityAdapter adapter=new CityAdapter(context, list);
					listView.setAdapter(adapter);
				}break;
			}
			dialog.dismiss();
		}
		
	};
	
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		CityActivity activity=(CityActivity) context;
		City city=(City) arg0.getItemAtPosition(arg2);
		activity.doClose(city.getCityName());
	}
	
	
}
