package com.zxtd.information.ui.me;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxtd.information.adapter.PageItemAdapter;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

public class FansAndFocusActivity extends BaseActivity implements OnClickListener{

	private ImageView img1;
	private ViewPager viewPager;
	private RadioButton rbFans;
	private RadioButton rbFocus;
	private RadioButton rbReco;
	private PageItemAdapter adapter;
	private float startLeft=0.0f;
	private int linesWidth=0;
	
	private FansLayout fansLayout=null;
	private FocusLayout focusLayout=null;
	private RecommandLayout recoLayout=null;
	private int userId=-1;
	private String from="";
	private boolean isSelf=false;
	private int splitNum=3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hiddleTitleBar();
		setContentView(R.layout.mine_fansfocus);
		Intent intent=getIntent();
		userId=intent.getIntExtra("userId", -1);
		from=intent.getStringExtra("from");
		isSelf=from.equals("self");
		initView();	
	}

	
	private void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText(getIntent().getStringExtra("userName")+"粉丝与关注");
		
		viewPager=(ViewPager) findViewById(R.id.mine_fansfocus_viewPager);
		rbFans=(RadioButton) findViewById(R.id.mine_fans_rb);
		rbFans.setOnClickListener(this);
		rbFocus=(RadioButton) findViewById(R.id.mine_focus_rb);
		rbFocus.setOnClickListener(this);
		rbReco=(RadioButton) findViewById(R.id.mine_recommend_rb);
		rbReco.setOnClickListener(this);
		
		img1=(ImageView) findViewById(R.id.img_select);
		int width=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int height=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		img1.measure(width, height);
		int[] demis=getScreenSize();
		
		if(!isSelf){
			rbReco.setVisibility(View.GONE);
			splitNum=2;
		}
		
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(demis[0]/splitNum,2);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.mine_fans_focus_rg);
		params.setMargins(0, 0, 0,1);
		img1.setLayoutParams(params);
		
		linesWidth=demis[0]/splitNum;
				
		List<View> mViewArray = new ArrayList<View>();
		fansLayout=new FansLayout(this,userId,from);
		mViewArray.add(fansLayout);
		focusLayout=new FocusLayout(this,userId,from);
		mViewArray.add(focusLayout);
		
		if(isSelf){
			recoLayout=new RecommandLayout(this,userId,from);
			mViewArray.add(recoLayout);
		}
		
		adapter=new PageItemAdapter(mViewArray);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new XonPageChangeListener());
		viewPager.setCurrentItem(0);
		
		int type=getIntent().getIntExtra("type", 0);
		if(type==1){
			startLeft=linesWidth;
		}
		changeCheckStatus(type);
	}


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.mine_fans_rb:{
				changeCheckStatus(0);
			}break;
			case R.id.mine_focus_rb:{
				changeCheckStatus(1);
			}break;
			case R.id.mine_recommend_rb:{
				changeCheckStatus(2);
			}break;
			
		}
	}
	
	
	/**
     * PageView监听:
     */
    private class XonPageChangeListener implements OnPageChangeListener{

		
		public void onPageScrollStateChanged(int key) {
			// TODO Auto-generated method stub
		}

		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			changeCheckStatus(arg0);
		}
    	
    }
    

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		fansLayout.destoryReceiver();
		focusLayout.destoryReceiver();
		if(isSelf){
			recoLayout.destoryReceiver();
		}
	}


	private void changeCheckStatus(int checkedId){
		switch (checkedId) {
		case 0:
		case R.id.mine_fans_rb:{
			rbFans.setClickable(false);
			rbFocus.setClickable(true);
			rbReco.setClickable(true);
			anit(0);
			viewPager.setCurrentItem(0);
		}break;
		case 1:
		case R.id.mine_focus_rb:{
			rbFans.setClickable(true);
			rbFocus.setClickable(false);
			rbReco.setClickable(true);
			anit(1);
			viewPager.setCurrentItem(1);
		}break;
		case 2:
		case R.id.mine_recommend_rb:{
			rbFans.setClickable(true);
			rbFocus.setClickable(true);
			rbReco.setClickable(false);
			anit(2);
			viewPager.setCurrentItem(2);
		}break;
		default:
			break;
		}
	}
    
    
	private void anit(int index){
		AnimationSet _AnimationSet = new AnimationSet(true);
		TranslateAnimation animation=new TranslateAnimation(startLeft,linesWidth*index,0,0);
		_AnimationSet.addAnimation(animation);
		_AnimationSet.setFillBefore(true);
		_AnimationSet.setFillAfter(true);
		_AnimationSet.setDuration(500);
		img1.startAnimation(_AnimationSet);
		startLeft=linesWidth*index;
	}
	
	
	public void setFansCount(int fansCount){
		rbFans.setText("粉丝 "+String.valueOf(fansCount));
	}
	
	public void setFocusCount(int count){
		rbFocus.setText("关注 "+String.valueOf(count));
	}
	
	
	
}
