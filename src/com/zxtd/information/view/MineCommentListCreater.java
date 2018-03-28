package com.zxtd.information.view;

import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zxtd.information.adapter.MineCommentListAdapter;
import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.InvitationReplyParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MineCommentListCreater implements OnRefreshListener2<ListView>, OnItemClickListener, DialogInterface.OnClickListener{
	private View mView;
	private ListView mListView;
	private PullToRefreshView refreshView;
	private LinearLayout edtPost;
	private EditText edtPostContent;
	private Context mContext;
	private List<CommentBean> mCommentBeans;
	private int position = -1;
	private OnRefreshListener mOnRefreshListener;
	private ItemMenuDialog mItemMenuDialog;
	private BaseAdapter mAdapter;
	private InputMethodManager methodManager;
	private CommentBean curBean = null;
	
	public MineCommentListCreater(List<CommentBean> commentBeans, int position){
		mCommentBeans = commentBeans;
		this.position = position;
	}
	
	public View createView(LayoutInflater inflater){
		if(mView == null){
			mView = inflater.inflate(R.layout.mine_commment_creater_view, null);
			mContext = inflater.getContext();
			
			refreshView = (PullToRefreshView) mView.findViewById(R.id.new_pull_to_refresh);
			mListView = refreshView.getRefreshableView();
			edtPost = (LinearLayout) mView.findViewById(R.id.edt_post);
			edtPostContent = (EditText) mView.findViewById(R.id.new_edit_post);
			Button btnPost = (Button) mView.findViewById(R.id.new_btn_post);
			
			mAdapter = creatAdapter();
			mListView.setAdapter(mAdapter);
			
			mItemMenuDialog = new ItemMenuDialog(mContext);
			
			refreshView.canLoading(true);
			refreshView.setOnRefreshListener2(this);
			mListView.setOnItemClickListener(this);
			mItemMenuDialog.setOnClickListener(this);
			btnPost.setOnClickListener(onClickPost);
			
			mItemMenuDialog.addMenuItem("复制");
			mItemMenuDialog.addMenuItem("查看原文");
			if(position == 1){
				mItemMenuDialog.addMenuItem("回复");
			}
			setPostViewVisible(View.GONE);
			methodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mView; 
	}
	
	public void setPostViewVisible(int visibility){
		edtPost.setVisibility(visibility);
	}
	
	public PullToRefreshView getPullToRefreshView(){
		return refreshView;
	}
	
	public void setOnRefreshListener(OnRefreshListener onRefreshListener){
		this.mOnRefreshListener = onRefreshListener;
	}
	
	protected BaseAdapter creatAdapter() {
		return new MineCommentListAdapter(mContext, mCommentBeans);
	}
	
	public interface OnRefreshListener{
		void doRefresh(int position);
		void doLoad(int position);
	}
	
	public void updateAdapter(){
		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void setCanLoading(boolean loading){
		refreshView.canLoading(loading);
	}
	
	public void onRefreshComplete(){
		refreshView.onRefreshComplete2();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		this.refreshView.canLoading(true);
		if(mOnRefreshListener != null){
			mOnRefreshListener.doRefresh(position);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(mOnRefreshListener != null){
			mOnRefreshListener.doLoad(position);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object obj = arg0.getAdapter().getItem(arg2);
		if(obj != null && obj instanceof CommentBean){
			curBean = (CommentBean)obj;
			mItemMenuDialog.show(arg1);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case 0:
			Utils.copy(mContext, curBean.getContent());
			Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			Intent intent = new Intent(mContext, NewInfoActivity.class);
			NewBean newBean = new NewBean();
			newBean.newId = curBean.getNewsId() + "";
			newBean.infoUrl = curBean.getInfoUrl();
			//CurrentManager.newInstance().setCurrentBean(newBean);
			if("0".equals(curBean.getType())){
				intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.NORMAL_TYPE);
			}else{
				intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.NET_FRIEND_TYPE);
			}
			intent.putExtra(Constant.BundleKey.NEWBEAN, newBean);
			mContext.startActivity(intent);
			break;
		case 2:
			setPostViewVisible(View.VISIBLE);
			edtPostContent.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					showSoftKeyboard();
				}
			}, 100);
			break;
		}
	}
	public void hideSoftKeyboard(){
		methodManager.hideSoftInputFromWindow(edtPostContent.getWindowToken(), 0);
	}
	
	public void showSoftKeyboard(){
		edtPostContent.requestFocus();
		methodManager.showSoftInput(edtPostContent, 0);
	}
	
	private OnClickListener onClickPost = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String msg = edtPostContent.getText().toString();
			if("0".equals(curBean.getType())){
				RequestManager.newInstance().invitationReplyComm(curBean.getNewsId() + "", curBean.getCommentId() + "", XmppUtils.getUserId() + "", msg, Constant.Flag.NORMAL_TYPE, mCallBack);
			}else{
				RequestManager.newInstance().invitationReplyComm(curBean.getNewsId() + "", curBean.getCommentId() + "", XmppUtils.getUserId() + "", msg, Constant.Flag.NET_FRIEND_TYPE, mCallBack);
			}
			hideSoftKeyboard();
			edtPostContent.setText("");
		}
	};
	
	private RequestCallBack mCallBack = new RequestCallBack() {
		
		@Override
		public void requestSuccess(String requestCode, Result result) {
			boolean isSuccess = result.getBoolean(InvitationReplyParseData.IS_SUCCESS);
			if(isSuccess){
				Toast.makeText(mContext, "发布成功！", Toast.LENGTH_SHORT).show();
				onPullDownToRefresh(null);
			}else{
				Toast.makeText(mContext, "发布失败！", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		public void requestError(String requestCode, int errorCode) {
			Toast.makeText(mContext, "网络不给力！", Toast.LENGTH_SHORT).show();
		}
	};
}
