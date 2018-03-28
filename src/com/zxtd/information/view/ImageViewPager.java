package com.zxtd.information.view;

import com.zxtd.information.gestureimage.GestureImageView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ImageViewPager extends ViewPager {
	private OnClickListener mOnClickListener;
	private GestureImageView curImageView;
	private boolean isPageScrolled = false;
	private OnPageChangeListener mOnPageChangeListener2;
	private OnTouchListener mOnTouchListener2;
	private GestureDetector mDetector;
	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageViewPager(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		super.setOnPageChangeListener(mOnPageChangeListener);
		super.setOnTouchListener(mOnTouchListener);
		mDetector = new GestureDetector(onGestureListener);
	}
	
	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener2 = listener;
	}
	
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		mOnTouchListener2 = l;
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		super.onLayout(arg0, arg1, arg2, arg3, arg4);
		if(curImageView == null){
			curImageView = getCurImageView(this.getCurrentItem());
		}
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
	
	private GestureImageView getCurImageView(int position){
		GestureImageView gestureImageView = (GestureImageView)ImageViewPager.this.findViewWithTag(position);
		return gestureImageView;
	}
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		private int curPos = 0;
		private int lastPos = 0;
		public void onPageSelected(int arg0) {
			curPos = arg0;
			if(mOnPageChangeListener2 != null){
				mOnPageChangeListener2.onPageSelected(arg0);
			}
			
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if(arg1 == 0.0f){
				isPageScrolled = false;
			}else{
				isPageScrolled = true;
			}
			if(mOnPageChangeListener2 != null){
				mOnPageChangeListener2.onPageScrolled(arg0, arg1, arg2);
			}
		}
		
		public void onPageScrollStateChanged(int arg0) {
			if(mOnPageChangeListener2 != null){
				mOnPageChangeListener2.onPageScrollStateChanged(arg0);
			}
			if(arg0 == 0 && curPos != lastPos){
				if(curImageView != null){
					curImageView.reset();
				}
				curImageView = getCurImageView(curPos);
				lastPos = curPos;
			}
		}
	};
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			mDetector.onTouchEvent(event);
			boolean isHandle = false;
			if(!isPageScrolled){
				isHandle = curImageView.setOnTouch(curImageView, event);
			}
			int action = event.getAction();
			if(!isHandle || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP){
				if(mOnTouchListener2 != null){
					mOnTouchListener2.onTouch(v, event);
				}
			}
			return isHandle;
		}
	};
	private SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener(){

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(mOnClickListener != null){
				mOnClickListener.onClick(curImageView);
			}
			return super.onSingleTapUp(e);
		}
		
	};
}
