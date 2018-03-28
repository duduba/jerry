package com.zxtd.information.adapter;

import java.util.List;

import com.zxtd.information.bean.Province;
import com.zxtd.information.ui.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProvinceAdapter extends BaseAdapter {

	private Context context;
	private List<Province> dataList;
	private LayoutInflater inflater;
	
	public ProvinceAdapter(Context con,List<Province> list){
		this.context=con;
		this.dataList=list;
		inflater=LayoutInflater.from(context);
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
		
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		Province prov=dataList.get(position);
		if(null==view){
			view=inflater.inflate(R.layout.mine_simple_list, null);
			holder=new ViewHolder();
			holder.txtName=(TextView) view.findViewById(R.id.cityname);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		holder.txtName.setText(prov.getProvName());
		view.setBackgroundResource(R.drawable.listbg);
		return view;
	}

	
	private final static class ViewHolder{
		private TextView txtName;
	}
}
