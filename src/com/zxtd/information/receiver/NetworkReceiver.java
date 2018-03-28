package com.zxtd.information.receiver;

import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		isPush(arg0);
		Utils.changePushState(arg0);
		Utils.updateCoverData(arg0);
		Log.i(this.getClass().getName(), "------------NetworkReceiver-------------------");
	}
	
	private void isPush(Context context){
		String isPush = Utils.load(context, Constant.DataKey.ISPUSH);
		if(Utils.isEmpty(isPush)){
			Utils.isPush = true;
		}else{
			Utils.isPush =  Boolean.parseBoolean(isPush);
		}
	}
}
