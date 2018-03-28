package com.zxtd.information.view;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewInfoMenuDialog extends Dialog {
	private OnClickListener mOnClickListener = null;
	private String[] menuItems;
	private NewInfoMenuAdapter menuAdapter;
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(mOnClickListener != null){
				mOnClickListener.onClick(NewInfoMenuDialog.this, arg2);
			}
			NewInfoMenuDialog.this.dismiss();
		}
	};
	
	public NewInfoMenuDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public NewInfoMenuDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public NewInfoMenuDialog(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		menuItems = new String[2];
		menuItems[0] = "分享";
		menuItems[1] = "收藏";
	}
	
	public void setCollected(boolean isCollected){
		if(isCollected){
			menuItems[1] = "取消收藏";
		}else{
			menuItems[1] = "收藏";
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.new_info_menu_list);
		setDialogStyle();
		ListView listView = (ListView)this.findViewById(R.id.menu_list);
		menuAdapter = new NewInfoMenuAdapter(this.getContext(), new int[]{R.drawable.share_icon, R.drawable.collect_icon}, menuItems);
		listView.setAdapter(menuAdapter);
		listView.setOnItemClickListener(mItemClickListener);
	}
	
	@Override
	public void show() {
		super.show();
		if(menuAdapter != null){
			menuAdapter.notifyDataSetChanged();
		}
	}
	
	public void setDiloagLocation(int x, int y){
		LayoutParams params = this.getWindow().getAttributes();
		params.x = x;
		params.y = y - Utils.barHeight;
		this.getWindow().setAttributes(params);
	}
	
	private void setDialogStyle() {
		
		this.setCanceledOnTouchOutside(true);
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = Utils.dipToPx(125); // 设置对话框宽度
		params.height = LayoutParams.WRAP_CONTENT; // 设置对话框高度
		params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		params.gravity = Gravity.TOP|Gravity.RIGHT;
		//params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
	
	public void setOnClickListener(OnClickListener clickListener){
		mOnClickListener = clickListener;
	}
}
class NewInfoMenuAdapter extends BaseAdapter{
	private int[] mRess;
	private String[] mStrings;
	private LayoutInflater mInflater;
	public NewInfoMenuAdapter(Context context, int[] ress, String[] strings){
		mRess = ress;
		mStrings = strings;
		mInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return mStrings.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mStrings[arg0];
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		arg1 = mInflater.inflate(R.layout.new_info_menu_list_item, null);
		ImageView itemIcon = (ImageView)arg1.findViewById(R.id.item_icon);
		TextView itemText = (TextView) arg1.findViewById(R.id.item_text);
		itemIcon.setImageResource(mRess[arg0]);
		itemText.setText(mStrings[arg0]);
		return arg1;
	}
}
