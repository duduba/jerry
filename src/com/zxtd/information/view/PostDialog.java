package com.zxtd.information.view;

import com.zxtd.information.manager.UserNickManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.view.KeyboardListenLinearLayout.OnSoftKeyboardStateListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostDialog extends Dialog implements android.view.View.OnClickListener, OnSoftKeyboardStateListener{
	private Button btnPost;
	private EditText editPostContent;
	
	private OnPostContentListener mContentListener;
	
	private String mNewId;
	
	private String mInvitationId = null;
	
	private InputMethodManager methodManager;
	
	private boolean isHide = true;
	
	private OnCancelListener mCancelListener;
	
	private boolean isInited = false;
	
	public PostDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public PostDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public PostDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setHint(String hint){
		if(editPostContent != null){
			editPostContent.setHint(hint);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.post_dialog);
		setDialogStyle();
		
		methodManager = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		btnPost = (Button) this.findViewById(R.id.new_btn_post);
		editPostContent = (EditText) this.findViewById(R.id.new_edit_post);
		
		btnPost.setOnClickListener(this);
		
		isInited = true;
		//editPostContent.setOnFocusChangeListener(this);
		
	}
	
	
	@Override
	public void cancel() {
		if(isHide){
			if(mCancelListener != null){
				mCancelListener.onCancel(this);
			}
		}else{
			hideSoftKeyboard();
		}
	}
	
	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		mCancelListener = listener;
	}
	
	public void setNewBean(String newId){
		mNewId = newId;
	}
	
	public void setNiceName(String niceName){
	}
	
	public void setInvitationId(String invitationId){
		mInvitationId = invitationId;
	}
	
	private void setDialogStyle() {
		
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(true);
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT; // 设置对话框宽度
		params.height = LayoutParams.WRAP_CONTENT; // 设置对话框高度
		params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		params.gravity = Gravity.BOTTOM;
		params.softInputMode = LayoutParams.SOFT_INPUT_STATE_HIDDEN | LayoutParams.SOFT_INPUT_ADJUST_PAN;
		

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}

	public void setOnPostContentListener(OnPostContentListener contentListener){
		mContentListener = contentListener;
	}
	
	
	private void changeAttributes(boolean hasFocus){
		if(hasFocus){
			LayoutParams params = this.getWindow().getAttributes();
			params.dimAmount = 0.8f;
			this.getWindow().clearFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL);
			this.getWindow().setAttributes(params);
		}else{
			LayoutParams params = this.getWindow().getAttributes();
			params.dimAmount = 0.0f;
			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
			this.getWindow().setAttributes(params);
			editPostContent.setHint("我来说几句");
		}
		
	}
	
	public void onClick(View v) {
		if(v == btnPost && mContentListener != null && mNewId != null){
			String text = editPostContent.getText().toString();
			if(text != null && !"".equals(text.trim())){
				if(mInvitationId != null && !mInvitationId.equals("")){
					if(mContentListener.onPostContent(text, mNewId, UserNickManager.getNewInstance().getNickName(), mInvitationId)){
						editPostContent.setText("");
						hideSoftKeyboard();
					}
				}else{
					if(mContentListener.onPostContent( text, mNewId, UserNickManager.getNewInstance().getNickName())){
						editPostContent.setText("");
						hideSoftKeyboard();
					}
				}
				
			}else{
				Toast.makeText(this.getContext(), "请输入内容！", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

	public interface OnPostContentListener{
		/**
		 *跟帖
		 *text 跟帖类容
		 * */
		boolean onPostContent(String text, String newId, String niceName);
		boolean onPostContent(String text, String newId, String niceName, String invitationId);
	}

	public void onStateChanged(int state) {
		
		if(!isInited){
			return;
		}
		
		if(state == KeyboardListenLinearLayout.SOFT_KEY_BOARD_HIDE){
			mInvitationId = null;
			if(editPostContent.isFocused()){
				editPostContent.clearFocus();
			}
			changeAttributes(false);
			btnPost.setVisibility(View.GONE);
			isHide = true;
		}else{
			if(!editPostContent.isFocused()){
				editPostContent.requestFocus();
			}
			btnPost.setVisibility(View.VISIBLE);
			changeAttributes(true);
			isHide = false;
		}
	}


	
	public void hideSoftKeyboard(){
		methodManager.hideSoftInputFromWindow(editPostContent.getWindowToken(), 0);
	}
	
	public void showSoftKeyboard(){
		editPostContent.requestFocus();
		methodManager.showSoftInput(editPostContent, 0);
	}
	
	public boolean isSoftKeyboardHide(){
		return isHide;
	}
}
