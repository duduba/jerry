package com.zxtd.information.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

public class MobileData {
	private Context mContext;
	private ConnectivityManager mCM;
	public MobileData(Context context){
		this.mContext = context;
		getConnectivityManager();
	}
	
	private void getConnectivityManager(){
		mCM = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	//打开或关闭GPRS 
	public boolean gprsEnabled(boolean bEnable) {
		Object[] argObjects = null;

		boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled");
		System.out.println("***" + isOpen);
		if (isOpen == !bEnable) {
			setGprsEnabled("setMobileDataEnabled", bEnable);
		}

		return isOpen;
	}
     
    //检测GPRS是否打开 
	private boolean gprsIsOpenMethod(String methodName) {
		Class cmClass = mCM.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;

		Boolean isOpen = false;
		try {
			Method method = cmClass.getMethod(methodName, argClasses);

			isOpen = (Boolean) method.invoke(mCM, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isOpen;
	}
     
    //开启/关闭GPRS 
	private void setGprsEnabled(String methodName, boolean isEnable) {
		Class cmClass = mCM.getClass();
		Class[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		try {
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
