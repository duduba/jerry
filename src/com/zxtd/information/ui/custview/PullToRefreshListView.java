package com.zxtd.information.ui.custview;
import java.text.SimpleDateFormat;   
import java.util.Date;   
import java.util.HashMap;
import java.util.Map;

import com.zxtd.information.ui.R;

import android.content.Context;   
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;   
import android.util.Log;   
import android.view.LayoutInflater;   
import android.view.MotionEvent;   
import android.view.View;   
import android.view.ViewGroup;      
import android.view.animation.LinearInterpolator;   
import android.view.animation.RotateAnimation;   
import android.widget.AbsListView;   
import android.widget.BaseAdapter;   
import android.widget.ImageView;   
import android.widget.LinearLayout;   
import android.widget.ListView;   
import android.widget.ProgressBar;   
import android.widget.TextView;   
import android.widget.AbsListView.OnScrollListener;  


public class PullToRefreshListView extends ListView implements OnScrollListener{

	 private static final String TAG = "listview";   
	   
	    private final static int RELEASE_To_REFRESH = 0;   
	    private final static int PULL_To_REFRESH = 1;   
	    private final static int REFRESHING = 2;   
	    private final static int DONE = 3;   
	    private final static int LOADING = 4;   
	    private Context context;
	   
	    // 实际的padding的距离与界面上偏移距离的比例    
	    private final static int RATIO = 3;   
	   
	    private LayoutInflater inflater;   
	   
	    private LinearLayout headView;   
	   
	    private TextView tipsTextview;   
	    private TextView lastUpdatedTextView;   
	    private ImageView arrowImageView;   
	    private ProgressBar progressBar;   
	   
	    private RotateAnimation animation;   
	    private RotateAnimation reverseAnimation;   
	   
	    // 用于保证startY的值在一个完整的touch事件中只被记录一次    
	    private boolean isRecored;   
	   
	    private int headContentWidth;   
	    private int headContentHeight;   
	   
	    private int startY;   
	    private int firstItemIndex;
	    
	    public int getFirstItemIndex() {
			return firstItemIndex;
		}
	    
	    /*
	    private int firstShowIndex=0;
	    public int getFirstShowIndex() {
			return firstShowIndex;
		}*/


		private int state;   
	   
	    private boolean isBack;   
	   
	    private OnRefreshListener refreshListener;   
	   
	    private boolean isRefreshable;   
	    
	    //音效
		private SoundPool sp = null;
		private Map<Integer,Integer> map=null;
	   
	    public PullToRefreshListView(Context context) {   
	        super(context);   
	        // 调用下面初始化的函数    
	        init(context);   
	    }   
	   
	    public PullToRefreshListView(Context context, AttributeSet attrs) {   
	        super(context, attrs);   
	        // 调用下面初始化的函数    
	        init(context);   
	    }   
	   
	    private void init(Context context) {   
	    	this.context=context;
	        // 获得LayoutInflater从给定的上下文。    
	        inflater = LayoutInflater.from(context);   
	   
	        // 实例化布局XML文件转换成相应的视图对象。    
	        headView = (LinearLayout) inflater.inflate(R.layout.common_pullrefresh_listview, null);   
	   
	        arrowImageView = (ImageView) headView   
	                .findViewById(R.id.head_arrowImageView);   
	        // 设置最小宽度 和高度    
	        arrowImageView.setMinimumWidth(70);   
	        arrowImageView.setMinimumHeight(100);   
	        // 实例化布局XML文件转换成相应的视图对象。    
	        progressBar = (ProgressBar) headView   
	                .findViewById(R.id.head_progressBar);   
	        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);   
	        lastUpdatedTextView = (TextView) headView   
	                .findViewById(R.id.head_lastUpdatedTextView);   
	   
	        // 调用下拉刷新的方法    
	        measureView(headView);   
	        // d得到原始高度和宽度    
	        headContentHeight = headView.getMeasuredHeight();   
	        headContentWidth = headView.getMeasuredWidth();   
	   
	        // 设置填充。视图可能添加的空间要求显示滚动条,这取决于风格和知名度的滚动条    
	        headView.setPadding(0, -1 * headContentHeight, 0, 0);   
	        headView.invalidate();   
	   
