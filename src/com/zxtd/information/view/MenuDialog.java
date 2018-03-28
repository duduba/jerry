package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuDialog extends Dialog implements OnItemClickListener{
	private List<String> mValues = null;
	private OnMenuItemChangeListener mOnMenuItemChangeListener = null;
	private View mView;
	private Animation animWindowIn;
	private Animation animWindowOut;
	private ListView listView;
	
	private AnimationListener animationListener = new AnimationListener() {
		
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		public void onAnimationEnd(Animation animation) {
			superDismiss();
		}
	};
	
	public MenuDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public void superDismiss(){
		super.dismiss();
	}
	
	public MenuDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public MenuDialog(Context context) {
		super(context);
		init();
	}
	
	
	private void init(){
		mValues = new ArrayList<String>();
	}
	
	public void setMenuValues(List<String> values){
		mValues.clear();
		mValues.addAll(values);

	}
	
	public void setView(View view){
		mView = view;
	}
	
	public void setOnMenuItemChangeListener(OnMenuItemChangeListener onMenuItemChangeListener){
		mOnMenuItemChangeListener = onMenuItemChangeListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_list_dialog);
		
		setDialogStyle();
		
		listView = (ListView) this.findViewById(R.id.menu_list);
		
		animWindowIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.menu_diloag_in);
		animWindowOut = AnimationUtils.loadAnimation(this.getContext(), R.anim.menu_diloag_out);
		
		animWindowOut.setAnimationListener(animationListener);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), R.layout.menu_list_dialog_item, R.id.menu_list_item, mValues);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(mOnMenuItemChangeListener != null){
			mOnMenuItemChangeListener.menuItemChange(mValues.get(arg2), arg2);
		}
		this.dismiss();
	}
	
	public interface OnMenuItemChangeListener{
		void menuItemChange(String item, int index);
	}
	
	@Override
	public void dismiss() {
		listView.clearAnimation();
		listView.startAnimation(animWindowOut);
	}
	
	public void show() {
		if(mView != null){
			int[] location = new int[2];
			mView.getLocationOnScreen(location);
			setDiloagLocation(location[0], location[1] - Utils.barHeight + mView.getHeight());
		}
		super.show();
		listView.clearAnimation();
		listView.startAnimation(animWindowIn);
	}
	
	private void setDiloagLocation(int x, int y){
		LayoutParams params = this.getWindow().getAttributes();
		params.x = x;
		params.y = y;
		this.getWindow().setAttributes(params);
	}
	
	private void setDialogStyle() {
		
		this.setCanceledOnTouchOutside(true);

		LayoutParams params = this.getWindow().getAttributes();
		params.width = mView.getWidth(); // 设置对话框宽度
		params.height = Utils.dipToPx(264); // 设置对话框高度
		params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		params.gravity = Gravity.TOP|Gravity.LEFT;
		params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
	}
}
