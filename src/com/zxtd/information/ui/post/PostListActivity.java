package com.zxtd.information.ui.post;

import java.util.ArrayList;
import java.util.List;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zxtd.information.adapter.PostListAdpater;
import com.zxtd.information.adapter.PostListAdpater.OnCopyContentListener;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.InvitationCheckedBean;
import com.zxtd.information.bean.InvitationReplyBean;
import com.zxtd.information.listener.OnLeftOrRightListener;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.InvitationCheckedParseData;
import com.zxtd.information.parse.InvitationReplyParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.LoginActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import com.zxtd.information.view.CopyDialog;
import com.zxtd.information.view.KeyboardListenLinearLayout;
import com.zxtd.information.view.PostDialog;
import com.zxtd.information.view.PostDialog.OnPostContentListener;
import com.zxtd.information.view.PostListView;
import com.zxtd.information.view.PullToRefreshView;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostListActivity extends BaseActivity implements  OnPostContentListener, OnCancelListener, OnRefreshListener2<ListView> {
	private PostListView postListView;
	private PullToRefreshView pullToRefreshView;
	private Button btnPostListBack;
	private KeyboardListenLinearLayout keyboardListenLinearLayout;
	private LinearLayout zxtdLoadingView;
	private RelativeLayout netErrorView;
	private LinearLayout isEmptyPost;

	private PostListAdpater listAdpater;
	
	private PostDialog postDialog;
	private CopyDialog copyDialog;

	private String mNewId = null;
	private String flag = null;
	private String postTotalCount = null;
	private List<Bean> mHotCheckedBeans;
	private List<Bean> mNewCheckedBeans;
	
	private int userId = -1;

	private static final int INIT = 0;
	private static final int LOAD = 1;
	private static final int REFRESH = 2;
	private static final int NONE = 3;	
	
	private static final int LOGIN_CODE = 110;
	
	private RequestCallBack callBack = new RequestCallBack() {
		
		public void requestSuccess(String requestCode, Result result) {
			int currentState = result.getInteger("state");
			if(Constant.RequestCode.ZXTD_PUBLISH_NEWS_CHECK_INVITATION.equals(requestCode) 
					|| Constant.RequestCode.ZXTD_CHECK_INVITATION.equals(requestCode)
					|| Constant.RequestCode.ZXTD_VIDEO_CHECK_INVITATION.equals(requestCode)){
				switch (currentState) {
				case INIT:
					changeData(result, true);
					postListView.setAdapter(listAdpater);
					zxtdLoadingView.setVisibility(View.GONE);
					break;

				case LOAD:
					changeData(result, false);
					pullToRefreshView.onRefreshComplete2();
					break;
				case REFRESH:
					changeData(result, true);
					pullToRefreshView.onRefreshComplete2();
					break;
				case NONE:
	
					break;
				}
			}else{
				if(result.getBoolean(InvitationReplyParseData.IS_SUCCESS)){
					Toast.makeText(PostListActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
					doInit();
				}else{
					Toast.makeText(PostListActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
				}
			}
			zxtdLoadingView.setVisibility(View.GONE);
		}
		
		public void requestError(String requestCode, int errorCode) {
			if(Constant.RequestCode.ZXTD_PUBLISH_NEWS_CHECK_INVITATION.equals(requestCode) || Constant.RequestCode.ZXTD_CHECK_INVITATION.equals(requestCode)){
				netErrorView.setVisibility(View.VISIBLE);
				
				isEmptyPost.setVisibility(View.GONE);
			}else{
				Toast.makeText(PostListActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
			}
			zxtdLoadingView.setVisibility(View.GONE);
			pullToRefreshView.onRefreshComplete2();
		}
	};

	private OnCopyContentListener contentListener = new OnCopyContentListener() {

		public void onCopy(View view, InvitationReplyBean replyBean) {
			if (copyDialog.isShowing()) {
				copyDialog.dismiss();
			} else {
				copyDialog.setReply(replyBean);
				copyDialog.show(view);
			}
		}
	};
	
	private OnScrollListener onScrollListener = new OnScrollListener() {
		
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(copyDialog.isShowing()){
				copyDialog.dismiss();
			}
		}
		
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};
	
	private OnClickListener netConnectClick = new OnClickListener() {
		
		public void onClick(View v) {
			zxtdLoadingView.setVisibility(View.VISIBLE);
			netErrorView.setVisibility(View.GONE);
			doRefresh();
		}
	}; 
	private OnClickListener wlanSettingClick = new OnClickListener() {
		
		public void onClick(View v) {
			Utils.jumpToWlanSetting(PostListActivity.this);
		}
	};
	
	//左右滑动手势
	private OnLeftOrRightListener leftOrRightListener = new OnLeftOrRightListener(){

		@Override
		public boolean onLeftOrRight(View view, EventType eventType) {
			if(eventType == EventType.RIGHT){
				back();
			}
			return true;
		}
	};
	
	private OnClickListener postBtnOnClick = new OnClickListener() {

		public void onClick(View v) {
			InvitationReplyBean checkedBean = (InvitationReplyBean) v.getTag();
			postDialog.setInvitationId(checkedBean.invitationId);
			postDialog.setHint("我对" + checkedBean.nickname + "说:");
			v.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					postDialog.showSoftKeyboard();
				}
			}, 100);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.post_list);

		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			mNewId = extras.getString(Constant.BundleKey.NEWID);
			flag = extras.getString(Constant.BundleKey.FLAG);
		}
		
		userId = XmppUtils.getUserId();
		pullToRefreshView = (PullToRefreshView) this
				.findViewById(R.id.pull_to_refresh_view);
		postListView = (PostListView)pullToRefreshView.getRefreshableView();
		btnPostListBack = (Button) this.findViewById(R.id.btn_post_list_back);
		keyboardListenLinearLayout = (KeyboardListenLinearLayout) this.findViewById(R.id.keyboard_linear_layout);
		zxtdLoadingView = (LinearLayout) this.findViewById(R.id.loading_view);
		netErrorView = (RelativeLayout) this.findViewById(R.id.net_error);
		isEmptyPost = (LinearLayout) this.findViewById(R.id.is_empty);
		TextView btnWlanSetting = (TextView) this.findViewById(R.id.btn_wlan_setting);
		
		postDialog = new PostDialog(this);
		copyDialog = new CopyDialog(this);
		
		mNewCheckedBeans = new ArrayList<Bean>();
		mHotCheckedBeans = new ArrayList<Bean>();
		listAdpater = new PostListAdpater(this, mNewCheckedBeans, mHotCheckedBeans, mNewId, flag);
		
		
		postListView.setAdapter(listAdpater);

		pullToRefreshView.setOnRefreshListener2(this);
		pullToRefreshView.canLoading(true);
		keyboardListenLinearLayout.setOnSoftKeyboardStateListener(postDialog);
		postDialog.setOnPostContentListener(this);
		listAdpater.setCopyListener(contentListener);
		pullToRefreshView.setOnScrollListener(onScrollListener);
		postListView.setOnLeftOrRightListener(leftOrRightListener);
		netErrorView.setOnClickListener(netConnectClick);
		btnWlanSetting.setOnClickListener(wlanSettingClick);
		copyDialog.setOnClickListener(postBtnOnClick);
		btnPostListBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				back();
			}
		});
		
		doInit();
		postDialog.setNewBean(mNewId);
		postDialog.setOnCancelListener(this);
		postDialog.show();
	}
	
	private void doInit(){
		netErrorView.setVisibility(View.GONE);
		RequestManager.newInstance().invitationCheckedComm(mNewId, "", flag, callBack, INIT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == LOGIN_CODE){
			if(resultCode == RESULT_OK){
				userId = XmppUtils.getUserId();
				Toast("登陆成功！");
			}else{
				Toast("登陆失败！");
			}
			
		}
	}
	
	private void doLoad(){
		int size = mNewCheckedBeans.size();
		if(size == 0){
			doRefresh();
		}else{
			InvitationCheckedBean checkedBean = (InvitationCheckedBean)mNewCheckedBeans.get(size - 1);
			RequestManager.newInstance().invitationCheckedComm(mNewId, checkedBean.invitationId, flag, callBack, LOAD);			
		}
	}
	
	private void doRefresh(){
		pullToRefreshView.setFooterViewVisibile(true);
		netErrorView.setVisibility(View.GONE);
		pullToRefreshView.canLoading(true);
		RequestManager.newInstance().invitationCheckedComm(mNewId, "", flag, callBack, REFRESH);
	}

	private void changeData(Result result, boolean isClear){
		List<Bean> beans = result.getListBean(InvitationCheckedParseData.NEW_COMMENT_LIST_KEY);
		List<Bean> hotBeans = result.getListBean(InvitationCheckedParseData.HOT_COMMENT_LIST_KEY);
		
		System.out.println("----------------------newBeans="+beans+"---------------------");
		System.out.println("----------------------hotBeans="+hotBeans+"---------------------");
		
		postTotalCount = result.getString(InvitationCheckedParseData.POST_COUNTS_KEY);
		boolean ishasData = result.getBoolean(InvitationCheckedParseData.IS_DATA_OVER);
		if(isClear){
			mNewCheckedBeans.clear();
		}
		mNewCheckedBeans.addAll(beans);
		if(hotBeans.size() != 0){
			mHotCheckedBeans.clear();
			mHotCheckedBeans.addAll(hotBeans);
		}
		listAdpater.notifyDataSetChanged();
		if(mNewCheckedBeans.size() == 0){
			isEmptyPost.setVisibility(View.VISIBLE);
			pullToRefreshView.setFooterViewVisibile(false);
			return;
		}else{
			isEmptyPost.setVisibility(View.GONE);
		}
		
		if(ishasData){
			pullToRefreshView.canLoading(false);
		}
	}

	//回复新闻
	public boolean onPostContent(String text, String newId, String niceName) {
		if(userId == -1){
			onLogin();
			return false;
		}else{
			RequestManager.newInstance().invitationReportComm(newId, userId + "", text, flag, callBack);
			return true;
		}
	}
	//回复跟帖
	public boolean onPostContent(String text, String newId, String niceName,
			String invitationId) {
		if(userId == -1){
			onLogin();
			return false;
		}else{
			RequestManager.newInstance().invitationReplyComm(newId, invitationId, userId + "", text, flag, callBack);
			return true;
		}
	}
	//跳到登陆界面
	private void onLogin(){
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("from", "other");
		this.startActivityForResult(intent, LOGIN_CODE);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		postDialog.dismiss();
	}

	public void onCancel(DialogInterface dialog) {
		back();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void back(){
		Intent intent = new Intent();
		intent.putExtra(Constant.BundleKey.POST_COUNT, postTotalCount);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
	//刷新数据
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		doRefresh();
	}
	//加载数据
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		doLoad();
	}
}