	        // 标签用来识别一个日志消息的来源。标识类或活动日志调用发生    
	        Log.v("size", "width:" + headContentWidth + " height:"   
	                + headContentHeight);   
	   
	        // 添加一个固定视图出现在列表的顶部    
	        addHeaderView(headView, null, false);   
	        // 设置监听事件    
	        setOnScrollListener(this);   
	   
	        // 动画效果实现下拉和松开时候图片的 180度 旋转 注意0, -180,和他是有区别的 -180, 0,    
	        animation = new RotateAnimation(0, -180,   
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,   
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);   
	        // 设置加速度曲线为这个动画。默认值为一个线性插值。    
	        animation.setInterpolator(new LinearInterpolator());   
	        animation.setDuration(300);   
	        // 如果fillAfter是真的,转换,该动画执行完成时将会持续下去    
	        animation.setFillAfter(true);   
	   
	        reverseAnimation = new RotateAnimation(-180, 0,   
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,   
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);   
	        // 设置加速度曲线为这个动画。默认值为一个线性插值。    
	        reverseAnimation.setInterpolator(new LinearInterpolator());   
	        reverseAnimation.setDuration(300);   
	        // 如果fillAfter是真的,转换,该动画执行完成时将会持续下去    
	        reverseAnimation.setFillAfter(true);   
	   
	        // 设置状态    
	        state = DONE;   
	        // 设置不可刷新状态    
	        isRefreshable = false;   
	        
	        
	        
