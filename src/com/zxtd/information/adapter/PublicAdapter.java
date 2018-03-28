package com.zxtd.information.adapter;

import java.util.List;
import com.zxtd.information.bean.PublicBean;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PublicAdapter extends BaseAdapter {

	private Context context;
	private List<PublicBean> dataList;
	public List<PublicBean> getDataList() {
		return dataList;
	}

	private LayoutInflater inflater;
	private AsyncImageLoader asyncImageLoader;
	private ProgressDialog proDialog;
	
	private int currentPosition = -1;
	
	public PublicAdapter(Context con,List<PublicBean> list){
		this.context=con;
		this.dataList=list;
		inflater=LayoutInflater.from(this.context);
		asyncImageLoader = new AsyncImageLoader(); 
	}
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
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
		final PublicBean bean=dataList.get(arg0);
		ViewHolder holder=null;
		if(null==view){
			holder=new ViewHolder();
			view=inflater.inflate(R.layout.mine_public_list_layout, null);
			//holder.imgHeader=(ImageView) view.findViewById(R.id.mine_public_list_header);
			holder.txtTitle=(TextView) view.findViewById(R.id.mine_public_list_title);
			holder.txtContent=(TextView) view.findViewById(R.id.mine_public_list_content);
			holder.txtChannel=(TextView) view.findViewById(R.id.mine_public_list_channel);
			holder.txtTime=(TextView) view.findViewById(R.id.mine_public_list_time);
			holder.txtReplayCount=(TextView) view.findViewById(R.id.mine_public_list_replayCount);
			holder.imgNewIcon=(ImageView) view.findViewById(R.id.mine_public_list_replayicon);
			holder.imgRedPoint=(ImageView) view.findViewById(R.id.public_list_newreply);
			holder.optLayout=(LinearLayout) view.findViewById(R.id.layout_other);
			holder.viewLayout= (LinearLayout) view.findViewById(R.id.item_view);
			holder.editLayout= (LinearLayout) view.findViewById(R.id.item_edit);
			holder.deleteLayout = (LinearLayout) view.findViewById(R.id.item_delete);
			//holder.imgUpDown=(ImageView) view.findViewById(R.id.item_updown);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		holder.txtTitle.setText(bean.getNewsTitle());
		holder.txtContent.setText(bean.getContent());
		holder.txtChannel.setText(bean.getChannel());
		holder.txtTime.setText(TimeUtils.formatDate(bean.getPublicTime()));
		holder.txtReplayCount.setText(String.valueOf(bean.getReplayCount()));
		holder.imgRedPoint.setVisibility(bean.isHasNewReplay()? View.VISIBLE : View.GONE); 
		/*if(!TextUtils.isEmpty(bean.getUserImg())){
			setImg(holder,bean.getUserImg());
		}else{
			holder.imgHeader.setImageResource(R.drawable.session_default);
		}*/
		view.setBackgroundResource(R.drawable.list_selector);
		/*
		if(arg0==currentPosition){
			holder.optLayout.setVisibility(View.VISIBLE);
			holder.imgUpDown.setImageResource(R.drawable.newprivatedetails_arrow_up);
			holder.viewLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(context,NewInfoActivity.class);
					NewBean newBean = new NewBean();
					newBean.newId =String.valueOf(bean.getNewsId());
					newBean.postCount = String.valueOf(bean.getReplayCount());
					newBean.infoUrl =bean.getNewsUrl();
					CurrentManager.newInstance().setCurrentBean(newBean);
					Utils.netFriendClickedItem.add(newBean.newId);
					intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.NET_FRIEND_TYPE);
					context.startActivity(intent);
					closeMenu();
				}
			});
			
			holder.editLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
				}
			});
			
			holder.deleteLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					deleteWeibo(bean);
				}
			});
			
		}else{
			holder.imgUpDown.setImageResource(R.drawable.newprivatedetails_arrow_down);
			holder.optLayout.setVisibility(View.GONE);
		}*/
		return view;
	}
	
	public void addData(List<PublicBean> extr){
		this.dataList.addAll(extr);
	}

	
	private final static class ViewHolder{
		private TextView txtTitle;
		private TextView txtContent;
		private ImageView imgHeader;
		private TextView txtChannel;
		private TextView txtTime;
		private TextView txtReplayCount;
		private ImageView imgNewIcon;
		private ImageView imgRedPoint;
		
		private ImageView imgUpDown;
		private LinearLayout optLayout;
		private LinearLayout viewLayout;
		private LinearLayout editLayout;
		private LinearLayout deleteLayout;
	}
	
	

	
	/**
	 * 
	 * @param holder
	 * @param imgUrl
	 */
	private void setImg(final ViewHolder holder,String imgUrl){	
		Drawable drawable=asyncImageLoader.loadDrawable(imgUrl,false,new ImageCallback(){
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				if(null!=imageDrawable){
					holder.imgHeader.setImageDrawable(imageDrawable);  
				}else{
					holder.imgHeader.setImageResource(R.drawable.mine_user_logo);  
				}
			}
        });
		if(null!=drawable){
			holder.imgHeader.setImageDrawable(drawable); 
		}	
	}
	
	
	/**
	 * 删除
	 * @param bean
	 */
	private void deleteWeibo(final PublicBean bean){
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle("确定删除吗?");
	
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
						int resultCode=PublicManager.getInstance().doDelete(bean.getNewsId());
						Message msg=handler.obtainMessage();
						msg.what=resultCode;
						msg.obj=bean;
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
	
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					boolean isConn=Utils.isNetworkConn();
					if(!isConn){
						Toast("系统接口异常");
					}
				}break;
				case 2:
				case 0:{
					Toast("删除失败");
				}break;
				case 1:{
					Toast("删除成功");
					PublicBean bean=(PublicBean) msg.obj; 
					dataList.remove(bean);
					notifyDataSetChanged();
					boolean isSuccess=PublicManager.getInstance().deleteLocal(bean);
					if(isSuccess){
						Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
						intent.putExtra("type", 0);
						intent.putExtra("data", -1);
						context.sendBroadcast(intent);
					}
				}break;
			}
			if(null!=proDialog){
				proDialog.dismiss();
			}
		}
	};
	
	
	private void Toast(String str){
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
	
	private void closeMenu(){
		currentPosition=-1;
		notifyDataSetChanged();
	}
	
	public void clear(){
		dataList.clear();
	}
	
}
