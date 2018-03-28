package com.zxtd.information.view;

import com.zxtd.information.util.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NewViewPager extends ViewPager {
	private int mLastMotionX = 0;
	private int mLastMotionY = 0;
	public NewViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NewViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		Utils.isIntercept = false;
		int action = arg0.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = (int) arg0.getX();
			mLastMotionY = (int) arg0.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int)arg0.getX() - mLastMotionX;
			int deltaY = (int)arg0.getY() - mLastMotionY;
			if(isFirstOrLast(deltaX) || isPullDown(deltaX, deltaY)){
				Utils.isIntercept = true;
			}
			break;
		}
		return super.onInterceptTouchEvent(arg0);
	}

	private boolean isFirstOrLast(int deltaX){
		if(deltaX > 10 && getCurrentItem() == 0){
			return true;
		}else if(deltaX < -10 && getCurrentItem() == this.getAdapter().getCount() - 1){
			return true;
		}
		return false;
	}
	
	private boolean isPullDown(int deltaX, int deltaY){
		if(Math.abs(deltaX) < Math.abs(deltaY)){
			return true;
		}
		return false;
	}
	
}
