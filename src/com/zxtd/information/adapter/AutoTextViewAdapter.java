package com.zxtd.information.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


public class AutoTextViewAdapter extends BaseAdapter implements Filterable {  
	  
	public List<String> mList;  
    private Context mContext;  
    private MyFilter mFilter;  
      
    public AutoTextViewAdapter(Context context) {  
        mContext = context;  
        mList = new ArrayList<String>();  
    }  
        
    public int getCount() {  
        return mList == null ? 0 : mList.size();  
    }  

      
    public Object getItem(int position) {  
        return mList == null ? null : mList.get(position);  
    }  

      
    public long getItemId(int position) {  
        return position;  
    }  
  
    public View getView(int position, View convertView, ViewGroup parent) {  
        if (convertView == null) {  
            TextView tv = new TextView(mContext);  
            tv.setPadding(0, 10, 0, 10);
            tv.setTextColor(Color.parseColor("#565656"));  
            tv.setTextSize(20);  
            convertView = tv;  
        }  
        TextView txt = (TextView) convertView;  
        txt.setText(mList.get(position));  
        return txt;  
    }  

    public Filter getFilter() {  
        if (mFilter == null) {  
            mFilter = new MyFilter();  
        }  
        return mFilter;  
    }  
    
    private class MyFilter extends Filter {  
    	
    	@Override  
    	protected FilterResults performFiltering(CharSequence constraint) {  
    		FilterResults results = new FilterResults();  
    		if (mList == null) {  
    			mList = new ArrayList<String>();  
    		}  
    		results.values = mList;  
    		results.count = mList.size();  
    		return results;  
    	}  
    	
    	@Override  
    	protected void publishResults(CharSequence constraint, FilterResults results) {  
    		if (results.count > 0) {  
    			notifyDataSetChanged();  
    		} else {  
    			notifyDataSetInvalidated();  
    		}  
    	}  
    	
    }  
}
