package com.zxtd.information.view;

import com.zxtd.information.listener.OnLeftOrRightListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class PostListView extends ListView {

	private OnLeftOrRightListener mLeftOrRightListener;
	public PostListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public PostListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PostListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnLeftOrRightListener(OnLeftOrRightListener leftOrRightListener){
		mLeftOrRightListener = leftOrRightListener;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		mLeftOrRightListener.onTouch(this, ev);
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mLeftOrRightListener.onTouch(this, ev);
		return super.onTouchEvent(ev);
	}
}
