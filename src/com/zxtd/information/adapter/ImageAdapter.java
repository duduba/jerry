package com.zxtd.information.adapter;

import java.util.List;
import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.util.Constant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class ImageAdapter extends BaseAdapter {

	   private List<Object> imageUrls;       //图片地址list   
	   public List<Object> getImageUrls() {
		return imageUrls;
	   }
	   

	  private Context context;  
	   AsyncImageLoader loader=new AsyncImageLoader();
	   private ImageSize size;
	   private int gItemBg;
	   private LayoutInflater inflater;

	   private static int width=120;
	   private static int height=140;
	   private boolean isShake=false;
	   private int selectIndex=-1;
	   public void setShake(boolean isShake) {
	    	this.isShake = isShake;
	    }
	
		public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}



		public void setSize(ImageSize size) {
	    	this.size = size;
	    }

		public ImageAdapter(List<Object> imageUrls, Context context) {  
	        this.imageUrls = imageUrls;  
	        this.context = context;
	        inflater=LayoutInflater.from(this.context);
	        //loader.setSize(new ImageSize(80,120));
	        /* 使用在res/values/attrs.xml中的<declare-styleable>定义* 的Gallery属性.*/
	          TypedArray a =context.obtainStyledAttributes(R.styleable.Gallery);
	          /*取得Gallery属性的Index id*/
	          gItemBg = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
	          /*让对象的styleable属性能够反复使用*/ 
	          a.recycle();
	          
	          DisplayMetrics display=context.getResources().getDisplayMetrics();
	         // MineNewActivity activity=(MineNewActivity) context;
	         // DisplayMetrics display=new DisplayMetrics();
	         // activity.getWindowManager().getDefaultDisplay().getMetrics(display);
	          int screenWidth=display.widthPixels;
	          //int screenHeight=display.heightPixels;
	          switch(screenWidth){
	          	case 320:{
	          		width=70;
	          		height=90;
	          	}break;
	          	case 480:{
	          		width=110;
	          		height=130;
	          	}break;
	          	case 540:{
	          		width=120;
	          		height=140;
	          	}break;
	          	case 640:{
	          		width=130;
	          		height=150;
	          	}break;
	          	case 720:{
	          		width=155;
	          		height=180;
	          	}break;
	          	case 800:{
	          		width=155;
	          		height=180;
	          	}break;
	          }
	         
	    }  
	  
	    public int getCount() {  
	        return imageUrls.size();  
	    }  
	  
	    public Object getItem(int position) {  
	        return imageUrls.get(position);  
	    }  
	  
	    public long getItemId(int position) {  
	        return position;  
	    }  
	   
	    public View getView(int position, View convertView, ViewGroup parent) { 
	    	Object obj=imageUrls.get(position);
	    	
	    	convertView=inflater.inflate(R.layout.mine_gallery_item_layout, null);
	    	FrameLayout mainLayout=(FrameLayout) convertView.findViewById(R.id.icon_framelayout);
	    	mainLayout.setLayoutParams(new Gallery.LayoutParams(width,height));
	    	
	    	ImageView image=(ImageView) convertView.findViewById(R.id.image);
	    	image.setAdjustViewBounds(true);   
			image.setScaleType(ScaleType.FIT_XY);
			image.setLayoutParams(new RelativeLayout.LayoutParams(width,height));  
	    	RelativeLayout deleteLayout=(RelativeLayout) convertView.findViewById(R.id.cross_icon_ll);
	    	
	    	Drawable drawable=null;
	    	if(obj instanceof NewImageBean){
	    		NewImageBean bean=(NewImageBean) obj;
	    		drawable=loadImage(bean.getSmallUrl(),image);
	    	}else{
	    		drawable=(Drawable) obj;
	    	}
	        if(null!=drawable){
	        	image.setImageDrawable(drawable);
	        }
	        if(isShake && position==selectIndex){
	        	shake(image,deleteLayout);
	        	convertView.findViewById(R.id.gallery_delete_icon).setOnClickListener(new ImageView.OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder=new AlertDialog.Builder(context);
						builder.setTitle("确定删除吗?");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
						builder.create().show();
					}
	        	});
	        }
	        return convertView;  
	    }  
	    
	    
	    private Drawable loadImage(String filePath,final ImageView imageView){
	    	 return loader.loadDrawable(filePath,true, new ImageCallback(){
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						// TODO Auto-generated method stub
						if(null!=imageDrawable){
							imageView.setImageDrawable(imageDrawable);  
						}else{
							imageView.setImageResource(R.drawable.album_load);
						}
						imageView.setScaleType(ImageView.ScaleType.FIT_XY);   
					}
		        });
	    }
	    
	    
	    public void add(Object obj){
	    	int size=imageUrls.size();
	    	if(size<Constant.MINE_PHOTO_SIZE){
	    		imageUrls.add(size-1, obj);
	    	}else{
	    		imageUrls.remove(size-1);
	    		this.imageUrls.add(size-1, obj);
	    	}
	    }
	    
	    
	    public void remove(int index){
	    	imageUrls.remove(index);
	    	int size=imageUrls.size();//size<Constant.MINE_PHOTO_SIZE && 
	    	if(size==Constant.MINE_PHOTO_SIZE-1 && imageUrls.get(size-1) instanceof NewImageBean){
	    		imageUrls.add(context.getResources().getDrawable(R.drawable.upload_new_photo));
	    	}
	    }
	    
	    
	    
	    private boolean isanimation ;
	    private void shake(View view,RelativeLayout crossIconLL){
	    	Animation shake = AnimationUtils.loadAnimation(
					context, R.anim.anim);
			shake.reset();
			shake.setFillAfter(true);
			shake.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					isanimation = true;
				}
				
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					isanimation = false;
				}
			});
			view.startAnimation(shake);
			crossIconLL.setVisibility(View.VISIBLE);
	    }
	
}
