package com.zxtd.information.ui.me;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.zxtd.information.adapter.PageItemAdapter;
import com.zxtd.information.bean.Province;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

public class CityActivity extends BaseActivity implements OnClickListener{

	private ImageView img1;
	private ViewPager viewPager;
	private RadioButton rbHot;
	private RadioButton rbAll;
	private PageItemAdapter adapter;
	
	private static final int requestCode=11;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_regist_citys);
		initView();
	}

	/**
	 * 
	 */
	private void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText("选择城市");
		
		viewPager=(ViewPager) findViewById(R.id.mine_city_viewPager);
		rbHot=(RadioButton) findViewById(R.id.mine_city_hot);
		rbHot.setOnClickListener(this);
		rbAll=(RadioButton) findViewById(R.id.mine_city_all);
		rbAll.setOnClickListener(this);
		
		img1=(ImageView) findViewById(R.id.img_select);
		int width=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int height=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		img1.measure(width, height);
		int[] demis=getScreenSize();
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(demis[0]/2-1,2);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.mine_regist_city_type);
		params.setMargins(0, 0, 0,1);
		img1.setLayoutParams(params);
		
		List<View> mViewArray = new ArrayList<View>();
		HotCityLayout hotLayout=new HotCityLayout(this);
		hotLayout.loadData();
		
		ProvinceLayout provLayout=new ProvinceLayout(this);
		provLayout.loadData();
		
		mViewArray.add(hotLayout);
		mViewArray.add(provLayout);
		
		adapter=new PageItemAdapter(mViewArray);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new XonPageChangeListener());
		viewPager.setCurrentItem(0);
		
		changeCheckStatus(0);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.mine_city_hot:{
				changeCheckStatus(0);
			}break;
			case R.id.mine_city_all:{
				changeCheckStatus(1);
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
    
    private void changeCheckStatus(int checkedId){
		int[] demis=getScreenSize();
		int screenWidth=demis[0]/2;
		switch (checkedId) {
		case 0:
		case R.id.mine_city_hot:
			rbHot.setClickable(false);
			rbAll.setClickable(true);
			//rbMy.setTextColor(Color.BLACK);
			//rbSystem.setTextColor(Color.parseColor("#888888"));
			anit(screenWidth,0);
			viewPager.setCurrentItem(0);
			break;
		case 1:
		case R.id.mine_city_all:
			rbHot.setClickable(true);
			rbAll.setClickable(false);
			//rbSystem.setTextColor(Color.BLACK);
			//rbMy.setTextColor(Color.parseColor("#888888"));
			anit(0,screenWidth);
			viewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}
    
	private void anit(int startX,int endX ){
		TranslateAnimation anim = new TranslateAnimation(startX, endX, 0, 0);
		anim.setDuration(600);
		anim.setFillAfter(true);
		img1.startAnimation(anim);
	}
	
	
	public void doClose(String cityName){
		Intent intent=new Intent(CityActivity.this,RegistDetailsActivity.class);
		intent.putExtra("name", cityName);
		setResult(RESULT_OK,intent);
		finish();
	}
	
	public void doOpen(Province prov){
		Intent intent=new Intent(this,DistrictActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("prov", prov);
		intent.putExtras(bundle);
		startActivityForResult(intent, requestCode);
	}

	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK && reqCode==requestCode){
			String cityName=data.getStringExtra("cityName");
			doClose(cityName);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
}
