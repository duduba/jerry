package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.InvitationCheckedBean;
import com.zxtd.information.bean.InvitationReplyBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.InvitationSupportParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostListAdpater extends BaseAdapter {
	private Context mContext;
	private List<Bean> mNewCheckedBeans;
	private List<Bean> mHotCheckedBeans;
	private LayoutInflater inflater;
	private String mNewId;
	private String mFlag;
	private OnCopyContentListener mContentListener;
	private boolean isLock = false;
	private Handler handler = new Handler();
	private zxtd_AsyncImageLoader imageLoader;
	private zxtd_ImageCacheDao cacheDao;
	
	public interface OnCopyContentListener{
		void onCopy(View view, InvitationReplyBean replyBean);
	}

	public PostListAdpater(Context context,
			List<Bean> newCheckedBeans, List<Bean> hotCheckedBeans, String newId, String flag) {
		mNewCheckedBeans = newCheckedBeans;
		mHotCheckedBeans = hotCheckedBeans;
		mContext = context;
		mNewId = newId;
		mFlag = flag;
		inflater = LayoutInflater.from(mContext);
		imageLoader = new zxtd_AsyncImageLoader();
		cacheDao = zxtd_ImageCacheDao.Instance();
	}
	
	public void setCopyListener(OnCopyContentListener contentListener){
		mContentListener = contentListener;
	}

	public int getCount() {
		return mNewCheckedBeans.size() + mHotCheckedBeans.size();
	}

	public Object getItem(int arg0) {
		if(arg0 < mHotCheckedBeans.size()){
			return mHotCheckedBeans.get(arg0);
		}else{
			return mNewCheckedBeans.get(arg0 - mHotCheckedBeans.size());
		}
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (!isLock || arg1 == null) {
			Hoder hoder = null;
			if (arg1 == null) {
				hoder = new Hoder();
				
				arg1 = inflater.inflate(R.layout.post_list_item, null);
				hoder.nickName = (TextView) arg1
						.findViewById(R.id.post_nick_name);
				hoder.postContent = (TextView) arg1
						.findViewById(R.id.post_content);
				hoder.upStairs = (LinearLayout) arg1
						.findViewById(R.id.up_stairs);
				hoder.btnSupport = (LinearLayout) arg1.findViewById(R.id.btn_support);
				hoder.supportToast = (TextView) arg1
						.findViewById(R.id.top_toast);
				hoder.newTypeTitleIcon = (ImageView) arg1.findViewById(R.id.new_type_title_icon);
				hoder.postTime = (TextView) arg1.findViewById(R.id.post_time);
				hoder.txtSupportCount = (TextView) arg1.findViewById(R.id.text_support_count);
				hoder.imgSupportIcon = (ImageView) arg1.findViewById(R.id.image_support_icon);
				hoder.imgHeadIcon = (ImageView) arg1.findViewById(R.id.mine_self_header);
				hoder.toOtherMain = (LinearLayout) arg1.findViewById(R.id.line_to_other_main);
				hoder.linePostContent = (LinearLayout) arg1.findViewById(R.id.line_post_content); 
				arg1.setTag(hoder);
				
			} else {
				hoder = (Hoder) arg1.getTag();
			}

			InvitationCheckedBean checkedBean = (InvitationCheckedBean) getItem(arg0);
			
			if(mHotCheckedBeans.size() > 0 && arg0 == 0){
				hoder.newTypeTitleIcon.setVisibility(View.VISIBLE);
				hoder.newTypeTitleIcon.setImageResource(R.drawable.hot_post_title_icon);
			}else if(arg0 == mHotCheckedBeans.size()){
				hoder.newTypeTitleIcon.setVisibility(View.VISIBLE);
				hoder.newTypeTitleIcon.setImageResource(R.drawable.new_post_title_icon);
			}else{
				hoder.newTypeTitleIcon.setVisibility(View.GONE);
			}
			
			if (checkedBean != null) {
				if(Utils.isEmpty(checkedBean.address)){
					hoder.nickName.setText(checkedBean.nickname);
				}else{
					hoder.nickName.setText(checkedBean.nickname + "[" + checkedBean.address + "]");
				}
				
				if(Utils.hasSupportPost.contains(mFlag + "&" + checkedBean.invitationId)){
					hoder.btnSupport.setOnClickListener(null);
					hoder.imgSupportIcon.setImageResource(R.drawable.supported_icon);
				}else{
					hoder.btnSupport.setOnClickListener(topBtnOnClick);
					hoder.imgSupportIcon.setImageResource(R.drawable.support_icon);
				}
				
				hoder.imgHeadIcon.setTag(arg0);
				setUrlImage(checkedBean.imageUrl, hoder.imgHeadIcon, arg0);
				
				hoder.postContent.setText(checkedBean.content);
				hoder.txtSupportCount.setText(checkedBean.number);
				hoder.postTime.setText(checkedBean.time);
				
				hoder.btnSupport.setTag(R.id.tag_key_hoder, hoder);
				hoder.btnSupport.setTag(R.id.tag_key_check_bean, checkedBean);
				
				hoder.linePostContent.setTag(R.id.tag_key_reply_bean, checkedBean);

				hoder.upStairs.removeAllViews();
				createFloors(hoder.upStairs, checkedBean.invitationReplyBeans);
				
				hoder.linePostContent.setOnClickListener(textOnClick);
				
				hoder.toOtherMain.setTag(R.id.tag_key_reply_bean, checkedBean);
				hoder.toOtherMain.setOnClickListener(toOtherMain);
			}

		}
		return arg1;
	}

	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView, int position){
		//Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = imageLoader.loadCacheDrawable(url, imageView, position,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				int currentPosition = Integer.parseInt(v.getTag().toString());
				if(currentPosition == position){
					if(imageDrawable != null){
						((ImageView) v).setImageDrawable(imageDrawable);
						imageDrawable = null;
					}else{
						((ImageView) v).setImageResource(R.drawable.transparent_color);
					}
				}	
			}
		}, cacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView){
		//Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = imageLoader.loadCacheDrawable(url, imageView, 0,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				if(imageDrawable != null){
					((ImageView) v).setImageDrawable(imageDrawable);
					imageDrawable = null;
				}else{
					((ImageView) v).setImageResource(R.drawable.transparent_color);
				}
			}
		}, cacheDao);
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.transparent_color);
		}
	}
	
	private void createFloors(final LinearLayout parentView, final InvitationReplyBean[] replyBeans){
		if(replyBeans != null && replyBeans.length != 0){
			if(replyBeans.length > 5){
				for(int i = 0; i < 2; i ++){
					addFloor(parentView, replyBeans[i], i);
				}
				TextView btnUnfoldFloor = new TextView(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 52);
				btnUnfoldFloor.setLayoutParams(params);
				btnUnfoldFloor.setGravity(Gravity.CENTER);
				btnUnfoldFloor.setTextColor(0xff5b5a5a);
				btnUnfoldFloor.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
				btnUnfoldFloor.setBackgroundResource(R.drawable.hide_floor_selector);
				btnUnfoldFloor.setText("显示隐藏内容");
				
				btnUnfoldFloor.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						parentView.removeView(arg0);
						parentView.removeViewAt(parentView.getChildCount() - 1);
						for(int i = 2; i < replyBeans.length; i ++){
							
							addFloor(parentView, replyBeans[i], i);
						}
					}
				});
				
				parentView.addView(btnUnfoldFloor);
				
				addFloor(parentView, replyBeans[replyBeans.length - 1], replyBeans.length - 1);
				
			}else{
				for (int i = 0; i < replyBeans.length; i++) {
					addFloor(parentView, replyBeans[i], i);
				}
			}
		}
	}
	
	private void addFloor(LinearLayout parentView, InvitationReplyBean replyBean, int i){
		View item = createOneFloor(replyBean, i);
		if(item == null){
			return;
		}
		if( i == 0){
			item.setBackgroundResource(R.drawable.first_floor);
		}else{
			item.setBackgroundResource(R.drawable.un_first_floor);
		}
		parentView.addView(item);
	}
	
	private View createOneFloor(InvitationReplyBean replyBean, int i){
		if(replyBean != null){
			View item = inflater.inflate(R.layout.post_item_storey, null);
			
			TextView nickNameText = (TextView)item.findViewById(R.id.post_nick_name);
			TextView contentText = (TextView) item.findViewById(R.id.post_content);
			TextView floorNumberText = (TextView) item.findViewById(R.id.storey_number);
			ImageView imgHeadIcon = (ImageView) item.findViewById(R.id.mine_self_header);
			LinearLayout lineToOtherMain = (LinearLayout) item.findViewById(R.id.line_to_other_main);
			
			if(Utils.isEmpty(replyBean.address)){
				nickNameText.setText(replyBean.nickname);
			}else{
				nickNameText.setText(replyBean.nickname + "[" + replyBean.address + "]");
			}
			
			setUrlImage(replyBean.imageUrl, imgHeadIcon);
			
			contentText.setText(replyBean.content);
			floorNumberText.setText("" + (i + 1));
			
			item.setTag(R.id.tag_key_reply_bean, replyBean);
			item.setOnClickListener(textOnClick);
			
			lineToOtherMain.setTag(R.id.tag_key_reply_bean, replyBean);
			lineToOtherMain.setOnClickListener(toOtherMain);
			return item;
		}
		
		return null;
	}
	
	public void setLock(boolean lock) {
		//isLock = lock;
	}

	private static class Hoder {
		TextView nickName;
		TextView postContent;
		TextView supportToast;
		TextView postTime;
		TextView txtSupportCount;
		LinearLayout upStairs;
		LinearLayout btnSupport;
		LinearLayout toOtherMain;
		LinearLayout linePostContent;
		ImageView newTypeTitleIcon;
		ImageView imgSupportIcon;
		ImageView imgHeadIcon;
	}

	private OnClickListener textOnClick = new OnClickListener() {
		public void onClick(View v) {
			InvitationReplyBean replyBean = (InvitationReplyBean)v.getTag(R.id.tag_key_reply_bean);
			if(replyBean != null && mContentListener != null){
				mContentListener.onCopy(v, replyBean);
			}
			
		}
	};
	
	private OnClickListener toOtherMain = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			InvitationReplyBean replyBean = (InvitationReplyBean)v.getTag(R.id.tag_key_reply_bean);
			if(replyBean != null){
				try {
					if("-1".equals(replyBean.userId)){
						Toast.makeText(mContext, "该用户不是注册用户", Toast.LENGTH_SHORT).show();
					}else{
						FansFocusBean bean = new FansFocusBean();
						bean.setUserId(Integer.parseInt(replyBean.userId));
						Intent intent = new Intent(mContext, OtherMainActivity.class);
						intent.putExtra("bean", bean);
						mContext.startActivity(intent);
					}
					
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	private OnClickListener topBtnOnClick = new OnClickListener() {
		
		public void onClick(View v) {
			final Hoder hoder = (Hoder) v.getTag(R.id.tag_key_hoder);
			final InvitationCheckedBean checkedBean = (InvitationCheckedBean) v
					.getTag(R.id.tag_key_check_bean);
			hoder.btnSupport.setOnClickListener(null);
			RequestManager.newInstance().invitationSupportComm( mNewId, checkedBean.invitationId, mFlag, new RequestCallBack() {
				
				public void requestSuccess(String requestCode, Result result) {
					if(result.getBoolean(InvitationSupportParseData.IS_SUCCESS)){
						int number = Integer.parseInt(checkedBean.number);
						hoder.supportToast.setVisibility(View.VISIBLE);
						checkedBean.number = "" + (++number);
						hoder.txtSupportCount.setText(checkedBean.number);
						hoder.imgSupportIcon.setImageResource(R.drawable.supported_icon);
						handler.postDelayed(new Runnable() {
							
							public void run() {
								hoder.supportToast.setVisibility(View.GONE);
							}
						}, 300);
						Utils.hasSupportPost.add(mFlag + "&" + checkedBean.invitationId);
					}else{
						Toast.makeText(mContext, "顶贴失败！", Toast.LENGTH_LONG).show();
					}
				}
				public void requestError(String requestCode, int errorCode) {
					Toast.makeText(mContext, "网络异常！", Toast.LENGTH_LONG).show();
				}
			}); 
		}
	};

}
