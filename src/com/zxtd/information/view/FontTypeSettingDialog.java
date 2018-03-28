package com.zxtd.information.view;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class FontTypeSettingDialog extends Dialog {
	private FontTypeCallback fontTypeCallback;
	private RadioButton largeRadio;
	private RadioButton middleRadio;
	private RadioButton smallRadio;
	private Button cancelBotton;
	private Button confirmBotton;
	private String fontTypeStr = null;
	private String tempFontType = null;
	
	private android.view.View.OnClickListener onClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(v.getId() == R.id.font_setting_cancel){
				fontTypeCallback.refreshFontTypeTextView(fontTypeStr);
				FontTypeSettingDialog.this.cancel();
			}else{
				fontTypeCallback.refreshFontTypeTextView(tempFontType);
				FontTypeSettingDialog.this.dismiss();
				if(fontTypeStr != tempFontType){
					Utils.save(FontTypeSettingDialog.this.getContext(), Constant.DataKey.FONTTYPESIZE, tempFontType);
				}
			}
		}
	};
	
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.large_font_id){
				if(isChecked){
					tempFontType = "大号";
					largeRadio.setChecked(true);
					middleRadio.setChecked(false);
					smallRadio.setChecked(false);
				}
			}else if(buttonView.getId() == R.id.middle_font_id){
				if(isChecked){
					tempFontType = "中号";
					middleRadio.setChecked(true);
					largeRadio.setChecked(false);
					smallRadio.setChecked(false);
				}
			}else{
				if(isChecked){
					tempFontType = "小号";
					smallRadio.setChecked(true);
					largeRadio.setChecked(false);
					middleRadio.setChecked(false);
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.font_setting_dialog);
		setDialogTypeShow();
		
		largeRadio = (RadioButton)findViewById(R.id.large_font_id);
		middleRadio = (RadioButton)findViewById(R.id.middle_font_id);
		smallRadio = (RadioButton)findViewById(R.id.small_font_id);
		cancelBotton = (Button)findViewById(R.id.font_setting_cancel);
		confirmBotton = (Button)findViewById(R.id.font_setting_confirm);
		
		largeRadio.setOnCheckedChangeListener(listener);
		middleRadio.setOnCheckedChangeListener(listener);
		smallRadio.setOnCheckedChangeListener(listener);
		
		cancelBotton.setOnClickListener(onClickListener);
		confirmBotton.setOnClickListener(onClickListener);
		
		if(fontTypeStr.equals("大号")){
			largeRadio.setChecked(true);
		}else if(fontTypeStr.equals("中号")){
			middleRadio.setChecked(true);
		}else{
			smallRadio.setChecked(true);
		}
	}
	
	private void setDialogTypeShow() {
		LayoutParams params = this.getWindow().getAttributes();
		params.width = Utils.dipToPx(300);
		params.height = Utils.dipToPx(280);
		
		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}

	@Override
	public void show() {
		super.show();
	}
	
	public FontTypeSettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	
	public FontTypeSettingDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public FontTypeSettingDialog(Context context, FontTypeCallback typeCallback, String fontType) {
		super(context);
		fontTypeCallback = typeCallback;
		fontTypeStr = fontType;
		tempFontType = fontType;
	}
	
	public interface FontTypeCallback {
		public void refreshFontTypeTextView(String text);
	}
}
