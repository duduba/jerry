package com.zxtd.information.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class ZoomImageView extends View {
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private Bitmap bitmap;
	private Paint paint;
	
	
	private int heigth = 0;
	private int width = 0;
	
	private boolean isFirst = true;
	private float minSacle = 0.0f;
	private float currentSacle = 0.0f;
	
	
	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZoomImageView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		
		if(bitmap != null && !bitmap.isRecycled()){
			if(isFirst){
				isFirst =false;
				minSacle = this.getMeasuredWidth() * 1.0f / bitmap.getWidth();
				if(minSacle > 1){
					minSacle = 1;
				}
				currentSacle = minSacle;
				matrix.postScale(minSacle, minSacle);
				isMatrix();
			}
			canvas.drawBitmap(bitmap, matrix, paint);
		}
	}
	
	
	private boolean isMatrix(){
		if(bitmap == null){
			return false;
		}
		float[] values = new float[9];
		boolean isIntercept = false;
		matrix.getValues(values);
		currentSacle = values[0];
		int calculateWidth = (int)(bitmap.getWidth() * currentSacle);
		int calculateHeigth = (int)(bitmap.getHeight() * currentSacle);
		int deltaX = width - calculateWidth;
		int deltaY = heigth - calculateHeigth;
		int calculateX = (int) values[2];
		int calculateY = (int) values[5];
		if(deltaX > 0){
			values[2] = deltaX / 2;
		}else{
			if(calculateX > 0){
				values[2] = 0.0f;
			}else if(calculateX < deltaX){
				values[2] = deltaX;
			}else{
				isIntercept = true;
			}
		}
		if(deltaY > 0){
			values[5] = deltaY / 2;
		}else{
			if(calculateY > 0){
				values[5] = 0.0f;
			}else if(calculateY < deltaY){
				values[5] = deltaY;
			}
		}
		matrix.setValues(values);
		savedMatrix.set(matrix);
		
		return isIntercept;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
	
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
		invalidate();
	}
	
	public void clear(){
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	
	private int measureHeight(int measureSpec) {
		
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		heigth = specSize;
		
		int result = 20;

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
		
		width = specSize;
		
		int result = 20;
		if (specMode == MeasureSpec.AT_MOST)
		{
			result = specSize;
			
		}else if (specMode == MeasureSpec.EXACTLY)
		{
			result = specSize;
		}
		return result;
	}
	
	public boolean ZoomImage(MotionEvent event) {
		if(bitmap == null){
			return false;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			return false;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			return false;
		case MotionEvent.ACTION_POINTER_DOWN:
			
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			return false;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x,
						event.getY() - start.y);
				start.x = event.getX();
				start.y = event.getY();
				if(!isMatrix()){
					this.invalidate();
					return false;
				}
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f ) {
					float scale = newDist / oldDist;
					setSacle(scale);
				}
				oldDist = newDist;
			}
			break;
		}
		return true;
	}
	
	private void setSacle(float scale){
		
		if(currentSacle <= minSacle && scale < 1.0f){
			return;
		}else if(currentSacle >= 3 && scale > 3.0f){
			return;
		}
		
		float newSacle = currentSacle * scale;

		if(newSacle < minSacle ){
	
			scale = minSacle / currentSacle;
		}else if(newSacle > 3){

			scale = 3 / currentSacle;
		}
		matrix.set(savedMatrix);
		matrix.postScale(scale, scale, mid.x, mid.y);
		isMatrix();
		this.invalidate();
	}
	
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
