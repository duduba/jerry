package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class RadioImageSelector extends LinearLayout {
	private int mSpace = 0;
	private List<String> mImgUrls;
	private zxtd_AsyncImageLoader mAsyncImageLoader;
	private zxtd_ImageCacheDao mImageCacheDao;
	private int curIndex = -1;
	private List<ImageView> imgSignViews;
	
	private OnImageSelectListener mOnImageSelectListener;
	
	public RadioImageSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RadioImageSelector(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mImgUrls = new ArrayList<String>();
		mAsyncImageLoader = new zxtd_AsyncImageLoader();
		mImageCacheDao = zxtd_ImageCacheDao.Instance();
		imgSignViews = new ArrayList<ImageView>();
	}
	
	public void setItemSpace(int spaceDip){
		mSpace = Utils.dipToPx(spaceDip);
	}
	
	public void addImages(List<String> imgUrls){
		mImgUrls.addAll(imgUrls);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed && this.getChildCount() == 0){
			addChildren();
			if(mOnImageSelectListener != null){
				mOnImageSelectListener.onImageSelect(curIndex);
			}
		}
	}
	
	public void setSelectItem(int index){
		curIndex = index;
	}
	
	private void addChildren(){
		int size = mImgUrls.size();
		if(size > 0 && curIndex == -1){
			curIndex = 0;
		}
		for(int i = 0; i < size; i ++){
			View view = createChildView(mImgUrls.get(i), i);
			LayoutParams params = new LayoutParams(Utils.dipToPx(53), Utils.dipToPx(53));
			if(i != 0){
				if(this.getOrientation() == HORIZONTAL){
					params.leftMargin = mSpace;
				}else{
					params.topMargin = mSpace;
				}
			}
			view.setLayoutParams(params);
			view.setOnClickListener(mOnClickListener);
			view.setTag(R.id.tag_key_position, i);
			this.addView(view);
		}
	}
	
	private View createChildView(String imgUrl, int position){
		RelativeLayout itemView = new RelativeLayout(this.getContext());
		
		ImageView imgView = new ImageView(this.getContext());
		RelativeLayout.LayoutParams imgLyParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		imgView.setLayoutParams(imgLyParams);
		imgView.setBackgroundColor(0xffc0c0c0);
		imgView.setScaleType(ScaleType.CENTER_CROP);
		setUrlImage(imgUrl, imgView);
		
		ImageView imgSignView = new ImageView(this.getContext());
		RelativeLayout.LayoutParams imgSignLyParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		imgSignLyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imgSignLyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		imgSignView.setLayoutParams(imgSignLyParams);
		imgSignView.setImageResource(R.drawable.image_select_sign_icon);
		imgSignViews.add(imgSignView);
		
		if(position == curIndex){
			imgSignView.setVisibility(View.VISIBLE);
		}else{
			imgSignView.setVisibility(View.GONE);
		}
		
		itemView.addView(imgView);
		itemView.addView(imgSignView);
		
		return itemView;
	}
	
	public void setOnImageSelectListener(OnImageSelectListener imageSelectListener){
		mOnImageSelectListener = imageSelectListener;
	}
	
	private void setUrlImage(String url, ImageView imgView){
		Drawable drawable = mAsyncImageLoader.loadCacheDrawable(url, imgView, 0,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				if(imageDrawable != null){
					((ImageView) v).setImageDrawable(imageDrawable);
					imageDrawable = null;
				}else{
					((ImageView) v).setImageResource(R.drawable.transparent_color);
				}
			}
		}, mImageCacheDao);
		if(drawable != null){
			imgView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imgView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	public int getSelectIndex(){
		return curIndex;
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)v.getTag(R.id.tag_key_position);
			if(position != curIndex){
				ImageView curView = imgSignViews.get(curIndex);
				ImageView clickView = imgSignViews.get(position);
				curView.setVisibility(View.GONE);
				clickView.setVisibility(View.VISIBLE);
				curIndex = position;
				if(mOnImageSelectListener != null){
					mOnImageSelectListener.onImageSelect(curIndex);
				}
			}
		}
	};
	
	public interface OnImageSelectListener{
		void onImageSelect(int index);
	}
	
}
