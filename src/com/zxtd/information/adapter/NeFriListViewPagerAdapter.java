package com.zxtd.information.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.view.NetFriendListViewCreater;

public class NeFriListViewPagerAdapter extends PagerAdapter{
	private List<Bean> mNewTypeBeans;
	private Map<String, List<Bean>> newListMap;
	private LayoutInflater mInflater;
	private Map<String, NetFriendListViewCreater> netFriendListViewMap;
	public NeFriListViewPagerAdapter(Context context, List<Bean> newTypeBeans, Map<String, List<Bean>> newListMap){
		mNewTypeBeans = newTypeBeans;
		mInflater = LayoutInflater.from(context);
		this.newListMap = newListMap;
		netFriendListViewMap = new HashMap<String, NetFriendListViewCreater>();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		NetFriendListViewCreater creater = netFriendListViewMap.get(position + "");
		NewTypeBean newTypeBean = (NewTypeBean)mNewTypeBeans.get(position);
		List<Bean> newListBeans = newListMap.get(newTypeBean.id);
		View view = null;
		if(creater == null || newListBeans == null){
			
			if(newListBeans == null){
				newListBeans = new ArrayList<Bean>();
				newListMap.put(newTypeBean.id, newListBeans);
			}
			NetFriendListViewCreater viewCreater = new NetFriendListViewCreater( newTypeBean, newListBeans);
			view = viewCreater.createView(mInflater);
			netFriendListViewMap.put("" + position, viewCreater);
		}else{
			view = creater.createView(mInflater);
			creater.updataAdapter();
		}
		container.addView(view, 0);
		return view;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNewTypeBeans.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	public void refreshData(int position){
		NetFriendListViewCreater creater = netFriendListViewMap.get(position + "");
		if(creater != null){
			creater.refresh();
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View)object);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return ((NewTypeBean)mNewTypeBeans.get(position)).name;
	}
	
}
