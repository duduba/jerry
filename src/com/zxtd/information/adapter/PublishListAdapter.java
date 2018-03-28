package com.zxtd.information.adapter;


import java.util.List;

import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.db.zxtd_PublishInfoDao;
import com.zxtd.information.ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PublishListAdapter extends BaseAdapter {
	private Context mContext;
	private List<PublishBean> mPublishBeans;
	private zxtd_PublishInfoDao publishInfoDao;
	private boolean isCanDelete = false;
	public PublishListAdapter(Context context, List<PublishBean> publishBeans){
		mContext = context;
		mPublishBeans = publishBeans;
		publishInfoDao = new zxtd_PublishInfoDao();
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return mPublishBeans.size();
	}
	
	public void setCanDelete(boolean isCanDelete){
		this.isCanDelete = isCanDelete;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPublishBeans.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Hoder hoder = null;
		if(convertView == null){
			hoder = new Hoder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.pub_list_dialog_item, null);
			hoder.publishTitle = (TextView) convertView.findViewById(R.id.publish_item_title);
			hoder.btnPublishDelete = (ImageView) convertView.findViewById(R.id.btn_publish_item_delete);
			convertView.setTag(hoder);
		}else{
			hoder = (Hoder) convertView.getTag();
		}
		
		PublishBean publishBean = mPublishBeans.get(position);
		if(publishBean != null){
			hoder.publishTitle.setText(publishBean.title);
		}
		hoder.btnPublishDelete.setOnClickListener(new OnClickDelete(position, publishBean));
		
		if(isCanDelete){
			hoder.btnPublishDelete.setVisibility(View.VISIBLE);
		}else{
			hoder.btnPublishDelete.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	

	private static class Hoder{
		TextView publishTitle;
		ImageView btnPublishDelete;
	}
	
	private class OnClickDelete implements OnClickListener{
		private int index = -1;
		private PublishBean mPublishBean;
		public OnClickDelete(int position, PublishBean publishBean){
			index = position;
			mPublishBean = publishBean;
		}
		
		public void onClick(View v) {
			if(index > -1 ){
				publishInfoDao.delete(mPublishBean.id);
				mPublishBeans.remove(index);
				PublishListAdapter.this.notifyDataSetChanged();
			}
		}
	}
}
