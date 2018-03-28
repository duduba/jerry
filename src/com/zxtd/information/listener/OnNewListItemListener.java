package com.zxtd.information.listener;


import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.manager.CurrentManager;
import com.zxtd.information.ui.news.NewActivity;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class OnNewListItemListener implements OnItemClickListener {
	private Context mContext;
	private BaseAdapter mAdapter;
	public OnNewListItemListener(Context context,BaseAdapter adapter){
		mContext = context;
		mAdapter = adapter;
	}
	
	public void setAdapter(BaseAdapter adapter){
		mAdapter = adapter;
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Object item = arg0.getAdapter().getItem(arg2);
		
		if(item instanceof NewBean){
			Utils.newClickedItem.add(((NewBean) item).newId);
			Intent intent = new Intent(mContext, NewInfoActivity.class);
			CurrentManager.newInstance().setCurrentBean((Bean)item);
			intent.putExtra(Constant.BundleKey.FLAG, ((NewActivity) mContext).getCurrentFlag());
			mContext.startActivity(intent);
			mAdapter.notifyDataSetChanged();
		}else if(item instanceof DownloadNewsListBean){
			Intent intent = new Intent(mContext, NewInfoActivity.class);
			NewBean newBean = new NewBean();
			DownloadNewsListBean downloadNewsListBean = (DownloadNewsListBean)item;
			newBean.newTitle = downloadNewsListBean.title;
			newBean.newId = downloadNewsListBean.id;
			newBean.postCount = downloadNewsListBean.postcount;
			newBean.infoUrl = downloadNewsListBean.infoUrl;
			CurrentManager.newInstance().setCurrentBean((Bean)newBean);
			Utils.netFriendClickedItem.add(newBean.newId);
			intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.NET_FRIEND_TYPE);
			mContext.startActivity(intent);
			mAdapter.notifyDataSetChanged();
		}
		
	}

//	private void addClickCounts(int index) {
//		switch (index) {
//		case 1:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item1");
//			break;
//		case 2:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item2");
//			break;
//		case 3:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item3");
//			break;
//		case 4:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item4");
//			break;
//		case 5:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item5");
//			break;
//		case 6:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item6");
//			break;
//		case 7:
//			MobclickAgent.onEvent(mContext, "netfriend_list_item7");
//			break;
//		default:
//			break;
//		}
//	}
}
