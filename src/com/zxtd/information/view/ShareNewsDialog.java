package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;
import com.zxtd.information.ui.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShareNewsDialog extends Dialog {

	private OnClickListener mOnClickListener = null;

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(mOnClickListener != null){
				mOnClickListener.onClick(ShareNewsDialog.this, arg2);
			}
			ShareNewsDialog.this.dismiss();
		}
	};
	
	public ShareNewsDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public ShareNewsDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ShareNewsDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.share_news_dialog);
		
		setDialogStyle();
		
		GridView gridView = (GridView) this.findViewById(R.id.share_grid);
		ShareGridAdapter adapter = new ShareGridAdapter(getData(), getContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(mItemClickListener);
	}
	
	private List<Item> getData(){
		ArrayList<Item> items = new ArrayList<ShareNewsDialog.Item>();
		Item item;
		
		item = new Item();
		item.name = "新浪微博";
		item.icon = R.drawable.sina_icon;
		items.add(item);
		
		item = new Item();
		item.name = "腾讯微博";
		item.icon = R.drawable.qq_weibo_icon;
		items.add(item);
		
		item = new Item();
		item.name = "微信";
		item.icon = R.drawable.weixin_icon;
		items.add(item);
		
		item = new Item();
		item.name = "朋友圈";
		item.icon = R.drawable.pengyouquan_icon;
		items.add(item);
		
		item = new Item();
		item.name = "QQ空间";
		item.icon = R.drawable.qq_kongjian_icon;
		items.add(item);
		
		item = new Item();
		item.name = "人人网";
		item.icon = R.drawable.renren_icon;
		items.add(item);
		
		return items;
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
	
	static class ShareGridAdapter extends BaseAdapter{
		private List<Item> mItems;
		private LayoutInflater mInflater;
		public ShareGridAdapter(List<Item> items, Context context){
			this.mItems = items;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = mInflater.inflate(R.layout.share_news_dialog_item, null);
			TextView title = (TextView)view.findViewById(R.id.share_item_text);
			ImageView icon = (ImageView)view.findViewById(R.id.share_item_icon);
			
			Item item = mItems.get(arg0);
			
			title.setText(item.name);
			icon.setImageResource(item.icon);
			return view;
		}
	}
	
	static class Item{
		String name;
		int icon;
	}
	
}
