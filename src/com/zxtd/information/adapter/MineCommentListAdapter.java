package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineCommentListAdapter extends BaseAdapter {
	private List<CommentBean> mCommentBeans;
	private Context mContext;
	private LayoutInflater mInflater;
	private zxtd_AsyncImageLoader imageLoader;
	private zxtd_ImageCacheDao cacheDao;
	public MineCommentListAdapter(Context context, List<CommentBean> commentBeans){
		this.mContext = context;
		this.mCommentBeans = commentBeans;
		mInflater = LayoutInflater.from(mContext);
		imageLoader = new zxtd_AsyncImageLoader();
		cacheDao = zxtd_ImageCacheDao.Instance();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCommentBeans.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mCommentBeans.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Hoder hoder = null;
		if(arg1 == null){
			hoder = new Hoder();
			arg1 = mInflater.inflate(R.layout.mine_comment_item, null);
			hoder.imgHeader = (ImageView)arg1.findViewById(R.id.mine_self_header);
			hoder.txtNickname = (TextView) arg1.findViewById(R.id.comment_nick_name);
			hoder.txtTime = (TextView) arg1.findViewById(R.id.comment_time);
			hoder.txtContent = (TextView) arg1.findViewById(R.id.comment_content);
			hoder.txtOrigContent = (TextView) arg1.findViewById(R.id.orig_comment_content);
			hoder.txtOrigNewTitle = (TextView) arg1.findViewById(R.id.orig_news_title);
			hoder.toOtherMain = (LinearLayout) arg1.findViewById(R.id.line_to_other_main);
			arg1.setTag(hoder);
		}else{
			hoder = (Hoder)arg1.getTag();
		}
		
		CommentBean commentBean = mCommentBeans.get(arg0);
		if(commentBean != null){
			hoder.txtNickname.setText(commentBean.getNickName() + "[" + commentBean.getLocation() + "]");
			hoder.txtTime.setText(commentBean.getPublicTime());
			hoder.txtContent.setText(commentBean.getContent());
			if(Utils.isEmpty(commentBean.getOrigContent())){
				hoder.txtOrigContent.setVisibility(View.GONE);
			}else{
				hoder.txtOrigContent.setVisibility(View.VISIBLE);
				hoder.txtOrigContent.setText("【原帖】" + commentBean.getOrigContent());
			}
			
			hoder.imgHeader.setTag(arg0);
			hoder.toOtherMain.setTag(commentBean);
			
			setUrlImage(commentBean.getUserImg(), hoder.imgHeader, arg0);
			
			String newsTitle = commentBean.getTitle();
			if(Utils.isEmpty(newsTitle)){
				hoder.txtOrigNewTitle.setText("原文已被删除");
			}else{
				hoder.txtOrigNewTitle.setText("【原文】" + newsTitle);
			}
			hoder.toOtherMain.setOnClickListener(toOtherMain);
		}
		
		return arg1;
	}
	
	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView, int position){
		//Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = imageLoader.loadCacheDrawable(url, imageView, position,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				int currentPosition = Integer.parseInt(v.getTag().toString());
				if(currentPosition == position){
					if(imageDrawable != null){
						((ImageView) v).setImageDrawable(imageDrawable);
						imageDrawable = null;
					}else{
						((ImageView) v).setImageResource(R.drawable.transparent_color);
					}
				}	
			}
		}, cacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	static class Hoder{
		ImageView imgHeader;
		TextView txtNickname;
		TextView txtTime;
		TextView txtContent;
		TextView txtOrigContent;
		TextView txtOrigNewTitle;
		LinearLayout toOtherMain;
	}
	
	private OnClickListener toOtherMain = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CommentBean commentBean = (CommentBean)v.getTag();
			if(commentBean != null){
				try {
					if(commentBean.getUserId() == -1){
						Toast.makeText(mContext, "该用户不是注册用户", Toast.LENGTH_SHORT).show();
					}else{
						FansFocusBean bean = new FansFocusBean();
						bean.setUserId(commentBean.getUserId());
						Intent intent = new Intent(mContext, OtherMainActivity.class);
						intent.putExtra("bean", bean);
						mContext.startActivity(intent);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
