package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NetFriendNewsListAdpater extends BaseAdapter {
	private Context context;
	private List<Bean> downloadNewsList;
	private zxtd_AsyncImageLoader imageLoader;
	private zxtd_ImageCacheDao cacheDao;

	public NetFriendNewsListAdpater(Context mContext,
			List<Bean> downloadNewsListBeans) {
		this.context = mContext;
		this.downloadNewsList = downloadNewsListBeans;
		imageLoader = new zxtd_AsyncImageLoader();
		cacheDao = zxtd_ImageCacheDao.Instance();
	}

	public int getCount() {
		return downloadNewsList.size();
	}

	public Object getItem(int position) {
		return downloadNewsList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		Bean listBean = downloadNewsList.get(position);
		NewsListView newsListView = null;
		if (listBean instanceof NewTypeBean) {
			NewTypeBean newTypeBean = (NewTypeBean) listBean;
			if (view == null || !"NewTypeBean".equals((String) view.getTag(R.id.tag_key_flag))) {
				newsListView = new NewsListView();
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.publish_news_list_type, null);
				newsListView.typeName = (TextView) view
						.findViewById(R.id.type_name);
				view.setTag(R.id.tag_key_view, newsListView);
				view.setTag(R.id.tag_key_flag, "NewTypeBean");
			} else {
				newsListView = (NewsListView) view.getTag(R.id.tag_key_view);
			}
			if (listBean != null) {
				newsListView.typeName.setText(newTypeBean.name);
			}
		} else {
			DownloadNewsListBean downloadNewsListBean = (DownloadNewsListBean) listBean;
			if (view == null
					|| !"DownloadNewsListBean".equals((String) view.getTag(R.id.tag_key_flag))) {
				newsListView = new NewsListView();
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.publish_news_list_item, null);
				newsListView.newsTitle = (TextView) view
						.findViewById(R.id.news_title_content_id);
				newsListView.newsNickname = (TextView) view
						.findViewById(R.id.news_nickname_content_id);
				newsListView.newsDate = (TextView) view
						.findViewById(R.id.news_date_content_id);
				newsListView.postCounts = (TextView) view
						.findViewById(R.id.news_postcounts_content_id);
				newsListView.imgHeadIcon = (ImageView) view.findViewById(R.id.mine_self_header);
				view.setTag(R.id.tag_key_view, newsListView);
				view.setTag(R.id.tag_key_flag, "DownloadNewsListBean");
			} else {
				newsListView = (NewsListView) view.getTag(R.id.tag_key_view);
			}
			
			view.setBackgroundResource(R.drawable.list_selector);

			if (listBean != null) {
				
				if(Constant.NetFriendListFlag.BOUTIQUE_ITEM.equals(downloadNewsListBean.flag)){
					SpannableString title = new SpannableString("热 " + downloadNewsListBean.title);
			        title.setSpan(new ForegroundColorSpan(0xffff3600), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        title.setSpan(new RelativeSizeSpan(0.6f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        title.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        newsListView.newsTitle.setText(title);
				}else{
					newsListView.newsTitle.setText(downloadNewsListBean.title);
				}
				if(Utils.netFriendClickedItem.contains(downloadNewsListBean.id)){
					newsListView.newsTitle.setTextColor(0xff898888);
				}else{
					newsListView.newsTitle.setTextColor(0xff1c1c1c);
				}
				newsListView.newsNickname.setText(downloadNewsListBean.nickname);
				
				newsListView.newsDate.setText(downloadNewsListBean.time);
				newsListView.postCounts.setText(downloadNewsListBean.postcount);
				newsListView.imgHeadIcon.setTag(position);
				setUrlImage(downloadNewsListBean.imgUrl, newsListView.imgHeadIcon, position);
			}
		}
		return view;
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

	private static class NewsListView {
		TextView newsTitle;
		TextView newsNickname;
		TextView newsDate;
		TextView postCounts;
		TextView typeName;
		ImageView imgHeadIcon;
	}
}
