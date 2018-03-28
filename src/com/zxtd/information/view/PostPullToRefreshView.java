package com.zxtd.information.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PostPullToRefreshView extends PullToRefreshView {

	public PostPullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PostPullToRefreshView(
			Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,
			com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
		// TODO Auto-generated constructor stub
	}

	public PostPullToRefreshView(Context context,
			com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
		// TODO Auto-generated constructor stub
	}

	public PostPullToRefreshView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		PostListView listView = new PostListView(context, attrs);
		listView.setId(android.R.id.list);
		return listView;
	}
	
}
