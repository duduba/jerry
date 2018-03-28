package com.zxtd.information.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zxtd.information.bean.CoverBean;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageCallback;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.CoverParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class CoverService extends Service {
	private TimerReceiver timerReceiver;
	private AlarmManager alarmManager;
	private Handler handler = new Handler();
	
	public static final String COVER_TIMER_ACTION = "cover_timer_action";
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		timerReceiver = new TimerReceiver();
		this.registerReceiver(timerReceiver, new IntentFilter(COVER_TIMER_ACTION));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startUpdateCoverData();
		return START_REDELIVER_INTENT;
	}
	
	private void startUpdateCoverData() {
		Log.i("com.zxtd.information.CoverService", "---startUpdateCoverData(),alarmManager="+alarmManager);
		if(alarmManager == null){
			Intent intent = new Intent(COVER_TIMER_ACTION);
			PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
			alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 7200000, operation);
		}
	}
	
	private void stopUpdateCoverData() {
		if(alarmManager != null){
			Intent intent = new Intent(COVER_TIMER_ACTION);
			PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
			alarmManager.cancel(operation);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void requestServer(){
		handler.post(new Runnable(){
			public void run() {
				// 读取数据库信息 ------>id
				String[] keys = {Constant.CoverDataKey.COVERID, Constant.CoverDataKey.SCREENWIDTH, 
						Constant.CoverDataKey.SCREENHEIGHT};
				String[] value = Utils.load(CoverService.this, keys);
				Log.i("com.zxtd.information.CoverService", "id="+value[0]+",width="+value[1]+",high="+value[2]);
				RequestManager.newInstance().CoverComm(value[0], value[1], value[2], new RequestCallBack() {
					public void requestSuccess(String requestCode, Result result) {
						if(result == null) {
							return;
						}
						CoverBean coverBean = (CoverBean)result.getBean(CoverParseData.COVER_BEAN_KEY);
						if(coverBean != null) {
							if(coverBean.time != null){
								String[] times = coverBean.time.substring(0, 10).split("-");
								int netImageTime = Integer.parseInt(times[0]+times[1]+times[2]);
								SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
								int sysTime = Integer.parseInt(format.format(new Date(System.currentTimeMillis())));
								if(netImageTime >= sysTime){
									Map<String, String> map = new HashMap<String, String>();
									map.put(Constant.CoverDataKey.COVERID, coverBean.id);
									map.put(Constant.CoverDataKey.TIME, coverBean.time);
									Utils.save(CoverService.this, map);
									
									zxtd_AsyncImageLoader imageLoader = new zxtd_AsyncImageLoader();
									String netImageUrl = coverBean.imageUrl;
									Log.i("com.zxtd.information.CoverService", "netImageUrl="+netImageUrl);
									imageLoader.loadDrawable(netImageUrl, null, new ImageCallback() {
										public void imageLoaded(Drawable imageDrawable, View view) {
											if(imageDrawable != null) {
												Log.i("com.zxtd.information.CoverService", "------图片下载完成------");
												BitmapDrawable bd = (BitmapDrawable)imageDrawable;
												Bitmap bitmap = bd.getBitmap();
												String filePath = Utils.saveImageCache(bitmap, Constant.CoverDataKey.COVERIMAGENAME);
												if(!Utils.isEmpty(filePath)) {
													Utils.save(CoverService.this, Constant.CoverDataKey.COVERIMAGEPATH, filePath);
													Log.i("com.zxtd.information.CoverService", "saveimagepath="+filePath);
												}
											}else {
												Log.i("com.zxtd.information.CoverService", "----图片下载不成功-----------");
											}
										}
									});
								}else{
									Log.i("com.zxtd.information.CoverService", "------------图片有效期小于当前时间----------------");
								}
							}
						}else{
							Log.i("com.zxtd.information.CoverService", "------无封面更新------");
						}
					}
					
					public void requestError(String requestCode, int errorCode) {
						Log.e("com.zxtd.information.CoverService", "错误请求码:" + requestCode);
						// 异常处理
						
					}
				});
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopUpdateCoverData();
		this.unregisterReceiver(timerReceiver);
	}
	
	private class TimerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Utils.isNetworkConn()){
				requestServer();
			}
		}
	}
}
