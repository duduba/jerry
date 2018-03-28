package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Bean> newBeans;
	private zxtd_AsyncImageLoader imageLoader = new zxtd_AsyncImageLoader();
	private boolean isLock = false;
	private zxtd_ImageCacheDao imageCacheDao;

	public NewListAdapter(Context context, List<Bean> newBeans){
		this.mContext = context;
		this.newBeans = newBeans;
		imageCacheDao = zxtd_ImageCacheDao.Instance();
		
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return newBeans.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return newBeans.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if(!isLock || arg1 == null){
			NewBean newBean = (NewBean)newBeans.get(arg0);
			Hoder hoder = null;
			if(arg1 == null){
				hoder = new Hoder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				arg1 = inflater.inflate(R.layout.new_list_item, null);
				hoder.newTitle = (TextView) arg1.findViewById(R.id.new_title);
				hoder.newInfo = (TextView) arg1.findViewById(R.id.new_info);
				hoder.postCount = (TextView) arg1.findViewById(R.id.new_reply_count);
				hoder.newImage = (ImageView) arg1.findViewById(R.id.new_image);
				hoder.isNetFriend = (ImageView) arg1.findViewById(R.id.is_net_friend);
				arg1.setTag(hoder);
			}else{
				hoder = (Hoder) arg1.getTag();
			}
			
			arg1.setBackgroundResource(R.drawable.list_selector);
			if(newBean != null){
				hoder.newImage.setTag(arg0);
				setUrlImage(newBean.iconUrl, hoder.newImage, arg0);
				hoder.newTitle.setText(newBean.newTitle);
				if(Utils.newClickedItem.contains(newBean.newId)){
					hoder.newTitle.setTextColor(0xff898888);
				}else{
					hoder.newTitle.setTextColor(0xff1c1c1c);
				}
				hoder.newInfo.setText(newBean.newOutline);
				hoder.postCount.setText(newBean.postCount);
				if(Constant.ItemFlag.NET_FRIEND_ITEM.equals(newBean.flag)){
					hoder.isNetFriend.setVisibility(View.VISIBLE);
				}else if(Constant.ItemFlag.NEW_ITEM.equals(newBean.flag)){
					hoder.isNetFriend.setVisibility(View.GONE);
				}else{
					hoder.isNetFriend.setVisibility(View.GONE);
				}
			}
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
		}, imageCacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	public void setLock(boolean lock){
		//isLock = lock;
	}
	
	private static class Hoder{
		TextView newTitle;
		TextView newInfo;
		ImageView newImage;
		TextView postCount;
		ImageView isNetFriend;
	}

}
