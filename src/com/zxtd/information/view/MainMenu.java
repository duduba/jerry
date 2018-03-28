package com.zxtd.information.view;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.bean.MenuBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainMenu extends PopupWindow {
	private GridView mGridView;
	public MainMenu(Context context){
		super(context);
		init(context);
	}
	
	private void init(Context context){
		
		setMenuStyle(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.main_menu, null);
		mGridView = (GridView) view.findViewById(R.id.menu_grid);
		List<MenuBean> menuBeans = new ArrayList<MenuBean>();
		menuBeans.add(new MenuBean("设置", R.drawable.icon_setting));
		//menuBeans.add(new MenuBean("下载管理", R.drawable.icon_setting));
		menuBeans.add(new MenuBean("退出", R.drawable.icon_exit));
		MenuAdapter menuAdapter = new MenuAdapter(inflater, menuBeans);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setAdapter(menuAdapter);
		
		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(new View.OnKeyListener() {  
            
            public boolean onKey(View v, int keyCode, KeyEvent event) {  
                  
                if(keyCode == KeyEvent.KEYCODE_MENU && isShowing()){  
                    dismiss();  
                    return true;  
                }  
                return false;  
            }  
        });  
		
		setContentView(view);
	}
	
	private void setMenuStyle(Context context){
		setWidth(LayoutParams.FILL_PARENT);  
        setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new ColorDrawable(0xff848484));
		setFocusable(true);
		setAnimationStyle(R.style.main_menu);
	}
	
	public void setOnMenuItemClickListener(OnItemClickListener onItemClickListener){
		if(onItemClickListener != null && mGridView != null){
			mGridView.setOnItemClickListener(onItemClickListener);
		}
	}
	
	class MenuAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private List<MenuBean> mMenuBeans;
		public MenuAdapter(LayoutInflater inflater,List<MenuBean> menuBeans){
			mInflater = inflater;
			mMenuBeans = menuBeans;
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return mMenuBeans.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mMenuBeans.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.main_menu_item, null);
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, Utils.dipToPx(60));
				arg1.setLayoutParams(params);
			}

			MenuBean menuBean = mMenuBeans.get(arg0);

			if (menuBean != null) {
				TextView title = (TextView) arg1
						.findViewById(R.id.menu_item_title);
				ImageView icon = (ImageView) arg1
						.findViewById(R.id.menu_item_icon);
				title.setText(menuBean.title);
				if (menuBean.icon != -1) {
					icon.setImageResource(menuBean.icon);
				}
			}
			return arg1;
		}
	}
	
}
