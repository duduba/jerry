package com.zxtd.information.ui.me;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.util.Utils;


public class SingelPhotoShowActivity extends BaseActivity implements OnClickListener{

	private ImageView header;
	private  Bitmap bitmap;  
	
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix rotateMatrix =null;
	private PointF start = new PointF();
	private PointF mid = new PointF();
    DisplayMetrics dm; 
	float minScaleR=0.1f;  //最少缩放比例   
	static final float MAX_SCALE = 20f; //最大缩放比例   
	float dist = 1f;  
	private int screenWidth=0;
	private int screenHeight=0;
	private String imgUrl="";
	private AsyncImageLoader loader=new AsyncImageLoader();
	private int degrees=0;
	private float initScale=3f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_single_photo_layout);
		//loader.setSize(new ImageSize(100,100));
		imgUrl=getIntent().getStringExtra("imgUrl");
		//imgUrl="http://a.hiphotos.baidu.com/album/w%3D2048%3Bq%3D75/sign=26718697fcfaaf5184e386bfb86caf9f/1f178a82b9014a901e69680ca8773912b31bee28.jpg";
		initView();
	}

	private void noPhoto(){
		bitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.mine_user_logo);
		header.setImageBitmap(bitmap);
		initBitmap();
	}
	
	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.download).setOnClickListener(this);
		findViewById(R.id.rotate_left).setOnClickListener(this);
		findViewById(R.id.rotate_right).setOnClickListener(this);
		findViewById(R.id.zoom_out).setOnClickListener(this);
		findViewById(R.id.zoom_in).setOnClickListener(this);
		header=(ImageView) findViewById(R.id.show_header);
		Drawable drawable=null;
		if(!TextUtils.isEmpty(imgUrl) && !"null".equals(imgUrl)){
			drawable=loadImage(imgUrl);
			if(null!=drawable){
				header.setImageDrawable(drawable); 
				BitmapDrawable bd = (BitmapDrawable) drawable;
				bitmap= bd.getBitmap();
				initBitmap();
			}else{
				noPhoto();
			}
		}else{
			noPhoto();
		}
		
		
		header.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x, event.getY()- start.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}
				CheckScale();//限制缩放范围   
			    //center(true,true);//居中控制 
				view.setImageMatrix(matrix);
				return true;
			}
			
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}});
	}

	
	   private Drawable loadImage(String filePath){
	    	 return loader.loadDrawable(filePath,true, new ImageCallback(){
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						// TODO Auto-generated method stub
						if(null!=imageDrawable){
							header.setImageDrawable(imageDrawable);  
							BitmapDrawable bd = (BitmapDrawable) imageDrawable;
							bitmap= bd.getBitmap();
						}
					}
		        });
	    }
	
	 //限制最大最小缩放比例     
    protected void CheckScale()  
    {  
        float p[] = new float[9];  
        matrix.getValues(p);  
        if (mode == ZOOM)  
        {  
            if (p[0] < minScaleR)  
            {  
                matrix.postScale(minScaleR, minScaleR);  
            }  
            if (p[0] > MAX_SCALE)  
            {  
                matrix.set(savedMatrix);  
            }  
        }  
    } 
    
    
    /*
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }*/
	
    
	   private void center(boolean horizontal, boolean vertical){
	        Matrix m = new Matrix();  
	        m.set(matrix);  
	       
	        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());  
	        m.mapRect(rect);  
	        float height = rect.height();  
	        float width = rect.width();  
	        float deltaX = 0, deltaY = 0;  
	        if (vertical)  
	        {  
	           // int screenHeight =screenHeight;  //手机屏幕分辨率的高度   
	            //int screenHeight = 400;  
	            if (height < screenHeight)  
	            {  
	                deltaY = (screenHeight - height)/2 - rect.top;  
	            }else if (rect.top > 0)  
	            {  
	                deltaY = -rect.top;  
	            }else if (rect.bottom < screenHeight)  
	            {  
	                deltaY = header.getHeight() - rect.bottom;  
	            }  
	        }  
	          
	        if (horizontal)  
	        {  
	           // int screenWidth =screenWidth;  //手机屏幕分辨率的宽度   
	            //int screenWidth = 400;  
	            if (width < screenWidth)  
	            {  
	                deltaX = (screenWidth - width)/2 - rect.left;  
	            }else if (rect.left > 0)  
	            {  
	                deltaX = -rect.left;      
	            }else if (rect.right < screenWidth)  
	            {  
	                deltaX = screenWidth - rect.right;  
	            }  
	        }  
	        matrix.postTranslate(deltaX, deltaY);  
	        header.setImageMatrix(matrix);
	    }
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.download:{
				download();
			}break;
			case R.id.rotate_left:{
				degrees-=90;
				rotate();
			}break;
			case R.id.rotate_right:{
				degrees+=90;
				rotate();
			}break;
			case R.id.zoom_out:{
				if(initScale<MAX_SCALE){
					if(initScale>=1){
						initScale+=1f;
					}else{
						initScale+=0.1f;
					}
					savedMatrix.set(matrix);
					matrix.setScale(initScale, initScale);
					header.setImageMatrix(matrix);
					center(true,true);
				}
			}break;
			case R.id.zoom_in:{
				if(initScale>minScaleR){
					if(initScale>=2){
						initScale-=1f;
					}else{
						initScale-=0.1f;
					}
					savedMatrix.set(matrix);
					matrix.setScale(initScale, initScale);
					header.setImageMatrix(matrix);
					center(true,true);
				}
			}break;
		}
	}
	
	private void initBitmap(){
		LinearLayout layout=(LinearLayout) findViewById(R.id.mine_single_opt_layout);
		int width=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int height=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		layout.measure(width, height);
		
		dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm); //获取分辨率   
		screenWidth=dm.widthPixels;
		screenHeight=dm.heightPixels-47-20-20-layout.getMeasuredHeight();

		matrix.setScale(initScale, initScale);
		center(true,true);
		header.setImageMatrix(matrix);
		rotate();
	}
	
	
	private void rotate(){
		if(null==rotateMatrix){
			rotateMatrix=new Matrix(); 
		}
		rotateMatrix.setRotate(degrees);
		Bitmap savebitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
		header.setImageBitmap(savebitmap);
	}
	
	
	/**
	 * 下载
	 */
	private void download(){
		String dirPath=Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera";
		File file=new File(dirPath);
		if(!file.exists()){
			file.mkdirs();
		}
		String Suffix=".png";
		if(!TextUtils.isEmpty(imgUrl) && !"null".equals(imgUrl)){
			if(imgUrl.endsWith(".jpg") || imgUrl.endsWith(".jpeg")){
				Suffix=".jpg";
			}
		}
		String filePath=dirPath+"/"+System.currentTimeMillis()+Suffix;
		file=new File(filePath);
		try{
			file.createNewFile();
			FileOutputStream outStream=new FileOutputStream(file);
			if(".png".equals(Suffix)){
				bitmap.compress(CompressFormat.PNG, 100, outStream);
			}else{
				bitmap.compress(CompressFormat.JPEG, 100, outStream);
			}
			outStream.flush();
			outStream.close();
			Toast("已保存到手机相册..");
		}catch(Exception ex){
			Utils.printException(ex);
		}
	}
	
	
}
