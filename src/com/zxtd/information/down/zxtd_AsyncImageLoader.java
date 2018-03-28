package com.zxtd.information.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.util.Utils;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.view.View;

public class zxtd_AsyncImageLoader {
	// 为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问，比如在ListView时来回滚动）
		private final int hardCachedSize = 4*1024*1024;
		private static final int SOFT_CACHE_CAPACITY = 10; 
		public Map<String, SoftReference<Drawable>> imageCache = Collections.synchronizedMap(new HashMap<String, SoftReference<Drawable>>());
		public Map<String, WeakReference<Drawable>> bitmapCache = new HashMap<String, WeakReference<Drawable>>();
		public Map<String, SoftReference<Bitmap>> imageSoftCache = Collections.synchronizedMap(new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true){

			
			@Override
			protected boolean removeEldestEntry(
					Entry<String, SoftReference<Bitmap>> eldest) {
				if(size() > SOFT_CACHE_CAPACITY){
					
					return true;
				}
				return false;
			}
			
			
			
		});
		private Set<String> dowingUrl = new HashSet<String>();
		public LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(hardCachedSize){
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes()*value.getHeight();
			};
			
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
				imageSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			};
			
		};
		
		private ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
		private ExecutorService urlExecutorService = Executors.newFixedThreadPool(2);
		private final Handler handler = new Handler();

		public static class ImageSize{
			private int mWith;
			private int mHeight;
			public ImageSize(int with, int heigth){
				mHeight = heigth;
				mWith = with;
			}
			public int getWith(){
				return mWith;
			}
			public int getHeight(){
				return mHeight;
			}
		}
		
		
		
		public Drawable loadCacheDrawable(final String imageUrl, final View view,final int position, final ImageListCallback callback, final zxtd_ImageCacheDao imageCacheDao){
				
			if(imageUrl == null || imageUrl.equals("")){
				return null;
			}
			synchronized (lruCache) {
				Bitmap bitmap = lruCache.get(imageUrl);
				if(bitmap != null && !bitmap.isRecycled()){
					BitmapDrawable drawable= new BitmapDrawable(bitmap);
					//callback.imageLoaded(drawable, view, position);
					return drawable;
				}
			}
					
			if(imageSoftCache.containsKey(imageUrl)){
				SoftReference<Bitmap> softReference = imageSoftCache.get(imageUrl);
				if(softReference.get() != null && !softReference.get().isRecycled()){
					BitmapDrawable drawable=new BitmapDrawable(softReference.get());
					//callback.imageLoaded(drawable, view, position);
					return drawable;
				}
			}
			
			/*
			File file = new File(Constant.IMAGE_CACHE_FILE, ImageUtils.getFileName(imageUrl));
			if(file.exists()){
				BitmapDrawable drawable= new BitmapDrawable(ImageUtils.loadBitmap(file));
				callback.imageLoaded(drawable, view, position);
				return drawable;
			}
			*/
			
			
			
			final String imageFile = imageCacheDao.getImageCacheFile(imageUrl);
			if(!Utils.isEmpty(imageFile)){
				executorService.execute(new Runnable() {
					public void run() {
						Bitmap bitmap = BitmapFactory.decodeFile(imageFile);
						synchronized (imageCacheDao) {
							if(bitmap == null){
								imageCacheDao.deleteImageByFileName(imageFile);
								return;
							}
						}
						
						lruCache.put(imageUrl, bitmap);
						bitmap = null;
						handler.post(new Runnable() {
							
							
							public void run() {
								Bitmap bitmap = lruCache.get(imageUrl);
								if(bitmap != null && !bitmap.isRecycled()){
									BitmapDrawable drawable = new BitmapDrawable(bitmap);
									bitmap = null;
									callback.imageLoaded(drawable, view, position);
								}
							}
						});
					}
				});
				
			}else{
				if(dowingUrl.contains(imageUrl)){
					return null;
				}
				urlExecutorService.execute(new Runnable() {
					
					public void run() {
						
						dowingUrl.add(imageUrl);
						Bitmap bitmap = loadBitmapFromUrl(imageUrl);
						if(bitmap == null){
							dowingUrl.remove(imageUrl);
							handler.post(new Runnable() {
								public void run() {
									callback.imageLoaded(null, view, position);
								}
							});
							return;
						}
						lruCache.put(imageUrl, bitmap);
						bitmap = null;
						Bitmap bitmapCache = lruCache.get(imageUrl);
						String fileName = imageUrl.substring(imageUrl.lastIndexOf(File.separator) + 1);
						if(bitmapCache == null){
							dowingUrl.remove(imageUrl);
							dowingUrl.remove(imageUrl);
							handler.post(new Runnable() {
								public void run() {
									callback.imageLoaded(null, view, position);
								}
							});
							return;
						}
						String imageFile = Utils.saveImageCache(bitmapCache, fileName);
						synchronized (imageCacheDao) {
							imageCacheDao.saveImage(imageUrl, imageFile);
							if(imageCacheDao.isMaxCount(1000)){
								long minId = imageCacheDao.getMinId();
								String oldImageFile = imageCacheDao.getImageFileById(minId);
								File file = new File(oldImageFile);
								if(file.exists()){
									file.delete();
								}
								imageCacheDao.deleteImageById(minId);
							}
						}
						handler.post(new Runnable() {
							
							public void run() {
								Bitmap bitmap = lruCache.get(imageUrl);
								if(bitmap != null && !bitmap.isRecycled()){
									BitmapDrawable drawable = new BitmapDrawable(bitmap);
									bitmap = null;
									callback.imageLoaded(drawable, view, position);
									
								}
							}
						});
						dowingUrl.remove(imageUrl);
					}
				});	
				
			}
			
			return null;
		}
		
		
		public Drawable loadLocalDrawable(final String filePath, final View view, final ImageCallback callback, final ImageSize imageSize){
			if(filePath == null || filePath.equals("")){
				return null;
			}
			
			if(bitmapCache.containsKey(filePath)){
				WeakReference<Drawable> weakReference = bitmapCache.get(filePath);
				if(weakReference.get() != null){
					return weakReference.get();
				}
			}
			
			executorService.execute(new Runnable() {

				public void run() {
					Bitmap bitmap = null;
					if(imageSize == null){
						bitmap= BitmapFactory.decodeFile(filePath);
					}else{
						bitmap = Utils.decodeSampledBitmapFromResource(filePath, imageSize.getWith(), imageSize.getHeight());
					}
					
					final Drawable drawable = new BitmapDrawable(bitmap);
					bitmapCache.put(filePath, new WeakReference<Drawable>(drawable));
					handler.post(new Runnable() {
						
						
						public void run() {
							callback.imageLoaded(drawable, view);
						}
					});
				}
			});
			
			return null;
		}
		
	
		
		/**
		 * 
		 * @param imageUrl
		 *            图像url地址
		 * @param callback
		 *            回调接口
		 * @return 返回内存中缓存的图像，第一次加载返回null
		 */
		public Drawable loadDrawable(final String imageUrl,
				 final View view, final ImageCallback callback) {
			if(imageUrl == null || imageUrl.equals("")){
				return null;
			}
			// 如果缓存过就从缓存中取出数据
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache.get(imageUrl);
				if (softReference.get() != null) {
					return softReference.get();
				}
			}
			// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable() {
				public void run() {
					try {
						final Drawable drawable = loadImageFromUrl(imageUrl); 
							
						imageCache.put(imageUrl, new SoftReference<Drawable>(
								drawable));
						handler.post(new Runnable() {
							public void run() {
								callback.imageLoaded( drawable, view);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		/**
		 * 
		 * @param imageUrl
		 *            图像url地址
		 * @param callback
		 *            回调接口
		 * @return 返回内存中缓存的图像，第一次加载返回null
		 */
		public Drawable loadDrawable(final String imageUrl,
				 final View view,final int position, final ImageListCallback callback) {
			if(imageUrl == null || imageUrl.equals("")){
				return null;
			}
			// 如果缓存过就从缓存中取出数据
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache.get(imageUrl);
				if (softReference.get() != null) {
					return softReference.get();
				}
			}
			// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable() {
				public void run() {
					try {
						
						imageCache.put(imageUrl, new SoftReference<Drawable>(
								loadImageFromUrl(imageUrl)));
						handler.post(new Runnable() {
							public void run() {
								callback.imageLoaded( imageCache.get(imageUrl).get(), view, position);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		/**
		 * 
		 * @param appinfo
		 *            应用的信息
		 * @param callback
		 *            回调接口
		 * @return 返回内存中缓存的图像，第一次加载返回null
		 */
		public Drawable loadDrawable(final ApplicationInfo appinfo,
				 final View view, final ImageCallback callback) {
			if(appinfo == null){
				return null;
			}
			// 如果缓存过就从缓存中取出数据
			if (imageCache.containsKey(appinfo.packageName)) {
				SoftReference<Drawable> softReference = imageCache.get(appinfo.packageName);
				if (softReference.get() != null) {
					return softReference.get();
				}
			}
			// 缓存中没有图像，则从apk上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable() {
				public void run() {
					try {
						final Drawable drawable = appinfo.loadIcon(ZXTD_Application.getMyContext().getPackageManager()); 
							
						imageCache.put(appinfo.packageName, new SoftReference<Drawable>(
								drawable));

						handler.post(new Runnable() {
							public void run() {
								callback.imageLoaded( drawable, view);
							}
						});
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			return null;
		}
		
		
		
		// 从网络上取数据方法
		protected Drawable loadImageFromUrl(String imageUrl) {
			try {
				return BitmapDrawable.createFromStream(new URL(imageUrl).openStream(),
						null);

			} catch (Exception e) {
			}
			return null;
		}
		
		private Bitmap loadBitmapFromUrl(String imageUrl){
			try {
				//return Utils.decodeSampledBitmapFromUrl(imageUrl, 120, 100);
				InputStream is = new URL(imageUrl).openStream();
				if(is != null){
					return BitmapFactory.decodeStream(is);
				}
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				
			}
			return null;
		}
		

		// 对外界开放的回调接口
		public interface ImageCallback {
			// 注意 此方法是用来设置目标对象的图像资源
			public void imageLoaded(Drawable imageDrawable, View view);
		}
		
		// 对外界开放的回调接口
		public interface ImageListCallback {
					// 注意 此方法是用来设置目标对象的图像资源
					public void imageLoaded(Drawable imageDrawable, View view, int position);
				}
}
