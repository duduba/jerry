package com.zxtd.information.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.PushNewsBean;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.PushNewsParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PushService extends Service {
	private Handler handler = new Handler();
	private TimerReceiver timerReceiver;
	private AlarmManager alarmManager;
	private int isSecondNetWork = 0;
	private Timer mtimer;
	
	public static final String TIMER_UPDATE_ACTION = "timer_update_action";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		timerReceiver = new TimerReceiver();
		this.registerReceiver(timerReceiver, new IntentFilter(TIMER_UPDATE_ACTION));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startPush();
		return START_REDELIVER_INTENT;
	}
	
	private void startPush(){
		Log.i("com.zxtd.information.service.PushService", "---startPush(),alarmManager="+alarmManager);
		if(alarmManager == null){
			Intent intent = new Intent(TIMER_UPDATE_ACTION);
			PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
			alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3600000, operation);
		}
	}
	
	private void stopPush(){
		if(alarmManager != null){
			Intent intent = new Intent(TIMER_UPDATE_ACTION);
			PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
			alarmManager.cancel(operation);
		}
	}
	
	private void sendMessage() {
		handler.post(new Runnable() {
			public void run() {
				String pushId = Utils.load(PushService.this, "pushId");
				Log.i("com.zxtd.information.service.PushService", "------pushId = "+ pushId + " ---------");
				RequestManager.newInstance().pushNewsComm(pushId, new RequestCallBack() {
					public void requestSuccess(String requestCode, Result result) {
						if(result == null){
							return;
						}
						PushNewsBean pushNewsBean = (PushNewsBean)result.getBean(PushNewsParseData.PUBSH_NEW_BEAN_KEY);
						if(pushNewsBean != null) {
							// 显示推送消息到通知栏
							showNotification(pushNewsBean);
						} else {
							Log.i("com.zxtd.information.service.PushService", "-----无推送数据----");
						}
					}
					
					public void requestError(String requestCode, int errorCode) {
						//异常处理
						Log.e("com.zxtd.information.service.PushService", "---------------"+requestCode+"请求出错---------");
						if(Utils.isNetworkConn()){
							// 有网络的情况下
							if(isSecondNetWork == 2){
								isSecondNetWork = 0;
								return;
							}else{
								isSecondNetWork = isSecondNetWork + 1;
								sendMessage();
							}
						}else{
							//无网络的情况下
							if(mtimer == null){
								mtimer = new Timer();
								mtimer.schedule(new TimerTask() {
									@Override
									public void run() {
										mtimer.cancel();
										if(Utils.isNetworkConn()){
											sendMessage();
										}else{
											return;
										}
									}
								}, 300000);
							}
						}
					}
				});
			}
		});
	}
	
	private void showNotification(PushNewsBean pushBean) {
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 
		Notification notification = new Notification(R.drawable.ic_news, pushBean.newsTitle, System.currentTimeMillis());
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent intent = new Intent(this, NewInfoActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		NewBean newBean = new NewBean();
		newBean.newId = pushBean.newsId;
		newBean.infoUrl = pushBean.httpUrl;
		newBean.postCount = pushBean.postCount;
		newBean.flag = pushBean.flag;
		newBean.newsTpye = pushBean.newsType;
		
		intent.putExtra(Constant.BundleKey.FLAG, "1".equals(newBean.flag) ? Constant.Flag.NORMAL_TYPE : Constant.Flag.NET_FRIEND_TYPE);
		intent.putExtra(Constant.BundleKey.NEWBEAN, newBean);
		intent.putExtra("isPushNew", true);
		
		PendingIntent contentIntent = PendingIntent.getActivity(
		        this, 
		        R.string.app_name, 
		        intent, 
		        PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.setLatestEventInfo(
				this,
				"杂色",
		        pushBean.newsTitle, 
		        contentIntent);
		nm.notify(R.string.app_name, notification);
		
		Utils.save(this, "pushId", pushBean.newsId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopPush();
		this.unregisterReceiver(timerReceiver);
	}
	
	private class TimerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Utils.isNetworkConn()){
				sendMessage();
			}
		}
	}

}
