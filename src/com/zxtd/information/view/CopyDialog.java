package com.zxtd.information.view;

import com.zxtd.information.bean.InvitationReplyBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class CopyDialog extends Dialog implements android.view.View.OnClickListener{
	private InvitationReplyBean mReplyBean;
	private android.view.View.OnClickListener mOnClickListener;
	public CopyDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public CopyDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public CopyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.copy_view);
		
		setDialogStyle();
		
		TextView btnCopy = (TextView) this.findViewById(R.id.btn_copy);
		TextView btnReply = (TextView) this.findViewById(R.id.btn_reply);
		btnCopy.setOnClickListener(this);
		btnReply.setOnClickListener(this);
	}

	public void setReply(InvitationReplyBean replyBean){
		mReplyBean = replyBean;
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
			setDiloagLocation(location[0] + (view.getWidth() - 112) / 2, location[1] - Utils.barHeight + (view.getHeight() - 64)/2);
		}
	}
	 
	public void onClick(View v) {
		this.dismiss();
		int id = v.getId();
		if(id == R.id.btn_copy){
			if(mReplyBean != null){
				Utils.copy(this.getContext(), mReplyBean.content);
				Toast.makeText(getContext(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
			}
		}else if(id == R.id.btn_reply){
			if(mOnClickListener != null){
				v.setTag(mReplyBean);
				mOnClickListener.onClick(v);
			}
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
		params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	public void setOnClickListener(android.view.View.OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
}
