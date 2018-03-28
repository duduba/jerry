package com.zxtd.information.ui.pub;

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
import android.widget.TextView;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.ExitDialog;

public class WriteContentActivity extends BaseActivity implements OnClickListener{
	private TextView titleText;
	private Button btnBack;
	private Button btnFinish;
	private EditText contentEdit;
	
	private boolean isContentChanged = false;
	private boolean isFirst = true;
	
	private ExitDialog mExitDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.write_news_content);
		initData();
		initView();
	}
	
	private void initData(){
		
	}
	
	private void initView(){
		btnBack = (Button) this.findViewById(R.id.back);
		btnFinish = (Button) this.findViewById(R.id.finish);
		titleText = (TextView) this.findViewById(R.id.title);
		contentEdit = (EditText) this.findViewById(R.id.pub_news_content_edit);
		
		mExitDialog = new ExitDialog(this);
		
		btnBack.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		contentEdit.addTextChangedListener(mTextWatcher);
		mExitDialog.setOnClickListener(mDialogClick);
		
		String newsContent = this.getIntent().getStringExtra("content");
		titleText.setText(R.string.pub_news);
		contentEdit.setText(newsContent);
		if(!Utils.isEmpty(newsContent)){
			contentEdit.setSelection(newsContent.length());
		}
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
		intent.putExtra("content", contentEdit.getText().toString());
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
	
}
