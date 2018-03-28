package com.zxtd.information.ui.post;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.adapter.MineCommentAdapter;
import com.zxtd.information.bean.CommentBean;
import com.zxtd.information.manager.CommentManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.view.MineCommentListCreater.OnRefreshListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MineCommentActivity extends BaseActivity implements OnRefreshListener, OnClickListener{
	
	private ImageView img1;
	private ViewPager viewPager;
	private RadioButton rbComment;
	private RadioButton rbReply;
	private MineCommentAdapter adapter;
	private float startLeft=0.0f;
	private int linesWidth=0;
	
	private LoadingDialog dialog;
	
	private List<CommentBean> mineCommentList;
	private List<CommentBean> mineReplyList;
	
	private int userId = -1;
	private int[] pageIndex;
	
	private static final int UPDATE_COMMENT_ADPATER = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mine_comment_list);
		initData();
		initView();
	}
	
	
	private void initData(){
		userId = getIntent().getIntExtra("userId", -1);
		mineCommentList = new ArrayList<CommentBean>();
		mineReplyList = new ArrayList<CommentBean>();
		pageIndex = new int[]{0,0};
		loadData(0);
		loadData(1);
	}
	
	private void initView(){
		TextView title = (TextView)this.findViewById(R.id.title);
		Button btnBack = (Button) this.findViewById(R.id.back);
		viewPager=(ViewPager) findViewById(R.id.mine_fansfocus_viewPager);
		rbComment=(RadioButton) findViewById(R.id.mine_comment);
		rbComment.setOnClickListener(this);
		rbReply=(RadioButton) findViewById(R.id.mine_reply);
		rbReply.setOnClickListener(this);
		
		img1=(ImageView) findViewById(R.id.img_select);
		int width=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int height=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		img1.measure(width, height);
		int[] demis=getScreenSize();
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(demis[0]/2,3);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.mine_comment_rp);
		params.setMargins(0, 0, 0,1);
		img1.setLayoutParams(params);
		
		linesWidth=demis[0]/2;
		
		adapter = new MineCommentAdapter(this, mineCommentList, mineReplyList);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(mOnPageChangeListener);
		adapter.setOnRefreshListener(this);
		
		int type=getIntent().getIntExtra("type", 0);
		if(type==1){
			startLeft=linesWidth;
		}
		changeCheckStatus(type);
		
		dialog=new LoadingDialog(this, R.style.loaddialog);
		
		btnBack.setOnClickListener(this);
		title.setText(getIntent().getStringExtra("userName")+"评论");
	}

	private void loadData(final int typeIndex){
		new Thread(){
			@Override
			public void run() {
				super.run();
				
				List<CommentBean> commentBeans = CommentManager.getInstance().getComments(userId, pageIndex[typeIndex], typeIndex);
				if(commentBeans != null ){
					Message message = handler.obtainMessage(UPDATE_COMMENT_ADPATER, commentBeans);
					message.arg1 = typeIndex;
					message.arg2 = pageIndex[typeIndex];
					message.sendToTarget();
				}
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back){
			this.finish();
		}else if(id == R.id.mine_comment){
			changeCheckStatus(0);
		}else if(id == R.id.mine_reply){
			changeCheckStatus(1);
		}
	}
	
	private void changeCheckStatus(int checkedId){
		switch (checkedId) {
		case 0:
		case R.id.mine_comment:{
			rbComment.setClickable(false);
			rbReply.setClickable(true);
			anit(0);
			viewPager.setCurrentItem(0);
		}break;
		case 1:
		case R.id.mine_reply:{
			rbComment.setClickable(true);
			rbReply.setClickable(false);
			anit(1);
			viewPager.setCurrentItem(1);
		}break;
		}
	}
    
    
	private void anit(int index){
		AnimationSet _AnimationSet = new AnimationSet(true);
		TranslateAnimation animation=new TranslateAnimation(startLeft,linesWidth*index,0,0);
		_AnimationSet.addAnimation(animation);
		_AnimationSet.setFillBefore(true);
		_AnimationSet.setFillAfter(true);
		_AnimationSet.setDuration(500);
		img1.startAnimation(_AnimationSet);
		startLeft=linesWidth*index;
	}
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			changeCheckStatus(arg0);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	@Override
	public void doRefresh(int position) {
		pageIndex[position] = 0;
		loadData(position);
	}

	@Override
	public void doLoad(int position) {
		pageIndex[position] += 1;
		loadData(position);
	}
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_COMMENT_ADPATER:
				List<CommentBean> commentBeans = (List<CommentBean>)msg.obj;
				boolean isDataOver = false;
				if(commentBeans.size() == 0){
					isDataOver = true;
				}else{
					CommentBean commentBean = commentBeans.get(0);
					if(commentBean.getCommentId() == -1){
						isDataOver = true;
						commentBeans.remove(0);
					}
				}
				
				if(msg.arg1 == 0){
					if(msg.arg2 == 0){
						mineCommentList.clear();
					}
					mineCommentList.addAll(commentBeans);
					adapter.updateListAdapter(0);
					adapter.onRefreshComplete(0);
				}else{
					if(msg.arg2 == 0){
						mineReplyList.clear();
					}
					mineReplyList.addAll(commentBeans);
					adapter.updateListAdapter(1);
					adapter.onRefreshComplete(1);
				}
				if(isDataOver){
					adapter.setCanLoading(msg.arg1, false);
				}
				break;

			default:
				break;
			}
		};
	};
}
