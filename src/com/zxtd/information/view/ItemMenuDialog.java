package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemMenuDialog extends Dialog implements View.OnClickListener{

	private List<String> menuNams;
	
	private OnClickListener mOnClickListener;
	private int mWidth = 0;
	
	public ItemMenuDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public ItemMenuDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public ItemMenuDialog(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		menuNams = new ArrayList<String>();
	}
	
	public void addMenuItem(String menuName){
		menuNams.add(menuName);
	}
	
	public void addMenuItems(List<String> menuNams){
		this.menuNams.addAll(menuNams);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.item_menu_dialog);
		
		setDialogStyle();
		
		LinearLayout rootView = (LinearLayout)this.findViewById(R.id.item_nemu_root_view);
		addMenuItems(rootView);
	}

	private void addMenuItems(LinearLayout rootView){
		for(int i = 0; i < menuNams.size(); i++){
			View view = createItemView(menuNams.get(i));
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dipToPx(70), Utils.dipToPx(40));
			view.setLayoutParams(params);
			rootView.addView(view);
			view.setTag(R.id.tag_key_position, i);
			mWidth += Utils.dipToPx(70);
			view.setOnClickListener(this);
		}
	}
	
	private View createItemView(String menuName){
		TextView textView = new TextView(this.getContext());
		textView.setText(menuName);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		textView.setGravity(Gravity.CENTER);
		return textView;
	}
	
	private void setDiloagLocation(int x, int y){
		LayoutParams params = this.getWindow().getAttributes();
		params.x = x;
		params.y = y;
		this.getWindow().setAttributes(params);
	}
	
	public void show(View view) {
		super.show();
		if(view != null){
			int[] location = new int[2];
			view.getLocationOnScreen(location);
			setDiloagLocation(location[0] + (view.getWidth() - mWidth) / 2, location[1] - Utils.barHeight + (view.getHeight() - 64)/2);
		}
	}
	 
	public void onClick(View v) {
		this.dismiss();
		int position = (Integer)v.getTag(R.id.tag_key_position);
		if(mOnClickListener != null){
			mOnClickListener.onClick(this, position);
		}
	}
	private void setDialogStyle() {
		
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);

		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.WRAP_CONTENT; // 设置对话框宽度
		params.height = LayoutParams.WRAP_CONTENT; // 设置对话框高度
		params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		params.gravity = Gravity.TOP|Gravity.LEFT;
		params.softInputMode = LayoutParams.SOFT_INPUT_STATE_VISIBLE | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
}
