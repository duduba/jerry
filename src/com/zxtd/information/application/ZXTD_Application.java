package com.zxtd.information.application;

import com.zxtd.information.net.RequestComm;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

public class ZXTD_Application extends Application {
	private static Context context;
	public static String versionName = "";
	@Override
	public void onCreate() {
		super.onCreate();
		context = this.getApplicationContext();
		RequestComm.setHandler(new Handler());
		try {
			PackageInfo packageInfo = this.getApplicationContext().getPackageManager().getPackageInfo(this.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CrashHandler.getInstance().init(context);
	}
	public static Context getMyContext(){
		return context;
	}
}
