package com.zxtd.information.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.bean.RegisterData;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageCallback;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.manager.UserNickManager;
import com.zxtd.information.service.CoverService;
import com.zxtd.information.service.PushService;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.pub.PublishActivity;
import com.zxtd.information.util.HanziToPinyin.Token;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
public class Utils {
	private static DisplayMetrics dm = null;
	private static List<NewTypeBean> mNewTypeBeans = null;
	public static String UUID = "00000000-0000-0000-0000-000000000000";
	public static boolean isPush = true;
	public static boolean isGoToNetFriend = false;
	public static int barHeight = 0;
	public static boolean isHasCoverImage = true;
	public static boolean isIntercept = true;
	
	public static boolean isImageMode = true;
	public static boolean isDataChange = false;
	
	public static boolean isInit = false;
	
	public static int fontSize = 2;
	
	public static Set<String> newClickedItem = new HashSet<String>();
	public static Set<String> netFriendClickedItem = new HashSet<String>();
	public static Set<String> hasSupportPost = new HashSet<String>();
	public static Set<String> collectSets = null;

	public static void initDisplayMetrics(Activity activity) {
		if (dm == null) {
			dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		}
	}

	public static int pxToDip(int size){
		return (int) (size / dm.density + 0.5f);  
	}
	
	public static int dipToPx(int size){
		return (int) (size * dm.density + 0.5f);
	}
	
