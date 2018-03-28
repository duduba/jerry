package com.zxtd.information.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Gallery;
import android.widget.RelativeLayout;

public class NewGallery extends Gallery {
	private OnClickListener mOnClickListener;
	
	private long downTime = 0;
	private PointF downPoint = new PointF();
	
	public NewGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public NewGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NewGallery(Context context) {
		super(context);
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			downTime = AnimationUtils.currentAnimationTimeMillis();
			downPoint.x = event.getX();
			downPoint.y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(downPoint.x - event.getX()) > 10 || Math.abs(downPoint.y - event.getY()) > 10){
				downTime = 0;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(downTime != 0){
				long difTime = AnimationUtils.currentAnimationTimeMillis() - downTime;
				if(difTime < 500 && mOnClickListener != null){
					mOnClickListener.onClick(this);
				}
			}
			break;
		}
		if(this.getChildCount() == 1){
			RelativeLayout frameLayout = (RelativeLayout)this.getChildAt(0);
			ZoomImageView imageView = (ZoomImageView)frameLayout.getChildAt(2);
			if(imageView.ZoomImage(event)){
				event.setAction(MotionEvent.ACTION_DOWN);
				super.onTouchEvent(event);
				event.setAction(MotionEvent.ACTION_UP);
			}
			
		}
		
		return super.onTouchEvent(event);
	}
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) { 

        return e2.getX() > e1.getX(); 

    } 


    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, 

            float velocityY) { 

        int keyCode; 

        if (isScrollingLeft(e1, e2)) {       

            keyCode = KeyEvent.KEYCODE_DPAD_LEFT; 

        } else { 

            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT; 

        } 

        onKeyDown(keyCode, null); 

        return true; 

    } 
	
}
