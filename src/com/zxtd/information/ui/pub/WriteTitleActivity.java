package com.zxtd.information.ui.pub;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.manager.NewTypeBeansManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.ExitDialog;
import com.zxtd.information.view.radiogroup.TextLeftRadioButton;
import com.zxtd.information.view.radiogroup.TextLeftRadioGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WriteTitleActivity extends BaseActivity implements OnClickListener{
	private TextLeftRadioGroup radioGroup;
	private EditText titleEdit;
	private Button btnBack;
	private Button btnFinish;
	private TextView titleText;
	
	private List<Bean> newTypeBeans;
	private int checkedNewType = 0;
	private boolean isContentChanged = false;
	private boolean isFirst = true;
	
	private ExitDialog mExitDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.write_news_title);
		initData();
		initView();
	}
	
	private void initData(){
		newTypeBeans = new ArrayList<Bean>();
		checkedNewType = this.getIntent().getIntExtra("newType", 0);
		initTypes();
	}
	
	private void initTypes(){
		List<Bean> temTypes = NewTypeBeansManager.getNewInstance().getNewTypeBeans();
		NewTypeBean temNewType = (NewTypeBean)temTypes.get(checkedNewType);
		for (Bean bean : temTypes) {
			NewTypeBean newTypeBean = (NewTypeBean)bean;
			if("10".equals(newTypeBean.flag)){
				if(newTypeBean == temNewType){
					checkedNewType = newTypeBeans.size();
				}
				newTypeBeans.add(newTypeBean);
			}
		}
	}

	private void initView(){
		titleText = (TextView) this.findViewById(R.id.title);
		btnBack = (Button) this.findViewById(R.id.back);
		btnFinish = (Button) this.findViewById(R.id.finish);
		titleEdit = (EditText)  this.findViewById(R.id.title_edit);
		radioGroup = (TextLeftRadioGroup) this.findViewById(R.id.pub_type_raido_group);
		radioGroup.setCheckedItem(checkedNewType);
		
		mExitDialog = new ExitDialog(this);
		
		radioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
		titleEdit.addTextChangedListener(mTextWatcher);
		btnBack.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		mExitDialog.setOnClickListener(mDialogClick);
		
		String newsTitle = this.getIntent().getStringExtra("title");
		initRadioGroup();
		titleText.setText(R.string.pub_news);
		titleEdit.setText(newsTitle);
		if(!Utils.isEmpty(newsTitle)){
			titleEdit.setSelection(newsTitle.length());
		}
		
	}
	//初始化新闻类型
	private void initRadioGroup(){
		int offset = 0;
		for(int i = 0; i < newTypeBeans.size(); i ++){
			NewTypeBean typeBean = (NewTypeBean) newTypeBeans.get(i);
			TextLeftRadioButton radioButton = new TextLeftRadioButton(this);
			radioButton.setText(typeBean.name);
			radioGroup.addRadioButton(radioButton, i - offset);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isContentChanged){
				mExitDialog.show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			checkedNewType = checkedId;
		}
	};
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(isFirst){
				isFirst = false;
				return;
			}
			isContentChanged = true;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	private DialogInterface.OnClickListener mDialogClick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == ExitDialog.CLICK_EXIT){
				back();
			}else if(which == ExitDialog.CLICK_FINISH){
				saveBack();
			}
		}
	};
	
	private void back(){
		this.setResult(RESULT_CANCELED);
		this.finish();
	}
	
	private void saveBack(){
		Intent intent = new Intent();
		intent.putExtra("title", titleEdit.getText().toString());
		NewTypeBean curTypeBean = (NewTypeBean)newTypeBeans.get(checkedNewType);
		intent.putExtra("newType", NewTypeBeansManager.getNewInstance().getNewTypeIndex(curTypeBean));
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
	
	@Override
	public void onClick(View v) {
		if(btnBack == v){
			if(isContentChanged){
				mExitDialog.show();
			}else{
				back();
			}
		}else if(btnFinish == v){
			saveBack();
		}
	}
}
