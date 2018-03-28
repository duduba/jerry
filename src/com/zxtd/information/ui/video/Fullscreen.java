package com.zxtd.information.ui.video;

import com.zxtd.information.ui.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.VideoView;


public class Fullscreen extends Activity implements OnErrorListener, 
						OnCompletionListener, OnPreparedListener, OnTouchListener{
	
	private static final String TAG = "Fullscreen";
	private ZxtdVideoView videoView= null;
	private String url = null;
	//not init
	private int mWhenPausedPosition = -1;
	private GestureDetector gestureDetector;
	private ProgressDialog videoLoading = null;
	private boolean isFillScreen = true;
//	private MyHandler refreshHandler = null;
	private static int screenWidth = 0;
	private static int screenHeight = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.full_screen_video_view);

		getScreenSize();
		videoView = (ZxtdVideoView) findViewById(R.id.zxtd_full_videoView);
//		refreshHandler = new MyHandler();
		
		
		videoView.setOnTouchListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnErrorListener(this);
		videoView.setOnPreparedListener(this);
		
		gestureDetector = new GestureDetector(this, gestureListener);
		
		Bundle iBundle = getIntent().getExtras();
		if (iBundle != null) {
			url = iBundle.getString("url");
			loadVideo(url);
		}

	}
	
	
	private void loadVideo(String url) {
		videoView.setVideoURI(Uri.parse(url));//)(url);
		videoView.setMediaController(new MediaController(this));
	}
	
	@Override
	public void onStart() {
		videoLoading = ProgressDialog.show(this, "正在加载...", "精彩即将开始");
		super.onStart();
	}

	@Override
	public void onPause() {
		mWhenPausedPosition = videoView.getCurrentPosition();
		videoView.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mWhenPausedPosition >= 0) {
			loadVideo(url);
			videoView.seekTo(mWhenPausedPosition);
//			mWhenPausedPosition = -1;
		}
	}
	
	@Override
	protected void onDestroy() {
		if(videoView.isPlaying()){
			videoView.stopPlayback();
		}
		super.onDestroy();
	}


	public boolean onTouch(View arg0, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	public void onPrepared(MediaPlayer mp) {
		if (videoLoading.isShowing()) {
			videoLoading.dismiss();
		}

		if (mWhenPausedPosition == -1) {
			videoView.start();
		}else {
			videoView.pause();
			mWhenPausedPosition = -1;
		}
		
	}

	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.e("Fullscreen onError", "-------what:"+what+"-----extra:"+extra+"---------");
		return false;
	}

	private void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
	}
	
   private void setVideoScale(boolean flag){
    	
    	videoView.getLayoutParams();
    	
    	if (flag) {
    			
    			Log.d(TAG, "screenWidth: "+screenWidth+" screenHeight: "+screenHeight);
    			videoView.setVideoScale(screenWidth, screenHeight, false, 0);
    			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			
    	}else {
    			int videoWidth = videoView.getVideoWidth();
    			int videoHeight = videoView.getVideoHeight();
    			int mWidth = screenWidth;
    			int mHeight = screenHeight - 25;
    			
    			if (videoWidth > 0 && videoHeight > 0) {
    	            if ( videoWidth * mHeight  > mWidth * videoHeight ) {
    	                //Log.i("@@@", "image too tall, correcting");
    	            	mHeight = mWidth * videoHeight / videoWidth;
    	            } else if ( videoWidth * mHeight  < mWidth * videoHeight ) {
    	                //Log.i("@@@", "image too wide, correcting");
    	            	mWidth = mHeight * videoWidth / videoHeight;
    	            } else {
    	                
    	            }
    	        }
    			Log.d(TAG, "width:"+mWidth+" height:"+mHeight);
    			videoView.setVideoScale(mWidth, mHeight, true, screenWidth);

//    			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			
    	}
    }
	
//	private class MyHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			Log.e("MyHandler", "sssssssss:"+msg.what);
//			if (msg.what == 1) {
//				videoView.invalidate();
//				
//				return;
//			}
//			
//			super.handleMessage(msg);
//		}
//	}
//	
//	private void refreshView() {
//		refreshHandler.sendEmptyMessage(1);
//	}
	
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener(){

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d("onDoubleTap", "=============================");
//			videoView.pause();
//			mWhenPausedPosition = videoView.getCurrentPosition();
//			videoView.stopPlayback();
//			if (videoView.isFullScreenShow()) {
//				videoView.setFullScreenShow(false);
//			}else {
//				videoView.setFullScreenShow(true);
//			}
//			loadVideo(url);
//			videoView.seekTo(mWhenPausedPosition);
//			mWhenPausedPosition = -1;
			
//			videoView.pause();
			if (!isFillScreen) {
//				FrameLayout.LayoutParams fullsize = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//				videoView.setFullScreenShow(true);
//				videoView.setLayoutParams(fullsize);
				isFillScreen = true;
				setVideoScale(true);
//				videoView.setFullScreenShow(true);
//				refreshView();
			}else {
//				FrameLayout.LayoutParams fullsize = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				videoView.setFullScreenShow(false);
//				videoView.setLayoutParams(fullsize);
				
//				videoView.invalidate();
				setVideoScale(false);
				isFillScreen = false;
//				videoView.setFullScreenShow(false);
//				refreshView();
			}
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			videoView.toggleShowController();
			
			return true;
		}

	};
}
