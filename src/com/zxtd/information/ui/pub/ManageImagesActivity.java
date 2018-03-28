package com.zxtd.information.ui.pub;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.adapter.ManageImagesAdapter;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.view.ImageViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ManageImagesActivity extends BaseActivity implements OnClickListener{
	
	private Button btnBack;
	private Button btnDelete;
	private TextView pageShowText;
	private ImageViewPager imageViewPager;
	
	private ManageImagesAdapter mImagesAdapter;
	
	private List<String> imagesPath;
	private int imageIndex = 0;
	
	private boolean isContentChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.manage_images_view);
		initData();
		initView();
	}
	
	private void initData(){
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			imagesPath = bundle.getStringArrayList("images");
			imageIndex = bundle.getInt( "index", 0);
		}
		mImagesAdapter = new ManageImagesAdapter(this, imagesPath);
	}
	
	private void setPageText(){
		pageShowText.setText((imageIndex + 1) + "/" + imagesPath.size());
	}
	
	private void initView(){
		btnBack = (Button) this.findViewById(R.id.btn_back);
		btnDelete = (Button) this.findViewById(R.id.btn_delete);
		pageShowText = (TextView) this.findViewById(R.id.page_show_text);
		imageViewPager = (ImageViewPager) this.findViewById(R.id.image_gallery);
		
		imageViewPager.setAdapter(mImagesAdapter);
		imageViewPager.setCurrentItem(imageIndex);
		
		setPageText();
		
		imageViewPager.setOnPageChangeListener(mOnPageChangeListener);
		btnBack.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
	}
	
	private void updateAdaper(){
		
		imageViewPager.setAdapter(new ManageImagesAdapter(this, new ArrayList<String>()));
		imageViewPager.setAdapter(mImagesAdapter);
		imageViewPager.setCurrentItem(imageIndex);
	}

	private void deleteImage(){
		imagesPath.remove(imageIndex);
		if(imageIndex == imagesPath.size()){
			imageIndex --;
		}
		updateAdaper();
		setPageText();
		isContentChanged = true;
		if(imagesPath.size() == 0){
			back();
		}
	}
	
	@Override
	public void onClick(View v) {
		if(btnBack == v){
			back();
		}else if(btnDelete == v){
			deleteImage();
		}
	}
	
	private void back(){
		Intent intent = new Intent();
		intent.putStringArrayListExtra("images", (ArrayList<String>)imagesPath);
		intent.putExtra("change", isContentChanged);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			imageIndex = arg0;
			setPageText();
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
