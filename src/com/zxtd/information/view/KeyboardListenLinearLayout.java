package com.zxtd.information.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class KeyboardListenLinearLayout extends LinearLayout{
	
	public static final int SOFT_KEY_BOARD_HIDE = 0;
	public static final int SOFT_KEY_BOARD_VISIBLE = 1;
	
	private OnSoftKeyboardStateListener mKeyboardStateListener = null;
	
	private int mState = -1;
	
	public interface OnSoftKeyboardStateListener {
		void onStateChanged(int state);
	}

	public KeyboardListenLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}

	public KeyboardListenLinearLayout(Context context) {
		super(context);
	
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(oldh != 0 ){
			int state = h > oldh ? SOFT_KEY_BOARD_HIDE:SOFT_KEY_BOARD_VISIBLE;
			if(mState == state){
				return;
			}
			mState = state;
			keyboardStateChanged(state);
		}
	}
	
	private void keyboardStateChanged(int state){
		if(mKeyboardStateListener != null){
			mKeyboardStateListener.onStateChanged(state);
		}
	}
	
	public void setOnSoftKeyboardStateListener(OnSoftKeyboardStateListener keyboardStateListener){
		mKeyboardStateListener = keyboardStateListener;
	}
	
}
