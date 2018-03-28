package com.zxtd.information.ui.me;


import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.adapter.ImageAdapter;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.AlignLeftGallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 我的主页
 * */
public class MineActivity extends BaseActivity implements 
		OnClickListener,AlignLeftGallery.OnItemSelectedListener{
	
	private ImageView imgBg;
	TranslateAnimation left, right;
	Animation up;
	private AlignLeftGallery myGallery;
	
	/*
	private final static int RELEASE_To_REFRESH = 0;  
	private final static int PULL_To_REFRESH = 1;  
	private final static int REFRESHING = 2;  
	private final static int DONE = 3;  
	private final static int LOADING = 4;
	private final static int RATIO = 3;
	private int state; 
	private boolean isRecored;
	private boolean isBack;
	private int startY; 
	*/
	
	private static int marginTop=200;
	private ImageAdapter adapter=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_main);
		//initView();
	}
	
	
	void initView(){
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText(R.string.my_home);

		/*
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setVisibility(View.GONE);
		btnBack.setOnClickListener(this);
*/
		Button btnCompose=(Button) findViewById(R.id.btn_compose);
		btnCompose.setOnClickListener(this);

		
		imgBg=(ImageView) findViewById(R.id.mine_bg_img);
		ScrollView scroll=(ScrollView) findViewById(R.id.scroll_layout);
		scroll.setVerticalScrollBarEnabled(false);
		scroll.setHorizontalScrollBarEnabled(false);
		//release();
		
		myGallery=(AlignLeftGallery) findViewById(R.id.myGallery);
		initGalleryData();
		myGallery.setSelection(2);
		myGallery.setOnItemSelectedListener(this);
		myGallery.setSpacing(10); 
		//baseInfoLayout=(DragLinearLayout) findViewById(R.id.mine_base_info);
		//baseInfoLayout.setOnTouchListener(touchListener);
		//runAnimation();
	}

	private void release(){
		int screenHeight=getScreenSize()[1];
		RelativeLayout.LayoutParams params=
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		marginTop=screenHeight/3*2;
		params.setMargins(0,-marginTop , 0, 0);
		imgBg.setLayoutParams(params);
	}
	
	private void initGalleryData(){
		List<Object> list=new ArrayList<Object>(10);
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=230ccff514ce36d3a20484300ecb3b87/3801213fb80e7bec2c5e30ce2e2eb9389b506b1c.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048%3Bq%3D75/sign=26718697fcfaaf5184e386bfb86caf9f/1f178a82b9014a901e69680ca8773912b31bee28.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=4bb4b5908d5494ee8722081919cde1fe/241f95cad1c8a786717d7e216609c93d70cf504d.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=230ccff514ce36d3a20484300ecb3b87/3801213fb80e7bec2c5e30ce2e2eb9389b506b1c.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048%3Bq%3D75/sign=26718697fcfaaf5184e386bfb86caf9f/1f178a82b9014a901e69680ca8773912b31bee28.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=4bb4b5908d5494ee8722081919cde1fe/241f95cad1c8a786717d7e216609c93d70cf504d.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=230ccff514ce36d3a20484300ecb3b87/3801213fb80e7bec2c5e30ce2e2eb9389b506b1c.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048%3Bq%3D75/sign=26718697fcfaaf5184e386bfb86caf9f/1f178a82b9014a901e69680ca8773912b31bee28.jpg");
		list.add("http://a.hiphotos.baidu.com/album/w%3D2048/sign=4bb4b5908d5494ee8722081919cde1fe/241f95cad1c8a786717d7e216609c93d70cf504d.jpg");
		adapter=new ImageAdapter(list, this);
		myGallery.setAdapter(adapter);
	}
	

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.btn_compose:{
			}break;
		}
	}


	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(adapter!=null){
			Intent intent=new Intent(this,LoginActivity.class);
			startActivity(intent);
		}
		
	}


	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
    
	
	/*
    private View.OnTouchListener touchListener=new View.OnTouchListener(){
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {  
				case MotionEvent.ACTION_DOWN:  
		        	//Log.v(Constant.TAG, "在down时候记录当前位密密麻麻密密麻麻妈妈置"); 
		             startY = (int) event.getY();
		             Log.e("news", startY+" ssssssssssssssssss");
		          break;  
		          case MotionEvent.ACTION_UP:  
		        	  MineActivity.this.release();
		          break; 
		          case MotionEvent.ACTION_MOVE:  
		                int tempY = (int) event.getY();  
		                int moveY=tempY-startY;
		                Log.e("news", moveY+"mmmmmmmmmmmmmmmmmmmm");
		                RelativeLayout.LayoutParams params=
		        				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		                params.setMargins(0, -(marginTop-moveY), 0, 0);
		                imgBg.setLayoutParams(params);
		                
		                break;  
	        }                  
			return false;
		}
		
	};
	
	
	
	public void runAnimation() {

		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f);
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f);
		right.setDuration(25000);
		left.setDuration(25000);
		right.setFillAfter(true);
		left.setFillAfter(true);

		right.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				imgBg.startAnimation(left);
			}
		});
		left.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				imgBg.startAnimation(right);
			}
		});
		imgBg.startAnimation(right);
	}
	*/
}
