package com.zxtd.information.ui.custview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class FilpperListvew extends ListView /*implements Runnable*/{
	//阻尼效果
	/*
	private float mLastDownY = 0f;  
	private int mDistance = 0;  
	private int mStep = 0;  
	private boolean mPositive = false;  
	private String Tag="PullListview"; 
	*/
	
	
	
	private float myLastX = -1;
	private float myLastY = -1;
	private boolean delete = false;
	//自定义的滑动删除监听
	private FilpperDeleteListener filpperDeleterListener;

	public FilpperListvew(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FilpperListvew(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 获得第一个点的x坐标
			myLastX = ev.getX(0);
			myLastY = ev.getY(0);
			
			
			
			//阻尼
			/*
		    if(mLastDownY == 0f && mDistance == 0){                        
                 mLastDownY = ev.getY();
                 return true;  
		    } 
		    */
		break;

		case MotionEvent.ACTION_MOVE:
			// 得到最后一个点的坐标
			float deltaX =Math.abs(ev.getX(ev.getPointerCount() - 1) - myLastX) ;
			float deltaY = Math.abs(ev.getY(ev.getPointerCount() - 1) - myLastY);
			// 可以滑动删除的条件：横向滑动大于100，竖直差小于50
			if ((deltaX > 70.0 && deltaY < 50)) {
				delete = true;
			}
			
			
			
			//阻尼
			/*
			   if (mLastDownY != 0f) {               
		              mDistance = (int) (mLastDownY - ev.getY());  
		              Log.d(Tag,"mLastDownY不为0，view跟随滑动"+"mDistance"+mDistance); 
		              if ((mDistance < 0 && getFirstVisiblePosition() == 0 && 
		                getChildAt(0).getTop() == 0) || (mDistance > 0 &&
		                 getLastVisiblePosition() == getCount() - 1)) {  
		                  //第一个位置并且是想下拉，就滑动或者最后一个位置向上拉  
		                  //这个判断的作用是在非顶端的部分不会有此滚动 
		                  mDistance /= 2; //这里是为了减少滚动的距离 
		                   scrollTo(0, mDistance); //滚动 
		                   return true;  
		               }  
		        }  
		        mDistance = 0;
		        */
			
			break;

		case MotionEvent.ACTION_UP:
			if (delete && filpperDeleterListener != null) {
				filpperDeleterListener.filpperDelete(myLastX,myLastY);
			}
			reset();
			
			/*
			//阻尼
			 if(mDistance != 0) {  
	              System.out.println("---post"); 
	           mStep = 1;  
	           mPositive = (mDistance >= 0);  
	           this.post(this);          
	           return true;  
	        }  
	        mLastDownY = 0f;  
	        mDistance = 0; 
	        */
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void reset(){
		delete = false ;
		myLastX = -1 ;
		myLastY = -1 ;
	}
	
	public void setFilpperDeleteListener(FilpperDeleteListener f) {
		filpperDeleterListener = f;
	}

	//自定义的接口
	public interface FilpperDeleteListener {
		public void filpperDelete(float xPosition,float yPosition);
	}

	
	
	//阻尼
	/*
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 mDistance += mDistance > 0 ? -mStep : mStep;  
         scrollTo(0, mDistance);  
         if ((mPositive && mDistance <= 0) || (!mPositive && mDistance >= 0)) {  
               scrollTo(0, 0);  
               mDistance = 0;  
               mLastDownY = 0f;  
               Log.d(Tag,"post中置0"); 
               return;  
          }  
         mStep += 1;  
       // this.postDelayed(this, 10);  
         this.post(this); 
	}*/
	
}
