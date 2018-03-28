package com.zxtd.information.util;

import java.io.File;
import java.io.FileInputStream;
import com.zxtd.information.application.ZXTD_Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {

	public static Bitmap loadBitmap(String filePath){
		try{
			 BitmapFactory.Options opts = new BitmapFactory.Options();
			 opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
			 BitmapFactory.decodeFile(filePath, opts);
			 int width=ZXTD_Application.getMyContext().getResources().getDisplayMetrics().widthPixels;
			 int height=ZXTD_Application.getMyContext().getResources().getDisplayMetrics().heightPixels;
			 if(opts.outWidth>width || opts.outHeight>height){
				 opts.inSampleSize =Utils.calculateInSampleSize(opts, width,
						 height);
			 }
			 opts.inJustDecodeBounds = false;
			 opts.inPurgeable = true;
			 opts.inInputShareable = true;
			 opts.inDither = false;
			 opts.inPurgeable = true;
			 opts.inTempStorage = new byte[16 * 1024];
			 Bitmap bmp = null;
			 FileInputStream is= new FileInputStream(filePath);
			 bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);           
			 double scale = getScaling(opts.outWidth * opts.outHeight, height * width);
			 Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,(int) (opts.outWidth * scale),(int) (opts.outHeight * scale), true);
			 if(bmp.isRecycled())
				 bmp.recycle();
			 if(bmp2.isRecycled())
				 bmp2.recycle();
			 is.close();
			 System.gc();
			 return bmp2;
		}catch(Exception ex){
			Utils.printException(ex);
			return showBitmap(filePath);
		}
	}
	
	
	public static Bitmap showBitmap(String filePath){
		try{
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inPreferredConfig=Bitmap.Config.RGB_565;
			options.inPurgeable=true;
			options.inInputShareable=true;
			options.inTempStorage = new byte[16 * 1024];
			Bitmap bitmap=BitmapFactory.decodeFileDescriptor(new FileInputStream(filePath).getFD(), null, options);
			if(null!=bitmap && bitmap.isRecycled()){
				bitmap.recycle();
			}
			return bitmap;
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return null;
	}
	
	
	
	
	private static double getScaling(int src, int des){
		/**
		48
		 * 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比
		49
		 */
		   double scale = Math.sqrt((double) des / (double) src);
		   return scale;
		}

	
	public static Bitmap compressImage(String filePath){
		try{
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			FileInputStream inStream=new FileInputStream(filePath);
			BitmapFactory.decodeStream(inStream, null, options);
			int width=500;//ZXTD_Application.getMyContext().getResources().getDisplayMetrics().widthPixels;
			int height=800;//ZXTD_Application.getMyContext().getResources().getDisplayMetrics().heightPixels;
			if(options.outWidth>width || options.outHeight>height){
				options.inSampleSize =Utils.calculateInSampleSize(options, width,
						 height);
			 }
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inDither = false;
			options.inPurgeable = true;
			options.inTempStorage = new byte[16 * 1024];
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(inStream.getFD(), null, options); 
			if(null!=bitmap && bitmap.isRecycled()){
				bitmap.recycle();
			}
			inStream.close();
			System.gc();
			return bitmap;
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return null;
	}
	
	
	
	public static Bitmap loadBitmap(File file){
		try{
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inPreferredConfig=Bitmap.Config.RGB_565;
			options.inPurgeable=true;
			options.inInputShareable=true;
			Bitmap bitmap=BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			if(null!=bitmap && bitmap.isRecycled()){
				bitmap.recycle();
			}
			return bitmap;
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return null;
	}
	
	
	public static String getFileName(String filepath){
		int index=filepath.lastIndexOf("/");
		String fileName=filepath.substring(index+1);
		if(fileName.indexOf(".")>0){
			return fileName;
		}else{
			return fileName+"_"+XmppUtils.getUserId()+".jpg";
		}
	}
	
	
	
	public static String getImageSuffix(String filepath){
		int index=filepath.lastIndexOf(".");
		return filepath.substring(index+1);
	}
	
	
}