	@SuppressLint("CommitPrefEdits")
	public static void save(Context context, Map<String, String> map) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			editor.putString(key, map.get(key));
		}
		editor.commit();
	}
	
	public static void save(Context context, String key, String value){
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putString(key, value);
		editor.commit();
	}
	
	public static void save(Context context, String key, boolean value){
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static String load(Context context, String key){
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		String value = preferences.getString(key, "");
		return value;
	}
	
	public static boolean load(Context context, String key, boolean defValue){
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		boolean value = preferences.getBoolean(key, defValue);
		return value;
	}

	public static String[] load(Context context, String[] keys) {
		int size = keys.length;
		SharedPreferences preferences = context.getSharedPreferences(
				Constant.DataKey.FILE_NAME, Context.MODE_PRIVATE);
		String[] values = new String[size];
		for (int i = 0; i < size; i++) {
			values[i] = preferences.getString(keys[i], "");
		}
		return values;
	}
	
	public static String getCss(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(" #title_section #new_sub_title {");
		sb.append(" line-height:" + dipToPx(20) + "px;");
		sb.append(" color: #666666;");
		sb.append(" text-align: center;");
		sb.append(" font-size: " + dipToPx(13)+"px;");
		sb.append("}");
		sb.append(" #title_section #new_title {");
		sb.append(" font-size: " + dipToPx(19) + "px;");
		sb.append(" line-height: " + dipToPx(23) + "px;");
		sb.append(" font-weight: bold;");
		sb.append(" text-align: center;");
		sb.append("}");
		sb.append(" #new_body {");
		sb.append(" width:100%;");
		sb.append(" font-size: " + dipToPx(16) + "px;");
		sb.append(" line-height: "+dipToPx(23) +"px;");
		sb.append(" letter-spacing: 0.1em;");
		sb.append("}");
		sb.append(" a {	text-decoration: none;}");
		sb.append(" img {max-width: 90%;  display:block;  margin:0 auto;}");
		
		return sb.toString();
	}
	
	public static String getHtml(PublishBean publishBean) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head>");
		buffer.append("<meta name=\"viewport\" content=\"target-densitydpi=device-dpi, width=device-width, initial-scale=1.0\" />");
		buffer.append("<style type='text/css'>");
		buffer.append(getCss());
		buffer.append("</style>");
		buffer.append("</head><body><div id=\"title_section\"><div id=\"new_title\">");
		buffer.append(publishBean.title);
		buffer.append("</div><div id=\"new_sub_title\">");
		buffer.append("作者：" + UserNickManager.getNewInstance().getNickName() + "   类型：" + publishBean.type.name);
		buffer.append("</div></div><hr /><div id=\"new_body\">");
		buffer.append(publishBean.content.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				+ "</div></body></html>");
		return buffer.toString();
	}
	
	public static DisplayMetrics getDisplayMetrics() {
		return dm;
	}

	public static void initNewTypeBeans(List<NewTypeBean> newTypeBeans) {
		mNewTypeBeans = newTypeBeans;
	}

	public static List<NewTypeBean> getNewTypeBeans() {
		if (mNewTypeBeans == null) {
			mNewTypeBeans = new ArrayList<NewTypeBean>();
		}
		return mNewTypeBeans;
	}

	public static boolean isEmpty(String string) {
		if (string == null || string.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			int inSampleSizeWith = Math.round((float) width / (float) reqWidth);
			int inSampleSizeHeight = Math.round((float) height
					/ (float) reqHeight);
			inSampleSize = Math.max(inSampleSizeWith, inSampleSizeHeight);
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String imagePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imagePath, options);
	}
	
	public static Bitmap decodeSampledBitmapFromUrl(String imageUrl,
			int reqWidth, int reqHeight) throws IOException {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(new URL(imageUrl).openStream(), null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(new URL(imageUrl).openStream(), null, options);
	}

	public static String SpannedToString(Spanned spanned){
		String text = Html.toHtml(spanned);
		if(isEmpty(text)){
			return "";
		}else{
			StringBuffer out = new StringBuffer();
			unicodeToString(text, out);
			return out.toString();
		}
	}
	
	private static void unicodeToString(String text, StringBuffer out){
		Pattern pattern = Pattern.compile("\\&\\#(\\d+)\\;");
		Matcher matcher = pattern.matcher(text);
		int len = text.length();
		int start = 0;
		while(matcher.find()){
			int first = matcher.start();
			
			if(first > start){
				start = add(text, out, start, first);
			}
			
			char c = (char)Integer.parseInt(matcher.group(1));
			out.append(c);
			start = matcher.end();
		}
		if(start != len){
			start = add(text, out, start, len);
		}
	}
	
	private static int add(String text, StringBuffer out, int start, int end){
		String subString = text.substring(start, end);
		out.append(subString);
		return end;
	}
	
	public static int getHotNewTypeIndex(List<Bean> newTypeBeans){
		for (int i = 0; i < newTypeBeans.size(); i++) {
			NewTypeBean newTypeBean = (NewTypeBean)newTypeBeans.get(i);
			if(Constant.Flag.HOT_TYPE.equals(newTypeBean.flag)){
				return i;
			}
		}
		return -1;
	}
	
	
	public static String saveImageCache(Bitmap cache,String fileName){
		if(!fileName.endsWith(".jpg")){
			int index = fileName.lastIndexOf(".");
			if(index != -1){
				fileName = fileName.substring(0, index) + ".jpg";
			}else{
				fileName = "img_" + System.currentTimeMillis() + ".jpg";
			}
		}
		File file = new File(Constant.IMAGE_CACHE_FILE, fileName);
		if(cache != null){
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				cache.compress(CompressFormat.JPEG, 100, outputStream);
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file.getPath();
	}
	
	public static void changePushState(Context context){
		if(isPush && isNetworkConn()){
			startPush(context);
		}else{
			stopPush(context);
		}
	}
	
	private static void startPush(Context context){
		Intent intent = new Intent(context, PushService.class);
		context.startService(intent);
	}
	
	private static void stopPush(Context context){
		Intent intent = new Intent(context, PushService.class);
		context.stopService(intent);
	}
	
	public static void updateCoverData(Context context){
		if(isNetworkConn()){
			startCoverService(context);
		}else{
			stopCoverService(context);
		}
	}
	
	private static void startCoverService(Context context){
		Intent intent = new Intent(context, CoverService.class);
		context.startService(intent);
	}
	
	private static void stopCoverService(Context context){
		Intent intent = new Intent(context, CoverService.class);
		context.stopService(intent);
	}
	
	public static boolean isNetworkConn(){
		ConnectivityManager connectivityManager = (ConnectivityManager) ZXTD_Application.getMyContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean activeNetState = false;
		if (activeNetInfo != null) {
			if (activeNetInfo.getState() == State.CONNECTED) {
				activeNetState = true;
			}
		}
		boolean mobNetState = false;
		if (mobNetInfo != null) {
			if (mobNetInfo.getState() == State.CONNECTED) {
				mobNetState = true;
			}
		}
		return activeNetState||mobNetState;
	}
	
	public static Map<String, String> getUrlParam(String url){
		Map<String, String> params = new HashMap<String, String>();
		Pattern pattern = Pattern.compile("\\&?(\\w+)=(\\w+)\\&?");
		Matcher matcher = pattern.matcher(url);
		while(matcher.find()){
			String key = matcher.group(1);
			String value = matcher.group(2);
			params.put(key, value);
			
			Log.i("url--param", "key:" + key + "  value:" + value);
		}
		
		return params;
	}
	
	public static void jumpToNetFriendSubList(Context context,NewTypeBean newTypeBean){
//		Intent intent  = new Intent(context, NetFriendSubListActivity.class);
//		intent.putExtra(Constant.BundleKey.NET_FRIEND_TYPE, newTypeBean.id);
//		intent.putExtra(Constant.BundleKey.NET_FRIEND_TYPE_NAME, newTypeBean.name);
//		context.startActivity(intent);
	}
	
	public static void MainToast(Handler handler,final Context context,final String text,final int duration){
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(context, text, duration).show();
			}
		});
	}
	
	/**
	 * 设置网络图片
	 * */
	public static void setUrlImage(zxtd_ImageCacheDao imageCacheDao,zxtd_AsyncImageLoader asyncImageLoader,String url, ImageView imageView, int position){
		//Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = asyncImageLoader.loadCacheDrawable(url, imageView, position,new ImageListCallback() {
					public void imageLoaded(Drawable imageDrawable, View v, int position) {
						if (imageDrawable != null) {
							((ImageView)v).setImageDrawable(imageDrawable);
							imageDrawable = null;
						} else {
							((ImageView)v).setImageResource(R.drawable.net_friend_logo);
						}
					}
		}, imageCacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.net_friend_logo);
		}
	}
	
	public static void setUrlImage(zxtd_AsyncImageLoader asyncImageLoader,String url, ImageView imageView){
		Drawable drawable = asyncImageLoader.loadDrawable(url, imageView, new ImageCallback() {
			
			public void imageLoaded(Drawable imageDrawable, View view) {
				if(imageDrawable != null){
					((ImageView) view).setImageDrawable(imageDrawable);
				}
			}
		});
		if(drawable != null){
			imageView.setImageDrawable(drawable);
		}
	}
	
	public static void copy(Context context,String content){
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}
	
	public static void jumpToWlanSetting(Context context){
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		context.startActivity(intent);
	}
	/**
	 * 获取注册信息
	 * */
	public static RegisterData getRegisterData(Context context) {
		RegisterData registerData = new RegisterData();
		registerData.ct = "1";
		registerData.si = Constant.Information.ZXTD_SI;
		registerData.di = Constant.Information.ZXTD_DI;
		registerData.phonetype = Build.MODEL;
		registerData.pt = "0";
		registerData.version = ZXTD_Application.versionName;

		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		registerData.number = telephonyManager.getLine1Number();
		registerData.imsi = telephonyManager.getSubscriberId();
		if(!isEmpty(registerData.imsi)){
			registerData.mnc = registerData.imsi.substring(3, 5);
		}else{
			Log.e(context.getClass().getName(), "该手机没有插入sim卡");
		}
		registerData.sc = "0000";
		return registerData;
	}
	
	public static void getBarHeight(Context mContext){
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
		    c = Class.forName("com.android.internal.R$dimen");
		    obj = c.newInstance();
		    field = c.getField("status_bar_height");
		    x = Integer.parseInt(field.get(obj).toString());
		    barHeight = mContext.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
		    e1.printStackTrace();
		} 
	}
	
	public static String transformContent(String text){
		if(!isEmpty(text)){
			return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		return "";
	}
	
	public static String reverseTransformContent(String text){
		if(!isEmpty(text)){
			return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		}
		return "";
	}
	
	public static boolean isHasImage(List<String> images){
		if(images.size() > 0){
			return true;
		}
		return false;
	}
	/*** 半角转换为全角 
	 *
	 * @param input    
	 * @return      
	*/      
	public static String ToDBC(String input) {
		if (isEmpty(input)) {
			return "";
		}
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	public static void printException(Exception ex){
		  StringWriter sw = new StringWriter();
	      PrintWriter pw = new PrintWriter(sw, true);
	      ex.printStackTrace(pw);
	      pw.flush();
	      sw.flush();
	      Log.e(Constant.TAG, "sssssssssssssssssssssss: "+sw.toString());
	}
	
	public static void jumpPubNews(Context context){
		Intent intent = new Intent(context, PublishActivity.class);
		context.startActivity(intent);
	}
	
	public static void saveImageCache(String url, Bitmap bitmap, zxtd_ImageCacheDao imageCacheDao){
		String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
		String imageFile = Utils.saveImageCache(bitmap, fileName);
		synchronized (imageCacheDao) {
			imageCacheDao.saveImage(url, imageFile);
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
	}
	
	public static String getPinYin(String input) { 
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);   
        StringBuilder sb = new StringBuilder();   
        if (tokens != null && tokens.size() > 0) {   
            for (Token token : tokens) {   
                if (Token.PINYIN == token.type) {   
                    sb.append(token.target);   
                } else {   
                    sb.append(token.source);   
               }   
            }   
        }   
        return sb.toString().toLowerCase();   
    }
	
	public static boolean hasCollent(String newsId, int type){
		if(collectSets != null){
			return collectSets.contains(newsId + "&" + type);
		}
		return false;
	}
	
	public static void addCollent(String newsId, int type){
		if(collectSets == null){
			collectSets = new HashSet<String>();
		}
		collectSets.add(newsId + "&" + type);
	}
	
	public static void delCollent(String newsId, int type){
		if(collectSets != null){
			collectSets.remove(newsId + "&" + type);
		}
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 80, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
