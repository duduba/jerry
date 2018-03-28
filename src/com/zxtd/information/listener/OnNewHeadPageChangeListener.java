package com.zxtd.information.listener;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.view.PageSelectView;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

public class OnNewHeadPageChangeListener implements OnPageChangeListener {
	private TextView mHeadTitle;
	private List<Bean> mHeadBeans;
	private PageSelectView mSelectView;
	public OnNewHeadPageChangeListener(Context context, List<Bean> headBeans, TextView headTitle, PageSelectView selectView){
		mHeadBeans = headBeans;
		mHeadTitle = headTitle;
		mSelectView = selectView;
	}
	
	public void onPageScrollStateChanged(int arg0) {
		
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void onPageSelected(int arg0) {
		NewBean newBean = (NewBean)mHeadBeans.get(arg0);
		if(newBean != null){
			mHeadTitle.setText(newBean.newTitle);
		}
		mSelectView.setNumber(arg0 + 1);
	}

}
