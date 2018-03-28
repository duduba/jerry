package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageCallback;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.gestureimage.GestureImageView;
import com.zxtd.information.ui.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ManageImagesAdapter extends PagerAdapter {
	private Context mContext;
	private List<String> mImageFileNames;
	private LayoutInflater mInflater;
	private zxtd_AsyncImageLoader imageLoader;
	private ImageSize mSize;
	public ManageImagesAdapter(Context context, List<String> imageFileNames){
		mContext = context;
		mImageFileNames = imageFileNames;
		mInflater = LayoutInflater.from(mContext);
		imageLoader = new zxtd_AsyncImageLoader();
		mSize = new ImageSize(400, 800);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageFileNames.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mInflater.inflate(R.layout.manage_image_item, null);
		GestureImageView imageView = (GestureImageView)view.findViewById(R.id.zoom_image);
		imageView.setTag(position);
		String fileImage = mImageFileNames.get(position);
		if(fileImage.startsWith("http:")){
			setUrlImage(fileImage, imageView, position);
		}else{
			setImage(imageView, fileImage);
		}
		
		container.addView(view, 0);
		return view;
	}
	
	
	public void setUrlImage(String url, GestureImageView zoomImageView, int position){

		Drawable drawable = imageLoader.loadCacheDrawable(url, zoomImageView, position,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				int currentPosition = (Integer)v.getTag();
				if(currentPosition == position){
					if(imageDrawable != null){
						((GestureImageView) v).setImageDrawable(imageDrawable);
						imageDrawable = null;
					}else{
						((GestureImageView) v).setImageResource(R.drawable.transparent_color);
					}
				}	
			}
		}, zxtd_ImageCacheDao.Instance());
		if(drawable != null){
			zoomImageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			zoomImageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	
	private void setImage(GestureImageView view, String fileName){
		Drawable drawable = imageLoader.loadLocalDrawable(fileName, view, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, View view) {
				if(imageDrawable != null){
					((GestureImageView)view).setImageDrawable(imageDrawable);
				}
			}
		}, mSize);
		if(drawable != null){
			view.setImageDrawable(drawable);
		}
	}
	

}
