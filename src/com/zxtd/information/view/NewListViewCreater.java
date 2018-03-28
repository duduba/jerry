package com.zxtd.information.view;

import java.util.List;

import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.zxtd.information.adapter.NewListAdapter;
import com.zxtd.information.adapter.NewListViewPagerAdapter;
import com.zxtd.information.adapter.NoIconNewListAdapter;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.ChangeDifferentNewsParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public class NewListViewCreater extends ListViewCreater{
	public NewListViewCreater(NewTypeBean newTypeBean, List<Bean> newListBean,
			NewListViewPagerAdapter parentAdapter) {
		super(newTypeBean, newListBean, parentAdapter);
	}
	
	private RequestCallBack callBack = new RequestCallBack() {
		public void requestSuccess(String requestCode, Result result) {
			int state = result.getInteger("state");
			List<Bean> beans = null;
			if(Constant.RequestCode.CHANGE_NEWS_INFO.equals(requestCode)){
				beans = result.getListBean(ChangeDifferentNewsParseData.NEW_BEAN_LIST_KEY);
			}else if (Constant.RequestCode.ZXTD_VIDEO_LIST.equals(requestCode)) {
				beans = result.getListBean(ChangeDifferentNewsParseData.NEW_BEAN_LIST_KEY);
				Log.e("zxtd_video", "beans.size:"+beans.size());
			}
			getResult(result, beans, state);
		}
		public void requestError(String requestCode, int errorCode) {
			refreshView.onRefreshComplete2();
			Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void doLoad() {
		int size = mNewListBean.size();
		if(size == 0){
			doRefresh();
		}else{
			NewTypeBean newTypeBean = (NewTypeBean)mNewTypeBean;
			Bean newBean = (Bean)mNewListBean.get(size - 1);
			if(newBean instanceof NewTypeBean){
				return;
			}
			if (Constant.Flag.VIDEO_TYPE.equals(newTypeBean.flag)) {
				RequestManager.newInstance().videoNewsListComm(newBean.toString(), newTypeBean.id, 0, "1", callBack, LOAD);
			}else {
				RequestManager.newInstance().changeDifferentNewsComm(newBean.toString(), newTypeBean.id, 0, getNoImage(), callBack, LOAD);
				//RequestManager.newInstance().changeDifferentNewsComm(newBean.operid, newTypeBean.id, 0, callBack, LOAD);
			}

		}
	}
	
	@Override
	protected void doRefresh() {
		if(mNewTypeBean == null){
			//doInit();
			return;
		}
		refreshView.canLoading(true);
		NewTypeBean newTypeBean = (NewTypeBean)mNewTypeBean;
		if(Constant.Flag.NET_FRIEND_TYPE.equals(newTypeBean.flag)){
			//RequestManager.newInstance().netFriendMenuListComm("", newTypeBean.id, menuListPosterId, callBack, REFRESH);
		}else if(Constant.Flag.VIDEO_TYPE.equals(newTypeBean.flag)) {
			RequestManager.newInstance().videoNewsListComm("", newTypeBean.id, 0, "1", callBack, REFRESH);
		}else{
			RequestManager.newInstance().changeDifferentNewsComm("", newTypeBean.id, 0, getNoImage(), callBack, REFRESH);
		}
	}
	@Override
	protected void doFirstRequest(String flag) {

		if(Constant.Flag.NET_FRIEND_TYPE.equals(flag)){
			
		}else if(Constant.Flag.HOT_TYPE.equals(flag) && Utils.isImageMode){
			RequestManager.newInstance().changeDifferentNewsComm("", mNewTypeBean.id, 0, "1", callBack, INIT);
		}else if (Constant.Flag.VIDEO_TYPE.equals(flag)) {
			RequestManager.newInstance().videoNewsListComm("", mNewTypeBean.id, 0, getNoImage(), callBack, INIT);
		}else{
			RequestManager.newInstance().changeDifferentNewsComm("", mNewTypeBean.id, 0,getNoImage(), callBack, INIT);
		}
	}
	@Override
	protected BaseAdapter createAdapter() {
		BaseAdapter baseAdapter = null;
		if(Utils.isImageMode){
			baseAdapter = new NewListAdapter(mContext, mNewListBean);
		}else{
			baseAdapter = new NoIconNewListAdapter(mContext, mNewListBean);
		}
		return baseAdapter;
	}
}
