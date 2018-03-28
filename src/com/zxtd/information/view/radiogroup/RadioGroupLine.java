package com.zxtd.information.view.radiogroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class RadioGroupLine extends RelativeLayout {

	public RadioGroupLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RadioGroupLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RadioGroupLine(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private LayoutParams getParams(int verb){
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(verb, TRUE);
		params.addRule(CENTER_VERTICAL, TRUE);
		return params;
	}
	
	public void addRadioButton(View radioButton, int verb){
		switch (verb) {
		case 0:
			radioButton.setLayoutParams(getParams(ALIGN_PARENT_LEFT));
			break;
		case 1:
			radioButton.setLayoutParams(getParams(CENTER_HORIZONTAL));
			break;
		case 2:
			radioButton.setLayoutParams(getParams(ALIGN_PARENT_RIGHT));
			break;
		default:
			break;
		}
		this.addView(radioButton);
	}
}
