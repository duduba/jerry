package com.zxtd.information.ui.me.im;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxtd.information.adapter.NewAlbumsAdapter;
import com.zxtd.information.bean.ImageBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.listener.OnLeftOrRightListener;
import com.zxtd.information.parse.json.ParseInfoImages;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.pub.PublishActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.ImageViewPager;

public class AlbumsActivity extends BaseActivity{

	
	private List<ImageBean> imageBeans;
	private ImageViewPager zoomImagesView;
	private ImageView btnBackUp;
	private ImageView btnDownImage;
	private TextView countView;
	private TextView imageDescribe;
	private LinearLayout headerView;
	private LinearLayout footerView;
	
	private Animation footerOutAni;
	private Animation footerInAni;
	private Animation headerOutAni;
	private Animation headerInAni;
	
	private int currentIndex = 0;
	private boolean isLocal=false;
	private zxtd_AsyncImageLoader asyncImageLoader;
	
	private OnLeftOrRightListener leftOrRightListener = new OnLeftOrRightListener(){

		@Override
		public boolean onLeftOrRight(View view, EventType eventType) {
			if(eventType == EventType.RIGHT){
				if(currentIndex == 0){
					AlbumsActivity.this.finish();
					return true;
				}
			}
			return false;
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_image);

		Bundle extras = this.getIntent().getExtras();
		String imageJson = "";
		if (extras != null) {
			imageJson = extras.getString(Constant.BundleKey.IMGE_DATA);
			currentIndex = extras.getInt(Constant.BundleKey.IMAGE_INDEX);
			isLocal=extras.getBoolean("isLocal", false);
			Log.e(this.getClass().getName(), "json:" + imageJson);
		}
		
		ParseInfoImages infoImages = new ParseInfoImages();
		infoImages.parse(imageJson);
		imageBeans = infoImages.getImageBeans();
		if(!isLocal){
			asyncImageLoader = new zxtd_AsyncImageLoader();
		}
		
		zoomImagesView = (ImageViewPager) this.findViewById(R.id.image_gallery);
		btnBackUp = (ImageView) this.findViewById(R.id.btn_zoom_back);
		btnDownImage = (ImageView) this.findViewById(R.id.btn_zoom_down);
		countView = (TextView) this.findViewById(R.id.count_view);
		imageDescribe = (TextView) this.findViewById(R.id.image_describe);
		headerView = (LinearLayout) this.findViewById(R.id.header_view);
		footerView = (LinearLayout) this.findViewById(R.id.footer_view);
		
		NewAlbumsAdapter imageAdapter = new NewAlbumsAdapter(this, imageBeans,isLocal);
		zoomImagesView.setAdapter(imageAdapter);
		
		zoomImagesView.setOnPageChangeListener(itemSelectedListener);
		btnBackUp.setOnClickListener(onClickListener);
		btnDownImage.setOnClickListener(onClickListener);
		if(isLocal){
			btnDownImage.setVisibility(View.GONE);
		}
		zoomImagesView.setOnClickListener(imageOnClickListener);
		zoomImagesView.setOnTouchListener(leftOrRightListener);
		
		setCurrentData();
		creatAnimation();
	}
	
	
	private void creatAnimation(){
		footerOutAni = AnimationUtils.loadAnimation(this, R.anim.footer_view_out);
		footerInAni = AnimationUtils.loadAnimation(this, R.anim.footer_view_in);
		headerInAni = AnimationUtils.loadAnimation(this, R.anim.header_view_in);
		headerOutAni = AnimationUtils.loadAnimation(this, R.anim.header_view_out);
		
		AnimationListener animationListener = new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {
				if(footerInAni == animation){
					footerView.setVisibility(View.VISIBLE);
				}else if(headerInAni == animation){
					headerView.setVisibility(View.VISIBLE);
				}
			}
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			public void onAnimationEnd(Animation animation) {
				if(footerOutAni == animation){
					footerView.setVisibility(View.GONE);
				}else if(headerOutAni == animation){
					headerView.setVisibility(View.GONE);
				}
				
			}
		};
		
		footerInAni.setAnimationListener(animationListener);
		footerOutAni.setAnimationListener(animationListener);
		headerInAni.setAnimationListener(animationListener);
		headerOutAni.setAnimationListener(animationListener);
		
	}
	
	private void hideFooterAndHeader(){
		footerView.startAnimation(footerOutAni);
		headerView.startAnimation(headerOutAni);
	}
	
	private void showFooterAndHeader(){
		footerView.startAnimation(footerInAni);
		headerView.startAnimation(headerInAni);
	}
	
	private void setCurrentData(){
		int size = imageBeans.size();
		
		if(size != 0){
			String describe = imageBeans.get(currentIndex).describe;
			if(!Utils.isEmpty(describe)){
				imageDescribe.setText(describe.trim());
			}
			countView.setText((currentIndex + 1)+ "/" + size);
//			if(currentIndex != zoomImagesView.getSelectedItemPosition()){
//				//zoomImagesView.setSelection(currentIndex, false);
//			}
			zoomImagesView.setCurrentItem(currentIndex);
		}else{
			countView.setText("0/0");
		}
		
	}
	
	private OnPageChangeListener itemSelectedListener = new OnPageChangeListener() {
		
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
			setCurrentData();
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	private OnClickListener imageOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(footerView.getVisibility() == View.GONE){
				showFooterAndHeader();
			}else if(footerView.getVisibility() == View.VISIBLE){
				hideFooterAndHeader();
			}
			
		}
	};
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(v == btnBackUp){
				AlbumsActivity.this.finish();
			}else if(v == btnDownImage){
				downImage();
			}
		}
	};

	private void downImage(){
		if(imageBeans.size() == 0){
			return;
		}
		String imageUrl = imageBeans.get(currentIndex).imageUrl;
		
		if(!imageUrl.startsWith("http")){
			imageUrl=Constant.Url.HOST_URL + imageUrl;
		}                                                         //Constant.Url.HOST_URL +
			Drawable drawable = asyncImageLoader.loadCacheDrawable(imageUrl, null, 0, new ImageListCallback() {
				
				public void imageLoaded(Drawable imageDrawable, View view, int position) {
					if(imageDrawable != null){
						saveImage(((BitmapDrawable) imageDrawable).getBitmap());
						Toast.makeText(AlbumsActivity.this, "图片已保存到相册！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(AlbumsActivity.this, "图片下载失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				}
			}, zxtd_ImageCacheDao.Instance());
			
			if(drawable != null){
				saveImage(((BitmapDrawable) drawable).getBitmap());
				Toast.makeText(AlbumsActivity.this, "图片已保存到相册！", Toast.LENGTH_SHORT).show();
			}
		
	}
	
	private void saveImage(Bitmap bitmap){
		File file = new File(Constant.SDCARD_CAMERA_PATH, PublishActivity.getPhotoFileName());
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.close();
			this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
