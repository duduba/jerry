package com.zxtd.information.ui.custview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;


/**
 * listview 阻尼效果
 * @author Administrator
 *
 */
public class DampListView extends ListView implements Runnable { 
    private float mLastDownY = 0f;  
    private int mDistance = 0;  
    private int mStep = 0;  
    private boolean mPositive = false;  
    private String Tag="PullListview"; 
  
 
    public DampListView (Context context, AttributeSet attrs) {  
          super(context, attrs);  
    }  
  
    public DampListView (Context context, AttributeSet attrs, int defStyle) {  
          super(context, attrs, defStyle);  
    }  
  
    public DampListView (Context context) {  
          super(context);  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
         switch (event.getAction()) {  
              case MotionEvent.ACTION_DOWN: //手指按下时触发 
                  Log.d(Tag,"ActionDown"); 
                   if (mLastDownY == 0f && mDistance == 0) {                        
                         mLastDownY = event.getY();  
                         Log.d(Tag,"mLastDownY赋值"+mLastDownY); 
                   return true;  
              }  
              break;  
  
    case MotionEvent.ACTION_CANCEL:  
           break; 
 
    case MotionEvent.ACTION_UP: //手指抬起之后触发 
      Log.d(Tag,"ActionUP"); 
          if (mDistance != 0) {  
              System.out.println("---post"); 
           mStep = 1;  
           mPositive = (mDistance >= 0);  
           this.post(this);          
           return true;  
        }  
        mLastDownY = 0f;  
        mDistance = 0;  
        break;  
  
    case MotionEvent.ACTION_MOVE:  //手指按下之后滑动触发
      Log.d(Tag,"ActionMove"); 
        if (mLastDownY != 0f) {               
              mDistance = (int) (mLastDownY - event.getY());  
              Log.d(Tag,"mLastDownY不为0，view跟随滑动"+"mDistance"+mDistance); 
              if (getChildCount()>0 && (mDistance < 0 && getFirstVisiblePosition() == 0 && 
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
        break;  
        }  
        return super.onTouchEvent(event);  
    }  
  
    public void run() {  
           Log.d(Tag,"ActionUP调用post"); 
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
     }  
}
