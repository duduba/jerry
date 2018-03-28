package com.zxtd.information.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.view.MineCommentListCreater;
import com.zxtd.information.view.MineCommentListCreater.OnRefreshListener;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MineCommentAdapter extends PagerAdapter {
	private List<CommentBean> mineCommentList;
	private List<CommentBean> mineReplyList;
	private Context mContext;
	private LayoutInflater mInflater;
	private OnRefreshListener mOnRefreshListener;
	private Map<String, MineCommentListCreater> mineCreaterMaps;
	public MineCommentAdapter(Context context, List<CommentBean> mineCommentList, List<CommentBean> mineReplyList){
		mContext = context;
		this.mineCommentList = mineCommentList;
		this.mineReplyList = mineReplyList;
		mInflater = LayoutInflater.from(mContext);
		mineCreaterMaps = new HashMap<String, MineCommentListCreater>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View)object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		List<CommentBean> commentBeans = null;
		if(position == 0){
			commentBeans = mineCommentList;
		}else{
			commentBeans = mineReplyList;
		}
		MineCommentListCreater listCreater = new MineCommentListCreater(commentBeans, position);
		listCreater.setOnRefreshListener(mOnRefreshListener);
		View view = listCreater.createView(mInflater);
		container.addView(view, 0);
		mineCreaterMaps.put("" + position, listCreater);
		return view;
	}
	
	public void updateListAdapter(int position){
		MineCommentListCreater listCreater = mineCreaterMaps.get("" + position);
		if(listCreater != null){
			listCreater.updateAdapter();
		}
	}
	
	public void setCanLoading(int position, boolean isLoading){
		MineCommentListCreater listCreater = mineCreaterMaps.get("" + position);
		if(listCreater != null){
			listCreater.setCanLoading(isLoading);
		}
	}
	
	public void onRefreshComplete(int position){
		MineCommentListCreater listCreater = mineCreaterMaps.get("" + position);
		if(listCreater != null){
			listCreater.onRefreshComplete();
		}
	}
	
	public void setOnRefreshListener(OnRefreshListener onRefreshListener){
		mOnRefreshListener = onRefreshListener;
	}
}
