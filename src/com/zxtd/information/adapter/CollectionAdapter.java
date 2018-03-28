package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.NewsInfo;
import com.zxtd.information.manager.MyCollectionManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.MyCollectionActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CollectionAdapter extends BaseAdapter {

	private List<NewsInfo> dataList;
	private Context context;
	private LayoutInflater inflater;
	private float x,ux;
	private int screenWidth;
	private ProgressDialog proDialog;
	private MyCollectionActivity activity;
	private View deleteView;
	
	public List<NewsInfo> getDataList() {
		return dataList;
	}

	public CollectionAdapter(Context context,List<NewsInfo> dataList){
		this.context=context;
		this.dataList=dataList;
		inflater=LayoutInflater.from(this.context);
		screenWidth=context.getResources().getDisplayMetrics().widthPixels;
		activity=(MyCollectionActivity) context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		final NewsInfo bean=dataList.get(arg0);
		final View tempView=view;
		if(view==null){
			view=inflater.inflate(R.layout.mine_collection_list_layout, null);
			holder=new ViewHolder();
			holder.title=(TextView) view.findViewById(R.id.coll_title);
			holder.remark=(TextView) view.findViewById(R.id.coll_remark);
			holder.replay=(TextView) view.findViewById(R.id.coll_replay);
			holder.btnDel=(Button) view.findViewById(R.id.del);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}

		holder.title.setText(bean.getNewsTitle());
		holder.remark.setText(bean.getNewSummary());
		holder.replay.setText(String.valueOf(bean.getNewReplyCount()));
		if(bean.isSelected()){
			if(holder.btnDel.getVisibility()==View.GONE){
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.in);
				holder.btnDel.startAnimation(animation);
				holder.btnDel.setVisibility(View.VISIBLE);
			}
			final Button btnDel=holder.btnDel;
			holder.btnDel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					btnDel.setVisibility(View.GONE);
					deleteView=tempView;
					proDialog=new ProgressDialog(context);
					proDialog.setCancelable(true);
					proDialog.setIndeterminate(false);
					proDialog.setMessage("正在删除..");
					proDialog.show();
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							int resultCode=MyCollectionManager.getInstance().deleteColl(XmppUtils.getUserId(), bean.getNewsId(),bean.getNewsType());
							Message msg=handler.obtainMessage();
							if(resultCode==-1){
								msg.what=-1;
							}else{
								msg.what=2;
								msg.obj=bean;
							}
							msg.sendToTarget();
						}
					}.start();
					
					/*
					AlertDialog.Builder builder=new AlertDialog.Builder(context);
					builder.setTitle("删除收藏");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
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
					*/
				}
			});
		}else{
			if(holder.btnDel.getVisibility()==View.VISIBLE){
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.out);
				holder.btnDel.startAnimation(animation);
				holder.btnDel.setVisibility(View.GONE);
			}
			
		}
		view.setBackgroundResource(R.drawable.list_selector);
		return view;
	}

	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					if(!Utils.isNetworkConn()){
						Toast.makeText(context, "网络不给力", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
					}
				}break;
				case 2:{
					final NewsInfo info=(NewsInfo) msg.obj;
					boolean isFlag=MyCollectionManager.getInstance().deleteLocalCollection(info.getNewsId(),info.getNewsType());
					if(isFlag){
						activity.setDeleteIndex(-1);
						Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
						intent.putExtra("type", 1);
						intent.putExtra("data", -1);
						context.sendBroadcast(intent);
						Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
					}
					
					Animation animation=AnimationUtils.loadAnimation(context, R.anim.left_to_out);
	    			animation.setAnimationListener(new AnimationListener() {
	    				public void onAnimationStart(Animation animation) {
	    				}
	    				public void onAnimationRepeat(Animation animation) {				
	    				}
	    				public void onAnimationEnd(Animation animation) {
	    					dataList.remove(info);
	    					notifyDataSetChanged();
	    				}
	    			});
	    			deleteView.startAnimation(animation);
	    			animation.reset();
				}break;
			}
			if(null!=proDialog){
				proDialog.dismiss();
			}
		}
	};
	
	
	
	private final static class ViewHolder{
		private TextView title;
		private TextView remark;
		private TextView replay;
		private Button btnDel;
	}
	
	

	
	
	public void addData(List<NewsInfo> list){
		this.dataList.addAll(list);
	}
	
	public void remove(int index){
		this.dataList.remove(index);
	}
	
}
