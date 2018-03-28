package com.zxtd.information.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.zxtd.information.ui.R;

public class PullToRefreshView extends PullToRefreshListView implements
		OnLastItemVisibleListener{
	private boolean isLoading = false;
	private boolean isLast = false;
	private OnRefreshListener2<ListView> mOnRefreshListener2;
	private TextView mFooterTextView;
	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		init();
	}

	public PullToRefreshView(Context context, Mode mode) {
		super(context, mode);
		init();
	}

	private void init(){
		this.setOnLastItemVisibleListener(this);
		this.setOnRefreshListener(mOnRefreshListener);
		View footerView = createFooterView();
		this.getRefreshableView().addFooterView(footerView);
	}
	
	public void canLoading(boolean isCan){
		isLast = !isCan;
		if(isCan){
			mFooterTextView.setText("正在加载中···");
		}else{
			mFooterTextView.setText("已加载全部");
		}
	}
	
	
	public PullToRefreshView(Context context) {
		super(context);
		init();
	}

	public void onLastItemVisible() {
		if(!isLoading && !isLast){
			isLoading = true;
			if(mOnRefreshListener2 != null){
				mOnRefreshListener2.onPullUpToRefresh(this);
			}
		}
	}
	
	public void onRefreshComplete2(){
		isLoading = false;
		onRefreshComplete();
	}
	
	@Override
	protected void onReset() {
		isLoading = false;
		super.onReset();
	}
	
	public final void setOnRefreshListener2(OnRefreshListener2<ListView> onRefreshListener2){
		mOnRefreshListener2 = onRefreshListener2;
	}
	
	private OnRefreshListener<ListView> mOnRefreshListener = new OnRefreshListener<ListView>() {

		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			if(mOnRefreshListener2 != null){
				mOnRefreshListener2.onPullDownToRefresh(refreshView);
			}
		}
	};
	
	private View createFooterView(){
		LayoutInflater inflater = LayoutInflater.from(this.getContext());
		View view = inflater.inflate(R.layout.no_more, null);
		mFooterTextView =  (TextView)view.findViewById(R.id.no_more);
		return view;
	}
	
	public void setFooterViewVisibile(boolean visible){
		if(visible){
			mFooterTextView.setVisibility(View.VISIBLE);
		}else{
			mFooterTextView.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected ListView createListView(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return new NewListView(context, attrs);
	}
}
