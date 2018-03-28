package com.zxtd.information.listener;

import com.zxtd.information.bean.NewBean;
import com.zxtd.information.manager.CurrentManager;
import com.zxtd.information.ui.news.NewInfoActivity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnImageClickListener implements OnClickListener {
	private NewBean mNewBean;
	public OnImageClickListener(NewBean newBean){
		mNewBean = newBean;
	}
	public void onClick(View arg0) {
		Intent intent = new Intent(arg0.getContext(), NewInfoActivity.class);
		CurrentManager.newInstance().setCurrentBean(mNewBean);
		//intent.putExtra(Constant.BundleKey.FLAG, ((NewActivity)arg0.getContext()).getCurrentFlag());
		arg0.getContext().startActivity(intent);
	}

}
