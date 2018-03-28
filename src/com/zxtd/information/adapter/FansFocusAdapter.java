package com.zxtd.information.adapter;

import java.util.List;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.manager.FansFocusManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.FansAndFocusActivity;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

public class FansFocusAdapter extends BaseAdapter {

	private Context context;
	private List<FansFocusBean> dataList;
	private LayoutInflater inflater;
	private AsyncImageLoader asyncImageLoader;
	private int from;
	private ProgressDialog dialog;
	private FansAndFocusActivity activity;
	private boolean isSelf;
	/*
	public void setFrom(int from) {
		this.from = from;
	}*/
	
	public FansFocusAdapter(Context con,List<FansFocusBean> list,int from,boolean isSelf){
		this.context=con;
		this.dataList=list;
		this.from=from;
		this.isSelf=isSelf;
		inflater=LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader(); 
		activity=(FansAndFocusActivity) context;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		final FansFocusBean bean=dataList.get(arg0);
		if(null==view){
			view=inflater.inflate(R.layout.mine_common_list_layout, null);
			holder=new ViewHolder();
			holder.icon=(RoundAngleImageView) view.findViewById(R.id.user_icon);
			holder.nickName=(TextView) view.findViewById(R.id.nick_name);
			holder.sign=(TextView) view.findViewById(R.id.sign);
			holder.imgfocus=(ImageView) view.findViewById(R.id.img_mutual_focus);
			holder.btnOpt=(LinearLayout) view.findViewById(R.id.btn_opt);
			
			holder.btnOptImg=(ImageView) view.findViewById(R.id.btn_opt_focus_img);
			holder.btnOptText=(TextView) view.findViewById(R.id.btn_opt_focus_text);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		if(!TextUtils.isEmpty(bean.getImg())){
			setImg(holder,bean.getImg());
		}else{
			holder.icon.setImageResource(R.drawable.mine_user_logo);
		}
		holder.nickName.setText(bean.getNickName());
		holder.sign.setText(bean.getSign());
		
		setButtonState(holder,bean);
		if(isSelf){
			holder.btnOpt.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View arg0) {
					if(bean.isAddFocus()){
						doAddAndCancelFocus(bean);
					}else{
						AlertDialog.Builder builder=new AlertDialog.Builder(context);
						builder.setTitle("确定要取消关注吗?");
						builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								doAddAndCancelFocus(bean);	
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				}
			});
		}else{
			holder.btnOpt.setVisibility(View.GONE);
		}
		view.setBackgroundResource(R.drawable.list_selector);
		return view;
	}

	public void addData(List<FansFocusBean> ext){
		this.dataList.addAll(ext);
	}
	
	
	
	private void doAddAndCancelFocus(final FansFocusBean bean){
		dialog=new ProgressDialog(context);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		String msg="";
		if(bean.isAddFocus()){
			msg="正在添加关注..";
		}else{
			msg="正在取消关注..";
		}
		dialog.setMessage(msg);
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				FansFocusManager manager=new FansFocusManager();
				int what=-1;
				if(bean.isAddFocus()){
					what=manager.addFocus(XmppUtils.getUserId(), bean.getUserId(), bean.getNickName());
				}else{
					what=manager.cancelFocus(XmppUtils.getUserId(), bean.getUserId(),from);
				}
				Message msg=handler.obtainMessage();
				msg.what=what;
				msg.obj=bean;
				msg.sendToTarget();
			}
		}.start();
	}
	
	
	private void setButtonState(ViewHolder holder,FansFocusBean bean){
		switch(from){
			case 0:{
				switch(bean.getAttentionState()){
					case 0:{
						//holder.btnOpt.setBackgroundResource(R.drawable.add_focus_button);
						setViewForAddFocus(holder);
						holder.imgfocus.setVisibility(View.GONE);
						bean.setAddFocus(true);
					}break;
					case 1:{
						//holder.btnOpt.setBackgroundResource(R.drawable.cancel_focus_button);
						holder.imgfocus.setVisibility(View.VISIBLE);
						setViewForCancelFocus(holder);
						bean.setAddFocus(false);
					}break;
				}
			}break;
			case 1:{
				bean.setAddFocus(false);
				//holder.btnOpt.setBackgroundResource(R.drawable.cancel_focus_button);
				setViewForCancelFocus(holder);
				switch(bean.getAttentionState()){
					case 0:{
						holder.imgfocus.setVisibility(View.GONE);
					}break;
					case 1:{
						holder.imgfocus.setVisibility(View.VISIBLE);
					}break;
				}
			}break;
			case 2:{
				 //0未关注 1已关注 2互相关注
				switch(bean.getAttentionState()){
					case 0:{
						//holder.btnOpt.setBackgroundResource(R.drawable.add_focus_button);
						setViewForAddFocus(holder);
						bean.setAddFocus(true);
						holder.imgfocus.setVisibility(View.GONE);
					}break;
					case 1:{
						//holder.btnOpt.setBackgroundResource(R.drawable.cancel_focus_button);
						setViewForCancelFocus(holder);
						bean.setAddFocus(false);
					}break;
					case 2:{
						//holder.btnOpt.setBackgroundResource(R.drawable.cancel_focus_button);
						holder.imgfocus.setVisibility(View.VISIBLE);
						setViewForCancelFocus(holder);
						bean.setAddFocus(false);
					}break;
				}
			}break;
		}
	}
	
	private void setViewForAddFocus(ViewHolder holder){
		holder.btnOptText.setText("关注");
		holder.btnOptText.setTextColor(Color.parseColor("#32963C"));
		holder.btnOptImg.setImageResource(R.drawable.userinfo_relationship_indicator_plus);
	}
	
	private void setViewForCancelFocus(ViewHolder holder){
		holder.btnOptText.setText("已关注");
		holder.btnOptText.setTextColor(Color.parseColor("#666666"));
		holder.btnOptImg.setImageResource(R.drawable.userinfo_relationship_indicator_tick_unfollow);
	}
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -2:{
					boolean isConn=Utils.isNetworkConn();
					if(isConn){
						Toast.makeText(context, "程序接口 错误", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(context, "网络不稳定或已断开", Toast.LENGTH_LONG).show();
					}
				}break;
				case -1:{
					Toast.makeText(context, "接口程序错误,未能正确处理请求", Toast.LENGTH_LONG).show();
				}break;
				//已关注
				case 0:{
				}
				//已互相关注
				case 1:{
					FansFocusBean bean=(FansFocusBean) msg.obj;
					FansFocusManager manager=new FansFocusManager();
					if(bean.isAddFocus()){//添加关注
						if(from==0){
							bean.setAttentionState(1);
						}else if(from==2){
							bean.setAttentionState(msg.what+1);
						}
						bean.setAddFocus(false);
						BCnotifyDataChange(1,bean.getUserId());
						manager.addLocalFocus(bean, XmppUtils.getUserId());
					}else{//取消关注
						if(from==0){
							bean.setAttentionState(0);
						}else if(from==1){
							dataList.remove(bean);
							FansFocusBean.focusCount--;
							activity.setFocusCount(FansFocusBean.focusCount);
						}else{
							bean.setAttentionState(0);
						}
						bean.setAddFocus(true);
						BCnotifyDataChange(0,bean.getUserId());
					}
					FansFocusAdapter.this.notifyDataSetChanged();
					
				}break;
			}
			if(null!=dialog && dialog.isShowing()){
				dialog.dismiss();
			}
		}
		
	};
	
	
	/**
	 * 广播通知数据改变
	 * type 1 添加关注
	 * type 0取消关注
	 * 
	 */
	private void BCnotifyDataChange(int type,int userId){
		Intent intent=new Intent(Constant.NOTIFY_FANSFOCUS_DATA_CHANGED);
		intent.putExtra("from", from);
		intent.putExtra("type", type);
		intent.putExtra("isOpenfire", false);
		intent.putExtra("userId", userId);
		context.sendBroadcast(intent);
	}
	
	
	 /* 
	 * @param holder
	 * @param imgUrl
	 */
	private void setImg(final ViewHolder holder,String imgUrl){
		Drawable drawable=asyncImageLoader.loadDrawable(imgUrl,true, new ImageCallback(){
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				if(null!=imageDrawable){
					holder.icon.setImageDrawable(imageDrawable);  
				}
				holder.icon.setScaleType(ImageView.ScaleType.FIT_XY);   
			}
		});
		if(null!=drawable){
			holder.icon.setImageDrawable(drawable); 
		}
	}
	
	
	private static final class ViewHolder{
		private RoundAngleImageView icon;
		private TextView nickName;
		private TextView sign;
		private LinearLayout btnOpt;
		private ImageView btnOptImg;
		private TextView btnOptText;
		private ImageView imgfocus;
	}
	
	public List<FansFocusBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<FansFocusBean> dataList) {
		this.dataList = dataList;
	}
	
	public void clear(){
		dataList.clear();
	}
	
}
