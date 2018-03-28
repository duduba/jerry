package com.zxtd.information.view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zxtd.information.adapter.NewListViewPagerAdapter;
import com.zxtd.information.adapter.NewListViewPagerAdapter.HeaderView;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.listener.OnNewListItemListener;
import com.zxtd.information.parse.ChangeDifferentNewsParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public abstract class ListViewCreater implements OnRefreshListener2<ListView>{
	protected NewTypeBean mNewTypeBean;
	protected List<Bean> mNewListBean;
	private BaseAdapter mAdapter;
	protected NewListViewPagerAdapter  mParentAdapter;
	protected ListView mNewListView;
	protected PullToRefreshView refreshView;
	private View mView;
	protected Context mContext;
	public static final int INIT = 0;
	public static final int LOAD = 1;
	public static final int REFRESH = 2;
	
	protected zxtd_AsyncImageLoader asyncImageLoader = new zxtd_AsyncImageLoader();
	
	public ListViewCreater(NewTypeBean newTypeBean, List<Bean> newListBean, NewListViewPagerAdapter parentAdapter){
		mNewListBean = newListBean;
		mNewTypeBean = newTypeBean;
		mParentAdapter = parentAdapter;
	}
	
	protected abstract void doFirstRequest(String flag);
	protected abstract BaseAdapter createAdapter();
	protected abstract void doLoad();
	protected abstract void doRefresh();
	
	public View createView(LayoutInflater inflater){
		if(mView != null){
			return mView;
		}
		View view = inflater.inflate(R.layout.refresh_load_list_view, null);
		mContext = inflater.getContext();
		
		refreshView = (PullToRefreshView) view.findViewById(R.id.new_pull_to_refresh);
		mNewListView = refreshView.getRefreshableView();
		refreshView.canLoading(true);
		if(Constant.Flag.HOT_TYPE.equals(mNewTypeBean.flag) && Utils.isImageMode){
			View headerView = mParentAdapter.createHotHeaderView();
			mNewListView.addHeaderView(headerView);
			startTimer(headerView);
		}
		if(mNewListBean.size() == 0){
			doFirstRequest(mNewTypeBean.flag);
		}
		mAdapter = createAdapter();
		mNewListView.setOnItemClickListener(new OnNewListItemListener(mContext, mAdapter));
		mNewListView.setAdapter(mAdapter);
		refreshView.setOnRefreshListener2(this);
		mView = view;
		return view;
	}
	
	private void startTimer(View headerView){
		final HeaderView hoder = (HeaderView)headerView.getTag();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				hoder.newHeader.post(new Runnable() {
					@Override
					public void run() {
						int curItem = hoder.newHeader.getCurrentItem();
						int count = hoder.newHeader.getAdapter().getCount();
						hoder.newHeader.setCurrentItem((curItem + 1) % count, true);
						System.out.println("curItem:" + curItem);
					}
				});
			}
		}, 0, 5000);
	}
	
	public BaseAdapter getAdapter(){
		return mAdapter;
	}
	
	public void updataAdapter(){
		if(mNewListView != null){
			mNewListView.setAdapter(mAdapter);
		}
	}
	
	protected void getResult(Result result, List<Bean> newBeans, int state){
		boolean isHasData = result.getBoolean(ChangeDifferentNewsParseData.IS_DATA_OVER_KEY);
		if(state == REFRESH){
			mNewListBean.clear();
			refreshView.onRefreshComplete2();
		}else if(state == LOAD){
			refreshView.onRefreshComplete2();
		}else if(state == INIT){
			mNewListBean.clear();
		}
		if(isHasData){
			refreshView.canLoading(false);
		}
		mNewListBean.addAll(newBeans);
		getAdapter().notifyDataSetChanged();
	}
	
	public static String getNoImage(){
		if(Utils.isImageMode){
			return "1";
		}else{
			return "0";
		}
	}
	
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		doRefresh();
	}

	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		doLoad();
	}
}
