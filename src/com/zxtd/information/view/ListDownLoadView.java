package com.zxtd.information.view;

import com.zxtd.information.ui.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ListDownLoadView {
	private ListView mListView;
	private TextView mFootView;
	private boolean isLock = false;
	private boolean isLoading = false;
	private boolean onScrollLock = true;
	private OnFooterRefreshListener mFooterRefreshListener;
	public ListDownLoadView(ListView listView){
		mListView = listView;
		mListView.addFooterView(createFooter());
	}
	
	private View createFooter(){
		LayoutInflater inflater = LayoutInflater.from(mListView.getContext());
		View view = inflater.inflate(R.layout.no_more, null);
		mFootView =  (TextView)view.findViewById(R.id.no_more);
		return view;
	}
	
	public void onScrollState(int state){
		if(state != 0){
			onScrollLock = false;
		}else{
			onScrollLock = true;
		}
	}
	
	public void onScroll(int firstVisibleItem,
			int visibleItemCount, int totalItemCount){
		if(!isLock && mFooterRefreshListener != null && !isLoading && !onScrollLock){
			if(visibleItemCount != mListView.getChildCount() || firstVisibleItem + visibleItemCount < totalItemCount - 1 ){
				return;
			}
			int bottom = mListView.getChildAt(visibleItemCount - 1).getBottom();
			if(bottom <= mListView.getHeight() + 10){
				mFooterRefreshListener.onFooterRefresh();
				isLoading = true;
			}
		}
	}
	
	public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener){
		this.mFooterRefreshListener = footerRefreshListener;
	}
	
	public void hasMore(){
		isLock = false;
		mFootView.setText("正在加载中···");
	}
	
	public void noMore(){
		isLock = true;
		mFootView.setText("已加载全部");
	}
	
	public void completeLoading(){
		isLoading = false;
	}
	
	public interface OnFooterRefreshListener {
		public void onFooterRefresh();
	}
	
	public void hideFooter(){
		mFootView.setVisibility(View.GONE);
	}
	
	public void showFooter(){
		mFootView.setVisibility(View.VISIBLE);
	}
}
