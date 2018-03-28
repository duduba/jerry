package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.manager.UserNickManager;
import com.zxtd.information.ui.NickNameActivity;
import com.zxtd.information.ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NickNameListAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mNickNames;
	public NickNameListAdapter(Context context, List<String> nickNames){
		mContext = context;
		mNickNames = nickNames;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return mNickNames.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mNickNames.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Hoder hoder = null;
		if(arg1 == null){
			hoder= new Hoder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			arg1 = inflater.inflate(R.layout.nick_name_list_item, null);
			hoder.nickNameText = (TextView) arg1.findViewById(R.id.nick_name_text);
			hoder.nickNameDel = (ImageView) arg1.findViewById(R.id.nick_name_delete);
			
			arg1.setTag(hoder);
		}else{
			hoder = (Hoder)arg1.getTag();
		}
		
		final String nickName = mNickNames.get(arg0);
		hoder.nickNameText.setText(nickName);
		hoder.nickNameDel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				UserNickManager nickManager = UserNickManager.getNewInstance();
				nickManager.deleteNickName(nickName);
				mNickNames.remove(nickName);
				NickNameListAdapter.this.notifyDataSetChanged();
				if(nickManager.isCurrentNickName(nickName)){
					nickManager.setNickName(null);
					((NickNameActivity) mContext).setCurrentNickName();
				}
				if(mNickNames.size() == 0){
					((NickNameActivity) mContext).HideListFooter();
				}
			}
		});
		
		return arg1;
	}
	
	private static class Hoder{
		TextView nickNameText;
		ImageView nickNameDel;
	}

}
