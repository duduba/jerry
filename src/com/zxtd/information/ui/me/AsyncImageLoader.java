package com.zxtd.information.ui.me;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ImageUtils;
import com.zxtd.information.util.Utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader {

	  private ImageSize size;
	  
	   
	  public void setSize(ImageSize size) {
	    this.size = size;
	  }
	
	private Map<String, SoftReference<Drawable>> imageCache=new HashMap<String, SoftReference<Drawable>>();
	
	public Drawable loadDrawable(final String imageUrl,final boolean isCache,final ImageCallback callback){
		if(imageCache.containsKey(imageUrl)){
			SoftReference<Drawable> softReference=imageCache.get(imageUrl);
			if(softReference.get()!=null){
				return softReference.get();
			}
		}
		File file=new File(Constant.CACHE_SAVE_PATH);
		if(!file.exists()){
			file.mkdirs();
		}else{
			file=new File(Constant.CACHE_SAVE_PATH+ImageUtils.getFileName(imageUrl));
			if(file.exists()){
				return new BitmapDrawable(ImageUtils.loadBitmap(file));
			}
		}
		
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
			}
		};
		new Thread(){
			public void run() {
				Drawable drawable=loadImageFromUrl(imageUrl,isCache);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				handler.sendMessage(handler.obtainMessage(0,drawable));
			};
		}.start();
		return null;
	}
	
	protected Drawable loadImageFromUrl(String url,boolean isCache) {
		try {
			Log.e(Constant.TAG, "down image form url: "+url);
			 Bitmap bitmap=null;
			 InputStream inStream=new URL(url).openStream();
			 if(null!=size){
      		   BitmapFactory.Options options = new BitmapFactory.Options();
      		   options.inJustDecodeBounds = true;
      		   BitmapFactory.decodeStream(new URL(url).openStream(),null,options);  
      		   //计算缩放倍数
      		    options.inSampleSize =Utils.calculateInSampleSize(options, size.getWith(),
      					size.getHeight());
      		    options.inJustDecodeBounds = false;
      		    bitmap=BitmapFactory.decodeStream(inStream,null,options); 
      	   	}else{
      	   	    bitmap=BitmapFactory.decodeStream(inStream);
      	   	   //return Drawable.createFromStream(new URL(url).openStream(),"src");
			}
			if(bitmap!=null && bitmap.isRecycled()){
   	   			bitmap.recycle();
   	   		}
			if(isCache){
				String filePath=Constant.CACHE_SAVE_PATH+ImageUtils.getFileName(url);
				FileOutputStream fos = new FileOutputStream(filePath);
	        	byte[] buffer = new byte[1024];
	        	int count = 0;
	        	inStream=new URL(url).openStream();
	        	while ((count = inStream.read(buffer)) > 0){
	        		fos.write(buffer, 0, count);
	        	}
	        	fos.flush();
	        	fos.close();
	        	inStream.close();
			}
			return new BitmapDrawable(bitmap);
		} catch (Exception e) {
			Utils.printException(e);
			return null;
		}
	}

	public interface ImageCallback{
		public void imageLoaded(Drawable imageDrawable,String imageUrl);
	}
	

	

	
}
