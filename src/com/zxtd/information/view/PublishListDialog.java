package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.adapter.PublishListAdapter;
import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PublishListDialog extends Dialog{
	private List<PublishBean> mPublishBeans;
	private PublishListAdapter mListAdapter;
	
	private ListView pubListView;
	private Button btnDialogDismiss;
	private Button btnPubEditable;
	private TextView hasNoPublishText;
	
	private boolean isCanDelete = false;
	
	private OnPublishItemSelectedListener mOnPublishItemSelectedListener = null;
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(mOnPublishItemSelectedListener != null){
				mOnPublishItemSelectedListener.onPublishItemSelected(mPublishBeans.get(arg2));
			}
			PublishListDialog.this.dismiss();
		}
	};
	
	private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener() {
		
		public void onClick(View v) {
			if(v == btnDialogDismiss){
				PublishListDialog.this.dismiss();
			}else if(v == btnPubEditable){
				isCanDelete = !isCanDelete;
				changeBtnPubEditableText();
				mListAdapter.setCanDelete(isCanDelete);
				mListAdapter.notifyDataSetChanged();
			}
			
		}
	};
	
	private void changeBtnPubEditableText(){
		if(isCanDelete){
			btnPubEditable.setText("取消");
		}else{
			btnPubEditable.setText("编辑");
		}
	}
	
	public PublishListDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public PublishListDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public PublishListDialog(Context context) {
		super(context);
		init();
	}
	
	public void setOnPublishItemSelectedListener(OnPublishItemSelectedListener publishItemSelectedListener){
		mOnPublishItemSelectedListener = publishItemSelectedListener;
	}
	
	private void init(){
		mPublishBeans = new ArrayList<PublishBean>();
	}
	
	public void setPublishBeans(List<PublishBean> publishBeans){
		mPublishBeans.clear();
		if(publishBeans != null){
			mPublishBeans.addAll(publishBeans);
		}
		if(mListAdapter != null){
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.pub_list_dialog);
		
		setDialogStyle();
		
		pubListView = (ListView) this.findViewById(R.id.pub_list);
		btnDialogDismiss = (Button) this.findViewById(R.id.btn_pub_list_dialog_dismiss);
		btnPubEditable = (Button) this.findViewById(R.id.btn_publish_editable);
		hasNoPublishText = (TextView) this.findViewById(R.id.has_no_publish_text);
		
		mListAdapter = new PublishListAdapter(this.getContext(), mPublishBeans);
		pubListView.setAdapter(mListAdapter);
		
		pubListView.setOnItemClickListener(itemClickListener);
		btnDialogDismiss.setOnClickListener(clickListener);
		btnPubEditable.setOnClickListener(clickListener);
		
		
	}
	@Override
	public void show() {
		isCanDelete = false;
		super.show();
		mListAdapter.setCanDelete(isCanDelete);
		changeBtnPubEditableText();
		if(mPublishBeans.size() == 0){
			hasNoPublishText.setVisibility(View.VISIBLE);
		}else{
			hasNoPublishText.setVisibility(View.GONE);
		}
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
	
	public interface OnPublishItemSelectedListener{
		void onPublishItemSelected(PublishBean publishBean);
	}
}
