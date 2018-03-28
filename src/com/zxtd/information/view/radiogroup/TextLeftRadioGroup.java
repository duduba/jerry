package com.zxtd.information.view.radiogroup;

import java.util.ArrayList;

import com.zxtd.information.util.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TextLeftRadioGroup extends LinearLayout {
	private RadioGroupLine mLine;
	private int checkedPos = -1;
	private ArrayList<TextLeftRadioButton> mLeftRadioButtons = new ArrayList<TextLeftRadioButton>();
	private OnCheckedChangeListener mCheckedChangeListener;
	public TextLeftRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextLeftRadioGroup(Context context) {
		super(context);
	}
	//设置监听
	public void setOnCheckedChangeListener(OnCheckedChangeListener checkedChangeListener){
		mCheckedChangeListener = checkedChangeListener;
	}
	
	public void addRadioButton(TextLeftRadioButton leftRadioButton, int index){
		int verb = index % 3;
		if(verb == 0){
			mLine = createLine();
			this.addView(mLine);
		}
		View radioView = leftRadioButton.getView();
		radioView.setTag(index);
		radioView.setOnClickListener(mOnClickListener);
		mLine.addRadioButton( radioView, verb);
		mLeftRadioButtons.add(leftRadioButton);
		if(index == checkedPos){
			setCheckedItem(checkedPos);
		}
	}
	
	public void setCheckedItem(int position){
		if(position >= 0 && mLeftRadioButtons.size() > position ){
			TextLeftRadioButton radioButton = mLeftRadioButtons.get(position);
			radioButton.setChecked(true);
			if(checkedPos != -1 && checkedPos != position){
				TextLeftRadioButton checkedRadio = mLeftRadioButtons.get(checkedPos);
				checkedRadio.setChecked(false);
			}
			if(mCheckedChangeListener != null){
				mCheckedChangeListener.onCheckedChanged(null, position);
			}
		}
		checkedPos = position;
	}
	//创建行
	private RadioGroupLine createLine(){
		RadioGroupLine line = new RadioGroupLine(this.getContext());
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		line.setLayoutParams(params);
		line.setPadding(0, Utils.dipToPx(14), 0, Utils.dipToPx(14));
		return line;
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int pos = (Integer)v.getTag();
			if(pos != checkedPos){
				setCheckedItem(pos);
			}
		}
	};
}
