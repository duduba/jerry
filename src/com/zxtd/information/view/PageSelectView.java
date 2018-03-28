package com.zxtd.information.view;

import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PageSelectView extends ImageView {
	
	private Bitmap selectImage = null;
	private Bitmap unSelectImage = null;
	
	private int offs = Utils.dipToPx(14);
	private int mCount = 0;
	private int mNumber = 1;
	
	private Paint mPaint;
	
	private int mSelectImageStart = 0;
	private int mUnSelectImageStart = 0;
	private int mSelectImageTop = 0;
	private int mUnSelectImageTop = 0;
	private int mStart = 0;
	
	public PageSelectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public PageSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
	}

	public PageSelectView(Context context) {
		super(context);
		mPaint = new Paint();
	}
	public void setPages(int count){
		this.mCount = count;
	}
	
	public void setOffs(int offs){
		this.offs = offs;
		this.invalidate();
	}
	
	public void setNumber(int number){
		this.mNumber = number;
		this.invalidate();
	}
	
	private void setSelectIc(int reId){
		selectImage = BitmapFactory.decodeResource(this.getResources(), reId);
		mSelectImageStart = (offs - selectImage.getWidth()) / 2;
		
		
	}
	private void setUnSelectIc(int reId){
		unSelectImage = BitmapFactory.decodeResource(this.getResources(), reId);
		mUnSelectImageStart = (offs - unSelectImage.getWidth()) / 2;
		
	}
	
	public void setIc(int selectIcId, int unSelectIcId){
		setSelectIc(selectIcId);
		setUnSelectIc(unSelectIcId);
		requestLayout();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int measureSpec) {
	    int result = 0;
	    int specMode = MeasureSpec.getMode(measureSpec);
	    int specSize = MeasureSpec.getSize(measureSpec);

	    if (specMode == MeasureSpec.EXACTLY) {
	        // We were told how big to be
	        result = specSize;
	    } else {
	        // Measure the text
	        result = (int) offs * mCount + getPaddingLeft()
	                + getPaddingRight();
	        if (specMode == MeasureSpec.AT_MOST) {
	            // Respect AT_MOST value if that was what is called for by measureSpec
	            result = Math.min(result, specSize);
	        }
	    }

	    return result;
	}
	
	private int measureHeight(int measureSpec) {
	    int result = 0;
	    int specMode = MeasureSpec.getMode(measureSpec);
	    int specSize = MeasureSpec.getSize(measureSpec);

	    if (specMode == MeasureSpec.EXACTLY) {
	        // We were told how big to be
	        result = specSize;
	    } else {
	        // Measure the text
	    	if(selectImage != null || unSelectImage != null){
	    		result = (int) Math.max(selectImage.getHeight(), unSelectImage.getHeight()) + getPaddingTop()
		                + getPaddingBottom();
	    	}
	        if (specMode == MeasureSpec.AT_MOST) {
	            // Respect AT_MOST value if that was what is called for by measureSpec
	            result = Math.min(result, specSize);
	        }
	    }

	    return result;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if(unSelectImage != null && selectImage != null){
			mUnSelectImageTop = (this.getHeight() - unSelectImage.getHeight()) / 2;
			mSelectImageTop = (this.getHeight() - selectImage.getHeight()) / 2;
		}
		mStart = (this.getWidth() - offs * mCount) / 2;
		super.onLayout(changed, left, top, right, bottom);
	}
	@Override
	protected void onDraw(Canvas canvas) {

		if(selectImage != null && unSelectImage != null && mCount > 0){
			for(int i = 0; i < mCount; i ++){
				if(i == mNumber - 1){
					int left = mStart + i * offs + mSelectImageStart;
					canvas.drawBitmap(selectImage, left, mSelectImageTop, mPaint);
				}else{
					int left = mStart + i * offs + mUnSelectImageStart;
					canvas.drawBitmap(unSelectImage, left, mUnSelectImageTop, mPaint);
				}
			}
		}
		
	}
}
