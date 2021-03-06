package com.zxtd.information.ui.me;

import java.util.List;

import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.manager.FansFocusManager;
import com.zxtd.information.util.Constant;

import android.content.Context;

public class FocusLayout extends MyRelativeLayout {

	private int userId;
	public FocusLayout(Context context,int userId,String from) {
		super(context);
		this.userId=userId;
		if(from.equals("self")){
			this.setSelf(true);
		}
		this.setCallBack(callBack);
		this.setFrom(1);
		super.initData();
	}
	
	
	CallBack callBack=new CallBack(){
		public List<FansFocusBean> loadData(int pageIndex) {
			// TODO Auto-generated method stub
			FansFocusManager manager=new FansFocusManager();
			return manager.getFocus(Constant.RequestCode.MINE_REQUEST_FOCUS, userId, pageIndex);
		}
	};

}