	        sp=new SoundPool(2,AudioManager.STREAM_MUSIC,100);
			map=new HashMap<Integer,Integer>();
			map.put(1, sp.load(context,R.raw.beep, 1));
			map.put(2, sp.load(context,R.raw.shake, 1));
			map.put(3, sp.load(context,R.raw.ptt_sound_playsound_over, 1));
			map.put(4, sp.load(context,R.raw.refresh_pulling, 1));
			map.put(5, sp.load(context,R.raw.refresh_loading, 1));
	    }   
	   
	    // 回调方法时要调用的列表或网格已经滚动。这将完成之后调用的滚动方法    
	    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,   
	            int arg3) {   
	        firstItemIndex = firstVisiableItem;   
	    }   
	   
	    /*  
	     * 回调方法调用而列表视图或网格视图被滚动。 如果这个视图被滚动,将调用此方法在接下来的一局画卷的呈现 *  
	     */   
	    public void onScrollStateChanged(AbsListView arg0, int scrollState) {  
	    	//if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	    	//	firstShowIndex=getFirstVisiblePosition();
           // }
	    }   
	   
	    // 触摸事件监听    
	    public boolean onTouchEvent(MotionEvent event) {   
	   
	        // 判断是否可以刷新    
	        if (isRefreshable) {   
	            // 根据动作相应不同的方法    
	            switch (event.getAction()) {   
	   
	            // 当按住屏幕向下拉屏幕的时候    
	            case MotionEvent.ACTION_DOWN:   
	                if (firstItemIndex == 0 && !isRecored) {   
	                    isRecored = true;   
	                    startY = (int) event.getY();   
	                    Log.v(TAG, "在下拉的时候记录当前位置‘");   
	                }   
	                break;   
	   
	            // 当按住屏幕向上松屏幕的时候    
	            case MotionEvent.ACTION_UP:   
	   
	                if (state != REFRESHING && state != LOADING) {   
	                    if (state == DONE) {   
	   
	                    }   
	                    if (state == PULL_To_REFRESH) {   
	                        state = DONE;   
	                        changeHeaderViewByState();   
	   
	                        Log.v(TAG, "由下拉刷新状态，到done状态");   
	                    }   
	                    if (state == RELEASE_To_REFRESH) {   
	                        state = REFRESHING;   
	                        changeHeaderViewByState();   
	                        onRefresh();   
	   
	                        Log.v(TAG, "由松开刷新状态，到done状态");   
	                    }   
	                }   
	   
	                isRecored = false;   
	                isBack = false;   
	   
	                break;   
	   
	            // 当按住屏幕移动时候    
	            case MotionEvent.ACTION_MOVE:   
	                int tempY = (int) event.getY();   
	   
	                if (!isRecored && firstItemIndex == 0) {   
	                    Log.v(TAG, "在move时候记录下位置");   
	                    isRecored = true;   
	                    startY = tempY;   
	                }   
	   
	                if (state != REFRESHING && isRecored && state != LOADING) {   
	   
	                    /***  
	                     * ， 当前的位置一直是在head，否则如果当列表超出屏幕的话， 当在上推的时候，列表会同时进行滚动  
	                     */   
	                    // 可以松手去刷新了    
	                    if (state == RELEASE_To_REFRESH) {   
	   
	                        setSelection(0);   
	   
	                        // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步    
	                        if (((tempY - startY) / RATIO < headContentHeight)   
	                                && (tempY - startY) > 0) {   
	                            state = PULL_To_REFRESH;   
	                            changeHeaderViewByState();   
	   
	                            Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");   
	                        }   
	                        // 一下子推到顶了    
	                        else if (tempY - startY <= 0) {   
	                            state = DONE;   
	                            // 调用改变时候的方法，更新UI    
	                            changeHeaderViewByState();   
	   
	                            Log.v(TAG, "由松开刷新状态转变到done状态");   
	                        } else {   
	                        }   
	                    }   
	                    // 还没有到达显示松开刷新的时候    
	                    if (state == PULL_To_REFRESH) {   
	   
	                        setSelection(0);   
	   
	                        // 下拉到可以进入RELEASE_TO_REFRESH的状态    
	                        if ((tempY - startY) / RATIO >= headContentHeight) {   
	                            state = RELEASE_To_REFRESH;   
	                            isBack = true;   
	                            // 调用改变时候的方法，更新UI    
	                            changeHeaderViewByState();   
	   
	                            Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");   
	                        }   
	                        // 上推到顶了    
	                        else if (tempY - startY <= 0) {   
	                            state = DONE;   
	                            // 调用改变时候的方法，更新UI    
	                            changeHeaderViewByState();   
	   
	                            Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");   
	                        }   
	                    }   
	   
	                    // done状态下    
	                    if (state == DONE) {   
	                        if (tempY - startY > 0) {   
	                            state = PULL_To_REFRESH;   
	                            // 调用改变时候的方法，更新UI    
	                            changeHeaderViewByState();   
	                        }   
	                    }   
	   
	                    // 更新headView的size    
	                    if (state == PULL_To_REFRESH) {   
	                        headView.setPadding(0, -1 * headContentHeight   
	                                + (tempY - startY) / RATIO, 0, 0);   
	   
	                    }   
	   
	                    // 更新headView的paddingTop    
	                    if (state == RELEASE_To_REFRESH) {   
	                        headView.setPadding(0, (tempY - startY) / RATIO   
	                                - headContentHeight, 0, 0);   
	                    }   
	   
	                }   
	   
	                break;   
	            }   
	        }   
	   
	        return super.onTouchEvent(event);   
	    }   
	   
	    private void playSound(int sound,int repeat){
			//实例化AudioManager对象，控制声音
		   // AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		    //最大音量
		   // float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		    //当前音量
		    //float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		    //float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
		    //播放
		    sp.play(map.get(sound),     //声音资源
		        10.0f,         //左声道
		        10.0f,         //右声道
		        1,             //优先级，0最低
		        repeat,         //循环次数，0是不循环，-1是永远循环
		        1);            //回放速度，0.5-2.0之间。1为正常速度
		}
	    
	    
	    
	    
	    // 当状态改变时候，调用该方法，以更新界面    
	    private void changeHeaderViewByState() {   
	        // 根据当前的状态进行判断    
	        switch (state) {   
	   
	        // 下拉时候，松开既可刷新    
	        case RELEASE_To_REFRESH:   
	            // 设置视图 VISIBLE 可见 ，GONE 不可见    
	        	playSound(4,1);
	            arrowImageView.setVisibility(View.VISIBLE);   
	            progressBar.setVisibility(View.GONE);   
	            tipsTextview.setVisibility(View.VISIBLE);   
	            lastUpdatedTextView.setVisibility(View.VISIBLE);   
	   
	            // 现在开始指定的动画。    
	            arrowImageView.clearAnimation();   
	            arrowImageView.startAnimation(animation);   
	   
	            tipsTextview.setText("松开既可刷新");   
	   
	            Log.v(TAG, "当前状态，松开即可刷新");   
	            break;   
	   
	        // 开始时候，下拉刷新    
	        case PULL_To_REFRESH:   
	            // 设置视图 VISIBLE 可见 ，GONE 不可见    
	            progressBar.setVisibility(View.GONE);   
	            tipsTextview.setVisibility(View.VISIBLE);   
	            lastUpdatedTextView.setVisibility(View.VISIBLE);   
	            // 现在开始指定的动画。    
	            arrowImageView.clearAnimation();   
	            arrowImageView.setVisibility(View.VISIBLE);   
	   
	            if (isBack) {   
	                isBack = false;   
	                // 现在开始指定的动画。    
	                arrowImageView.clearAnimation();   
	                arrowImageView.startAnimation(reverseAnimation);   
	   
	                tipsTextview.setText("下拉刷新");   
	            } else {   
	                tipsTextview.setText("下拉刷新");   
	            }   
	            Log.v(TAG, "当前状态，下拉刷新");   
	            break;   
	   
	        case REFRESHING:   
	        	playSound(5,1);
	            headView.setPadding(0, 0, 0, 0);   
	            // 设置视图 VISIBLE 可见 ，GONE 不可见    
	            progressBar.setVisibility(View.VISIBLE);   
	            // 现在开始指定的动画。    
	            arrowImageView.clearAnimation();   
	            arrowImageView.setVisibility(View.GONE);   
	            tipsTextview.setText("正在刷新...");   
	            lastUpdatedTextView.setVisibility(View.VISIBLE);   
	   
	            Log.v(TAG, "当前状态,正在刷新...");   
	            break;   
	        case DONE:   
	            // 设置填充。视图可能添加的空间要求显示滚动条    
	            headView.setPadding(0, -1 * headContentHeight, 0, 0);   
	            // 设置视图 VISIBLE 可见 ，GONE 不可见    
	            progressBar.setVisibility(View.GONE);   
	            // 现在开始指定的动画。    
	            arrowImageView.clearAnimation();   
	            arrowImageView.setImageResource(R.drawable.arrow_down);   
	            tipsTextview.setText("下拉刷新");   
	            lastUpdatedTextView.setVisibility(View.VISIBLE);   
	   
	            Log.v(TAG, "当前状态");   
	            break;   
	        }   
	    }   
	   
	    public void setonRefreshListener(OnRefreshListener refreshListener) {   
	        this.refreshListener = refreshListener;   
	        isRefreshable = true;   
	    }   
	   
	    public interface OnRefreshListener {   
	        public void onRefresh();   
	    }   
	   
	    // 设置更新时间    
	    public void onRefreshComplete() {   
	        state = DONE;   
	        //    
	        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");   
	        String date = format.format(new Date());   
	        lastUpdatedTextView.setText("最近更新:" + date);   
	        changeHeaderViewByState();
	        
	        //requestFocusFromTouch();
	        //setSelection(0);
	    }   
	   
	    private void onRefresh() {   
	        if (refreshListener != null) {   
	            refreshListener.onRefresh();   
	        }   
	    }   
	   
	    // 下拉刷新的    
	    private void measureView(View child) {   
	        // v这组布局参数宽度和高度    
	        ViewGroup.LayoutParams p = child.getLayoutParams();   
	        if (p == null) {   
	            // 创建一个新组布局参数指定的宽度(填充)和高度（包裹）。    
	            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,   
	                    ViewGroup.LayoutParams.WRAP_CONTENT);   
	        }   
	   
	        // d得到childWidthSpec(高度或宽度)的子视图    
	        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);   
	        int lpHeight = p.height;   
	        int childHeightSpec;   
	        if (lpHeight > 0) {   
	            // 创建一个测量规范基于所提供的大小和模式    
	            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,   
	                    MeasureSpec.EXACTLY);   
	        } else {   
	            childHeightSpec = MeasureSpec.makeMeasureSpec(0,   
	                    MeasureSpec.UNSPECIFIED);   
	        }   
	        // 找出一个视图应该多大。父供应约束信息在宽度和高度参数    
	        child.measure(childWidthSpec, childHeightSpec);   
	    }   
	   
	    public void setAdapter(BaseAdapter adapter) {   
	        // 设置最近刷新的时间    
	        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");   
	        String date = format.format(new Date());   
	        lastUpdatedTextView.setText("最近更新:" + date);   
	        super.setAdapter(adapter);   
	    }   
	
}
