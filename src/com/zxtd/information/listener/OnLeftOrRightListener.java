package com.zxtd.information.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

public abstract class OnLeftOrRightListener implements OnTouchListener {
	private View view;
	private GestureDetector mGestureDetector;
	public OnLeftOrRightListener(){
		mGestureDetector = new GestureDetector(mGestureListener);
	}
	
	public enum EventType{
		LEFT,
		RIGHT
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		view = arg0;
		return mGestureDetector.onTouchEvent(arg1);
	}

	private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener(){
		
		private boolean isFling = true;
		private float mDistanceY = 0.0f;
		
		

		@Override
		public boolean onDown(MotionEvent e) {
			isFling = true;
			mDistanceY = 0.0f;
//			System.out.println("onDown");
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			mDistanceY = distanceY + mDistanceY;
//			System.out.println("distanceX --> " + distanceX + "  distanceY --> " + distanceY);
			if(Math.abs(mDistanceY) > 80){
				isFling = false;
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(!isFling){
				return false;
			}
			float x = Math.abs(e2.getX() - e1.getX());
			float y = Math.abs(e2.getY() - e1.getY());
			if(velocityX > 0 && x > 120 && y < 80){
				return onLeftOrRight( view, EventType.RIGHT);
			}else if(velocityX < 0 && x > 120 && y < 80){
				
				return onLeftOrRight( view, EventType.LEFT);
			}
//			System.out.println("onFling");
			return false;
		}
		
	};
	
	
	public abstract boolean onLeftOrRight(View view,EventType eventType);
}
