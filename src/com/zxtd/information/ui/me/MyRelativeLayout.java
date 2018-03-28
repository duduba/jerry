package com.zxtd.information.ui.me;

import java.util.Iterator;
import java.util.List;

import com.zxtd.information.adapter.FansFocusAdapter;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("HandlerLeak")
public class MyRelativeLayout extends RelativeLayout 
	implements View.OnClickListener,ListView.OnItemClickListener{

	public interface CallBack{
		List<FansFocusBean> loadData(int pageIndex);
	};
	
	private CallBack callBack;
	
	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	private TextView footerMore;
	
	private LinearLayout footerLoad;
	private boolean isFooterLoad=false;
	private int pageIndex=0;
	private Context context;
	private LoadingDialog dialog;
	private ListView listView;
	private FansFocusAdapter adapter;
	private int pageCount=0;
	private int from;
	private boolean isSelf=false;
	
	private FansAndFocusActivity activity;
	
	
	public void setFrom(int from) {
		this.from = from;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}



	public MyRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		LayoutParams relativeParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		View view=LayoutInflater.from(context).inflate(R.layout.mine_common_listview, null);
		
		listView=(ListView) view.findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		initFooter();
		this.addView(view,relativeParams);
		
		
		/*注册广播*/
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		context.registerReceiver(receiver, filter);
		
		activity=(FansAndFocusActivity) context;
	}
	
	
	public void destoryReceiver(){
		context.unregisterReceiver(receiver);
	}

	private void initFooter(){
		LinearLayout footer=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_footer_load, null);
		footer.setBackgroundResource(R.drawable.personal_list_item_bg_normal);
		footerMore=(TextView) footer.findViewById(R.id.list_footer_more);
		footerLoad=(LinearLayout) footer.findViewById(R.id.list_footer_loading);
		footerMore.setOnClickListener(this);
		footer.setBackgroundResource(R.drawable.list_selector);
		listView.addFooterView(footer);
	}
	
	public void initData(){
		if(!isFooterLoad){
			dialog=new LoadingDialog(context, R.style.loaddialog);
			dialog.show();
		}
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				List<FansFocusBean> list=callBack.loadData(pageIndex);
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
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					boolean isConn=Utils.isNetworkConn();
					if(isConn){
						Toast.makeText(context, "程序接口 错误", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(context, "网络不稳定或已断开", Toast.LENGTH_LONG).show();
					}
				}break;
				case 1:{
					@SuppressWarnings("unchecked")
					List<FansFocusBean> list=((List<FansFocusBean>) msg.obj);
					pageIndex++;
					if(null==adapter){
						setCount();
						adapter=new FansFocusAdapter(context,list,from,isSelf);
						listView.setAdapter(adapter);
					}else{
						if(pageIndex==1){
							setCount();
						}
						adapter.addData(list);
						adapter.notifyDataSetChanged();
						
						footerMore.setVisibility(View.VISIBLE);
						footerLoad.setVisibility(View.GONE);
						isFooterLoad=false;
					}
					int size=list.size();
					if(size<Constant.PAGESIZE || pageIndex>=pageCount){
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


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.list_footer_more:{
				footerMore.setVisibility(View.GONE);
				footerLoad.setVisibility(View.VISIBLE);
				isFooterLoad=true;
				initData();
			}break;
		}
	}
	
	
	private void setCount(){
		int count=0;
		switch(from){
			case 0:{
				count=FansFocusBean.fansCount;
				activity.setFansCount(count);
			}break;
			case 1:{
				count=FansFocusBean.focusCount;
				activity.setFocusCount(count);
			}break;
		}
		pageCount=(count+Constant.PAGESIZE-1)/Constant.PAGESIZE;
	}
	
	

	
	
	private BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED.equals(action)){
				int f=intent.getIntExtra("from", 0);
				int t=intent.getIntExtra("type", 0);
				int userId=intent.getIntExtra("userId", 0);
				Log.e(Constant.TAG, "f: "+f+"   from:   "+from+ "   type:"+t+"    userId:"+userId);
				
				//if(f!=from){
					adapter.clear();
					adapter.notifyDataSetChanged();
					pageIndex=0;
					initData();
				//}
			}
		}
	};
	
	
	private FansFocusBean getBean(int userId){
		if(adapter!=null){
			Iterator<FansFocusBean> it=adapter.getDataList().iterator();
			while(it.hasNext()){
				FansFocusBean bean=it.next();
				if(bean.getUserId()==userId){
					return bean;
				}
			}
		}
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg2<adapter.getCount()){
			FansFocusBean bean=(FansFocusBean) adapter.getItem(arg2);
			Intent intent=new Intent(context,OtherMainActivity.class);
			Bundle bundle=new Bundle();
			bundle.putParcelable("bean", bean);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}
	
	
}

	
