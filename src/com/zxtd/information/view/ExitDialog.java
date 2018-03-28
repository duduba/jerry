package com.zxtd.information.view;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class ExitDialog extends Dialog {
	private OnClickListener mOnClickListener;
	public static final int CLICK_EXIT = 0;
	public static final int CLICK_FINISH = 1;
	
	public ExitDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public ExitDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ExitDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exit_dialog);
		
		setDialogStyle();
		
		Button btnExit = (Button)this.findViewById(R.id.btn_exit);
		Button btnFinish = (Button) this.findViewById(R.id.btn_finish);
		
		btnExit.setOnClickListener(viewClick);
		btnFinish.setOnClickListener(viewClick);
	}
	
	private void setDialogStyle() {
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT; // 设置对话框宽度
		params.height = Utils.dipToPx(267); // 设置对话框高度
		//params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		//params.gravity = Gravity.TOP|Gravity.LEFT;
		params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00ffffff));
	}
	
	private View.OnClickListener viewClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mOnClickListener == null){
				ExitDialog.this.dismiss();
				return;
			}
			int id = v.getId();
			if(id == R.id.btn_exit){
				mOnClickListener.onClick(ExitDialog.this, CLICK_EXIT);
			}else if(id == R.id.btn_finish){
				mOnClickListener.onClick(ExitDialog.this, CLICK_FINISH);
			}
			ExitDialog.this.dismiss();
		}
	};
	
}
