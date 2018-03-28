package com.zxtd.information.view;

import com.zxtd.information.ui.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PictureDialog extends Dialog{
	private OnClickListener mOnClickListener = null;

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(mOnClickListener != null){
				mOnClickListener.onClick(PictureDialog.this, arg2);
			}
			PictureDialog.this.dismiss();
		}
	};
	
	public PictureDialog(Context context){
		super(context);
		init();
	}
	
	public PictureDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public PictureDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public void init(){
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.picture_dialog);
		
		setDialogStyle();
		
		ListView listView = (ListView) this.findViewById(R.id.get_picture_method);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.picture_dialog_item, R.id.picture_item_text, new String[]{"本地相册","相机照相"});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(mItemClickListener);
	}
	
	private void setDialogStyle() {
		
		this.setCanceledOnTouchOutside(true);
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT; // 设置对话框宽度
		params.height = LayoutParams.WRAP_CONTENT; // 设置对话框高度
		//params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		//params.gravity = Gravity.BOTTOM;
		//params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
	
	public void setOnClickListener(OnClickListener clickListener){
		mOnClickListener = clickListener;
	}
	
}
