package com.zxtd.information.view;

import com.zxtd.information.util.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NewListViewPager extends ViewPager {

	public NewListViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NewListViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(arg0.getAction() == MotionEvent.ACTION_DOWN){
			Utils.isIntercept = true;
		}
		if(Utils.isIntercept){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
		
	}

}
