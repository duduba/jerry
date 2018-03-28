package com.zxtd.information.view;

import java.util.List;

import com.zxtd.information.adapter.NetFriendNewsListAdpater;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NetFriendWebBean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.bean.PosterImageBean;
import com.zxtd.information.db.zxtd_PosterImageInfoDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageCallback;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageSize;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.NetFriendItemTypeListParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.news.NewInfoActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class NetFriendListViewCreater extends ListViewCreater {
	private NetFriendWebBean mNetFriendWebBean;
	private zxtd_PosterImageInfoDao imageInfoDao = new zxtd_PosterImageInfoDao();
	private String itemPosterId = "";
	private ImageView headerImage;
	public NetFriendListViewCreater(NewTypeBean newTypeBean,
			List<Bean> newListBean) {
		super(newTypeBean, newListBean, null);
	}

	@Override
	protected void doFirstRequest(String flag) {
		headerImage = createHeadImage(mContext);
		int tempId = Integer.parseInt(mNewTypeBean.id);
		if(imageInfoDao.isHasData(tempId) > 0){
			PosterImageBean posterImageBean = imageInfoDao.getPosterDataById(tempId);
			itemPosterId = posterImageBean.posterId;
		}
		Log.i("com.zxtd.information", "--------------itemPosterId="+itemPosterId);
		RequestManager.newInstance().netFriendItemTypeListComm("", mNewTypeBean.id, itemPosterId, callBack, INIT);
		mNewListView.addHeaderView(headerImage);
	}

	@Override
	protected BaseAdapter createAdapter() {
		// TODO Auto-generated method stub
		return new NetFriendNewsListAdpater(mContext, mNewListBean);
	}
	
	public void refresh(){
		if(refreshView != null){
			refreshView.setRefreshing();
		}
	}

	@Override
	protected void doLoad() {
		int size = mNewListBean.size();
		if(size == 0){
			doRefresh();
		}else{
			DownloadNewsListBean downloadNewsListBean = (DownloadNewsListBean)mNewListBean.get(size - 1);
			RequestManager.newInstance().netFriendItemTypeListComm(downloadNewsListBean.operid, mNewTypeBean.id, itemPosterId, callBack, LOAD);
		}
	}
	@Override
	protected void doRefresh() {
		refreshView.canLoading(true);
		RequestManager.newInstance().netFriendItemTypeListComm("", mNewTypeBean.id, itemPosterId, callBack, REFRESH);
	}
	RequestCallBack callBack = new RequestCallBack() {
		
		public void requestSuccess(String requestCode, Result result) {
			List<Bean> newBeans = result.getListBean(NetFriendItemTypeListParseData.NET_FRIEND_LIST_KEY);
			int state = result.getInteger("state");
			if(state == INIT || state == REFRESH){
				mNetFriendWebBean = (NetFriendWebBean)result.getBean(NetFriendItemTypeListParseData.WEB_BEAN_KEY);
				String tempFlag = mNetFriendWebBean.flags;
				if(tempFlag != null && tempFlag.equals("1")){
					String id = mNetFriendWebBean.posterId;
					if(id != null && (!id.equals(""))){
						// 有广告信息需要更新
						String imageUrl = mNetFriendWebBean.imageUrl;
						asyncImageLoader.loadDrawable(imageUrl, null, new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable, View view) {
								if(imageDrawable != null){
									headerImage.setImageDrawable(imageDrawable);
									BitmapDrawable bd = (BitmapDrawable)imageDrawable;
									Bitmap bitmap = bd.getBitmap();
									String imageName = "item_poster"+mNewTypeBean.id+".jpg";
									String imagePath = Utils.saveImageCache(bitmap, imageName);
									// 把信息更新保存到数据库中
									updatePosterImageData(mNetFriendWebBean.posterId, imageName, imagePath, mNetFriendWebBean.httpUrl);
									Log.i("com.zxtd.information", "--------------mNetFriendWebBean.posterId="+mNetFriendWebBean.posterId);
									itemPosterId = mNetFriendWebBean.posterId;
								}else {
									headerImage.setImageResource(R.drawable.net_friend_logo);
								}
							}
						});
					}else {
						// 清除这个类型的数据库记录，显示默认广告信息
						imageInfoDao.deleteRecordById(Integer.parseInt(mNewTypeBean.id));
						headerImage.setImageResource(R.drawable.net_friend_logo);
					}
				}else{
					loadLocalImageShow();
				}
			}
			getResult(result, newBeans, state);
		}
		
		public void requestError(String requestCode, int errorCode) {
			refreshView.onRefreshComplete2();
			Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void updatePosterImageData(String posterId, String imageName, String imagePath, String httpUrl) {
		int id = Integer.parseInt(mNewTypeBean.id);
		if(imageInfoDao.isHasData(id) > 0){
			imageInfoDao.updateRecordById(id, posterId, imageName, imagePath, httpUrl);
		}else{
			imageInfoDao.addRecordById(id, posterId, imageName, imagePath, httpUrl);
		}
	}
	
	private void loadLocalImageShow(){
		// 查询数据库看有没有保存过的信息
		int tempId = Integer.parseInt(mNewTypeBean.id);
		if(imageInfoDao.isHasData(tempId) > 0){
			PosterImageBean posterImageBean = imageInfoDao.getPosterDataById(tempId);
			String path = posterImageBean.imagePath;
			if(path != null && !path.equals("")){
				String screenWidth =  String.valueOf(Utils.getDisplayMetrics().widthPixels);
				String screenHeight = String.valueOf(Utils.getDisplayMetrics().heightPixels);
				ImageSize imageSize = new ImageSize(Integer.parseInt(screenWidth), Integer.parseInt(screenHeight));
				asyncImageLoader.loadLocalDrawable(path, null, new ImageCallback(){
					public void imageLoaded(Drawable imageDrawable, View view) {
						if(imageDrawable != null){
							headerImage.setImageDrawable(imageDrawable);
						}else{
							headerImage.setImageResource(R.drawable.net_friend_logo);
						}
					}
				}, imageSize);
			}else{
				headerImage.setImageResource(R.drawable.net_friend_logo);
			}
		}else{
			headerImage.setImageResource(R.drawable.net_friend_logo);
		}
	}
	
	private OnClickListener posterCilck = new OnClickListener() {
		
		public void onClick(View v) {
			String httpUrl = Utils.load(mContext, Constant.posterDataKey.MENUPAGEHTTPURL);
			if(httpUrl != null && (!httpUrl.equals(""))){
				Intent intent = new Intent(mContext, NewInfoActivity.class);
				NewBean newBean = new NewBean();
				newBean.infoUrl = httpUrl;
				intent.putExtra(Constant.BundleKey.NEWBEAN, (Parcelable)newBean);
				intent.putExtra(Constant.BundleKey.FLAG, Constant.Flag.POSTER_TYPE);
				mContext.startActivity(intent);
			}
		}
	};
	
	private ImageView createHeadImage(Context context){
		ScaleImageView imageView = new ScaleImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(params);
		imageView.setOnClickListener(posterCilck);
		return imageView;
	}
}
