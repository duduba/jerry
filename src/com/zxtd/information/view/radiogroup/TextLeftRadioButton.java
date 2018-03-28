package com.zxtd.information.view.radiogroup;

import com.zxtd.information.ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class TextLeftRadioButton {
	private Context mContext;
	private RadioButton mRadioButton;
	private TextView mTitle;
	private View mView;
	public TextLeftRadioButton(Context context){
		mContext = context;
		init();
	}
	
	private void init(){
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		mView = mInflater.inflate(R.layout.text_left_radio_button, null);
		mRadioButton = (RadioButton) mView.findViewById(R.id.l_radio_button);
		mTitle = (TextView) mView.findViewById(R.id.l_radio_text);
	}
	
	public TextView getTextView(){
		return mTitle;
	}
	
	public RadioButton getRadioButton(){
		return mRadioButton;
	}
	
	public void setText(String text){
		mTitle.setText(text);
	}
	
	public void setChecked(boolean checked){
		mRadioButton.setChecked(checked);
	}
	
	public View getView(){
		return mView;
	}
	
	public Context getContext(){
		return mContext;
	}
	
}
