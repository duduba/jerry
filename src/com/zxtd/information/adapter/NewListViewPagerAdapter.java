package com.zxtd.information.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.listener.OnNewHeadPageChangeListener;
import com.zxtd.information.ui.R;
import com.zxtd.information.view.NewListViewCreater;
import com.zxtd.information.view.PageSelectView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewListViewPagerAdapter extends PagerAdapter {

	private List<Bean> mNewTypeBeans;
	private Map<String, List<Bean>> newListMap;
	private LayoutInflater mInflater;
	private View headerParent;
	
	private Map<String, NewListViewCreater> newListViewMap;
	
	public NewListViewPagerAdapter(Context context, List<Bean> newTypeBeans, Map<String, List<Bean>> newListMap){
		mNewTypeBeans = newTypeBeans;
		mInflater = LayoutInflater.from(context);
		this.newListMap = newListMap;
		newListViewMap = new HashMap<String, NewListViewCreater>();
	}
	
	public View createHotHeaderView(){
		if(headerParent != null){
			return headerParent;
		}
		headerParent = mInflater.inflate(R.layout.new_list_header, null);
		HeaderView newHeader = new HeaderView();
		newHeader.newHeadTitle = (TextView) headerParent.findViewById(R.id.new_head_title);
		newHeader.headButtomView = (LinearLayout) headerParent.findViewById(R.id.new_head_buttom);
		newHeader.pageSelectView = (PageSelectView) headerParent.findViewById(R.id.page_select);
		newHeader.pageSelectView.setIc(R.drawable.select, R.drawable.unselect);
		newHeader.newHeader = (ViewPager) headerParent.findViewById(R.id.new_header);
		
		headerParent.setTag(newHeader);
		initHeadViewData(newHeader, headerParent);
		return headerParent;
	}
	
	public void updateHotHeaderView(){
		if(headerParent == null){
			return;
		}
		HeaderView headerView = (HeaderView) headerParent.getTag();
		NewHeaderListAdapter headerListAdapter = new NewHeaderListAdapter(mInflater.getContext(), new ArrayList<Bean>());
		headerView.newHeader.setAdapter(headerListAdapter);
		initHeadViewData(headerView, headerParent);
	}
	
	private void initHeadViewData( HeaderView newHeader, View headerParent){
		List<Bean> newHotList = newListMap.get("newHotList");
		newHeader.newHeader.setOnPageChangeListener(new OnNewHeadPageChangeListener(mInflater.getContext(), newHotList, newHeader.newHeadTitle, newHeader.pageSelectView));
		if(newHotList == null){
			newHotList = new ArrayList<Bean>();
			newListMap.put("newHotList", newHotList);
		}
		NewHeaderListAdapter headerListAdapter = new NewHeaderListAdapter(mInflater.getContext(), newHotList);
		newHeader.newHeader.setAdapter(headerListAdapter);
		if(newHotList.size() != 0){
			newHeader.newHeadTitle.setText(((NewBean)newHotList.get(0)).newTitle);
		}
		newHeader.pageSelectView.setPages(newHotList.size());
		newHeader.pageSelectView.setNumber(1);
	}
	
	@Override
	public int getCount() {
		return mNewTypeBeans.size();
	}
	

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		NewListViewCreater creater = newListViewMap.get("" + position);
		NewTypeBean newTypeBean = (NewTypeBean)mNewTypeBeans.get(position);
		List<Bean> newListBeans = newListMap.get(newTypeBean.id);
		View view = null;
		if(creater == null || newListBeans == null){
			
			if(newListBeans == null){
				newListBeans = new ArrayList<Bean>();
				newListMap.put(newTypeBean.id, newListBeans);
			}
			NewListViewCreater viewCreater = new NewListViewCreater( newTypeBean, newListBeans,	this);
			view = viewCreater.createView(mInflater);
			newListViewMap.put("" + position, viewCreater);
		}else{
			view = creater.createView(mInflater);
			creater.updataAdapter();
		}
		container.addView(view, 0);
		return view;
	}
	
	
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
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
	
	public static class HeaderView{
		public TextView newHeadTitle;
		public LinearLayout headButtomView;
		public PageSelectView pageSelectView;
		public ViewPager newHeader;
	}
}
