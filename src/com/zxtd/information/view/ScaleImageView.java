package com.zxtd.information.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ScaleImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable drawable = this.getDrawable();
		if(drawable != null){
			int height = 0;
			int width = measureWidth(widthMeasureSpec);
			if(View.GONE != getVisibility()){
				double ratio = drawable.getMinimumHeight()*1.0d/drawable.getMinimumWidth();
				height = (int)Math.round(width * ratio);
			}
			setMeasuredDimension(width, height);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		
		
	}
	
	protected int measureHeight(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		int result = 500;

		if (specMode == MeasureSpec.AT_MOST)
		{
			result = specSize;
		}
		else if (specMode == MeasureSpec.EXACTLY)
		{
			result = specSize;
		}
		return result;
	}

	private int measureWidth(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST)
		{
			result = specSize;
		}else if (specMode == MeasureSpec.EXACTLY)
		{
			result = specSize;
		}
		return result;
	}
}
