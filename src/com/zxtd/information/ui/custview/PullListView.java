package com.zxtd.information.ui.custview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.Scroller;

public class PullListView extends ListView {

	private Context mContext;
	private Scroller mScroller;
	private int mLastMotionY = 0;
	
	public PullListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mScroller = new Scroller(mContext);
	}
	
	public PullListView(Context context) {
		super(context);
	}
	
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
	// TODO Auto-generated method stub
		int y = (int) event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			if (mScroller != null) {
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
			}
			mLastMotionY = y;
			break;
			case MotionEvent.ACTION_MOVE:
				int deltay = (int) (mLastMotionY - y);
				if(Math.abs(deltay)<20){
					return false;
				}
				mLastMotionY = y;
				if (isButtom(deltay)) {
					setVerticalScrollBarEnabled(false);
					scrollBy(0, deltay);
					return true;
				}
				if (isHeader(deltay)) {
					setVerticalScrollBarEnabled(false);
					scrollBy(0, deltay);
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				mLastMotionY = 0;
				if (getScrollY()!=0) {
					mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 1000);
					invalidate();
					handler.sendEmptyMessageDelayed(0, 500);
					return true;
				}
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}
	
	
	private boolean isHeader(int deltay) {
		int firstTop = getChildAt(0).getTop();
		int effectivePaddingTop = getListPaddingTop();
		int spaceAbove = effectivePaddingTop - firstTop;
		return getFirstVisiblePosition() == 0&&spaceAbove<=0&&deltay<0;
	}
	private boolean isButtom(int deltay) {
		int lastBottom = getChildAt(getChildCount() - 1).getBottom();
		int effectivePaddingBottom = getListPaddingBottom();
		return getLastVisiblePosition() == (getCount() - 1)&&lastBottom<=(getHeight()-effectivePaddingBottom)&&deltay>0;
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
				case 0:
						setVerticalScrollBarEnabled(true);
						invalidate();
				break;
				default:
					break;
			}
	};
	};
	
}
