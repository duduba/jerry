package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.ImageBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.gestureimage.GestureImageView;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ZoomImageAdapter extends PagerAdapter {
	private List<ImageBean> mImageBeans;
	private LayoutInflater inflater;
	private zxtd_ImageCacheDao cacheDao;
	private zxtd_AsyncImageLoader asyncImageLoader;
	//private boolean isCache=false;
	public ZoomImageAdapter(Context context, List<ImageBean> imageBeans){
		this.mImageBeans = imageBeans;
		inflater = LayoutInflater.from(context);
		cacheDao = zxtd_ImageCacheDao.Instance();
		asyncImageLoader = new zxtd_AsyncImageLoader();
	}
	
	/*
	public ZoomImageAdapter(Context context, List<ImageBean> imageBeans,boolean cache){
		this.mImageBeans = imageBeans;
		inflater = LayoutInflater.from(context);
		cacheDao = zxtd_ImageCacheDao.Instance();
		asyncImageLoader = new zxtd_AsyncImageLoader();
		isCache=cache;
	}
	*/
	
	
	@Override
	public int getCount() {
		return mImageBeans.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position){

		View view = inflater.inflate(R.layout.zoom_image_item, null);
		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		GestureImageView zoomImageView = (GestureImageView) view.findViewById(R.id.zoom_image);
		LinearLayout netError = (LinearLayout) view.findViewById(R.id.net_error);
		
		ImageBean imageBean = mImageBeans.get(position);
		if(imageBean != null){
			zoomImageView.setTag(position);
			setUrlImage( imageBean.imageUrl, zoomImageView, position, netError, progressBar);
			netError.setOnClickListener(new NetErrorClick(imageBean.imageUrl, zoomImageView, position, netError, progressBar));
		}
		
		container.addView(view, 0);
		return view;
	}
	
	public void setUrlImage(String url, GestureImageView zoomImageView, int position, final LinearLayout netError, final ProgressBar progressBar){
		netError.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		//http://test.zaseinfo.com/upLoad/user/image/imgs/20130913845320.jpg
		//http://test.zaseinfo.com/upLoad/user/image/imgs/20130913845320.jpg
		Drawable drawable = asyncImageLoader.loadCacheDrawable(url, zoomImageView, position,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				int currentPosition = Integer.parseInt(v.getTag().toString());
				if(currentPosition == position){
					Log.e(Constant.TAG, ""+imageDrawable);
					if(imageDrawable != null){
						((GestureImageView) v).setImageDrawable(imageDrawable);
						imageDrawable = null;
					}else{
						((GestureImageView) v).setImageResource(R.drawable.transparent_color);
						netError.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						Toast.makeText(inflater.getContext(), "网络不给力！", Toast.LENGTH_SHORT).show();
					}
				}	
			}
		}, cacheDao);
		if(drawable != null){
			zoomImageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			zoomImageView.setImageResource(R.drawable.transparent_color);
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View)object);
	}

	private class NetErrorClick implements OnClickListener{
		private String mUrl;
		private GestureImageView mGestureImageView;
		private int mPosition;
		private LinearLayout mNetError;
		private ProgressBar mProgressBar;
		public NetErrorClick(String url, GestureImageView zoomImageView, int position, LinearLayout netError, ProgressBar progressBar){
			mUrl = url;
			mGestureImageView = zoomImageView;
			mPosition = position;
			mNetError = netError;
			mProgressBar = progressBar;
		}
		public void onClick(View v) {
			setUrlImage(mUrl, mGestureImageView, mPosition, mNetError, mProgressBar);
		}
	}
}
