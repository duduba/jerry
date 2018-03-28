package com.zxtd.information.ui.pub;

import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.ScaleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImagesContainer extends LinearLayout {
	private OnItemClickListener mItemClickListener;
	private zxtd_AsyncImageLoader mAsyncImageLoader;
	public ImagesContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImagesContainer(Context context) {
		super(context);
		init();
	}
	
	
	private void init(){
		mAsyncImageLoader = new zxtd_AsyncImageLoader();
	}
	
	public void addImage(Drawable drawable){
		ScaleImageView imageView = new ScaleImageView(this.getContext());
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin = Utils.dipToPx(10);
		imageView.setImageDrawable(drawable);
		imageView.setLayoutParams(params);
		imageView.setTag(R.id.tag_image_item, this.getChildCount());
		imageView.setOnClickListener(mOnClickListener);
		this.addView(imageView);
	}
	
	public void addImage(String imageFile){
		Bitmap bitmap = Utils.decodeSampledBitmapFromResource(imageFile, Utils.dipToPx(200), Utils.dipToPx(200));
		Drawable drawable = new BitmapDrawable(bitmap);
		addImage(drawable);
	}
	
	public void addUrl(String url){
		ScaleImageView imageView = new ScaleImageView(this.getContext());
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin = Utils.dipToPx(10);
		imageView.setLayoutParams(params);
		imageView.setTag(R.id.tag_image_item, this.getChildCount());
		imageView.setOnClickListener(mOnClickListener);
		setUrlImage(url, imageView);
		this.addView(imageView);
	}
	
	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView) {
		// Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = mAsyncImageLoader.loadCacheDrawable(url, imageView,
				0, new ImageListCallback() {
					public void imageLoaded(Drawable imageDrawable, View v,
							int position) {
						if (imageDrawable != null) {
							((ImageView) v).setImageDrawable(imageDrawable);
							imageDrawable = null;
						} else {
							((ImageView) v)
									.setImageResource(R.drawable.transparent_color);
						}
					}
				}, zxtd_ImageCacheDao.Instance());
		if (drawable != null) {
			imageView.setImageDrawable(drawable);
			drawable = null;
		} else {
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int pos = (Integer)v.getTag(R.id.tag_image_item);
			if(mItemClickListener != null){
				mItemClickListener.onItemClick(v, pos);
			}
		}
	};
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.mItemClickListener = onItemClickListener;
	}
	
	public interface OnItemClickListener{
		void onItemClick(View v, int position);
	}
}
