package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoIconNewListAdapter extends BaseAdapter {
	private List<Bean> mNewBeans;
	private Context mContext;
	public NoIconNewListAdapter(Context context, List<Bean> newBeans){
		this.mNewBeans = newBeans;
		this.mContext = context;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mNewBeans.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mNewBeans.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		NewBean newBean = (NewBean)mNewBeans.get(position);
		Hoder hoder = null;
		if(convertView == null){
			hoder = new Hoder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.no_icon_new_list_item, null);
			hoder.newTitle = (TextView) convertView.findViewById(R.id.new_title);
			hoder.newInfo = (TextView) convertView.findViewById(R.id.new_info);
			hoder.postCount = (TextView) convertView.findViewById(R.id.new_reply_count);
			hoder.isNetFriend = (ImageView) convertView.findViewById(R.id.is_net_friend);
			convertView.setTag(hoder);
		}else{
			hoder = (Hoder) convertView.getTag();
		}
		
		convertView.setBackgroundResource(R.drawable.list_selector);
		if(newBean != null){
			hoder.newTitle.setText(newBean.newTitle);
			if(Utils.newClickedItem.contains(newBean.newId)){
				hoder.newTitle.setTextColor(0xff898888);
			}else{
				hoder.newTitle.setTextColor(0xff1c1c1c);
			}
			hoder.newInfo.setText(newBean.newOutline);
			hoder.postCount.setText(newBean.postCount);
			if(Constant.ItemFlag.NET_FRIEND_ITEM.equals(newBean.flag)){
				hoder.isNetFriend.setVisibility(View.VISIBLE);
			}else if(Constant.ItemFlag.NEW_ITEM.equals(newBean.flag)){
				hoder.isNetFriend.setVisibility(View.GONE);
			}else{
				hoder.isNetFriend.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	private static class Hoder{
		TextView newTitle;
		TextView newInfo;
		TextView postCount;
		ImageView isNetFriend;
	}
}
