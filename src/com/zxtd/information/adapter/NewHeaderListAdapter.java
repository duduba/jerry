package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.listener.OnImageClickListener;
import com.zxtd.information.ui.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NewHeaderListAdapter extends PagerAdapter {
	private List<Bean> mNewBeans;
	private boolean isLock = false;
	private zxtd_AsyncImageLoader imageLoader = new zxtd_AsyncImageLoader();
	private zxtd_ImageCacheDao cacheDao = zxtd_ImageCacheDao.Instance();
	private LayoutInflater mInflater;
	public NewHeaderListAdapter(Context context, List<Bean> newBeans){
		mNewBeans = newBeans;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View item = mInflater.inflate(R.layout.new_list_header_item, null);
		
		if(!isLock ){
			
			NewBean newBean = (NewBean)mNewBeans.get(position);
			if(newBean != null){
				ImageView itemImage = (ImageView)item.findViewById(R.id.new_header_list_item_image);
				setUrlImage(newBean.iconUrl, itemImage);
				item.setOnClickListener(new OnImageClickListener(newBean));
			}
			container.addView(item, 0);
		}
		Log.i(this.getClass().getName(), "当前显示项：" + position);
		return item;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView){
		Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = imageLoader.loadCacheDrawable(url, imageView, 0,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				if(imageDrawable != null && v != null){
					((ImageView) v).setImageDrawable(imageDrawable);
				}else{
					((ImageView) v).setImageResource(R.drawable.transparent_color);
				}
				
			}
		}, cacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
		}else{
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	public void setLock(boolean lock){
		isLock = lock;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNewBeans.size();
	}


	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
}
