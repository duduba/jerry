package com.zxtd.information.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalFocusAdapter extends BaseAdapter {

	private List<FansFocusBean> dataList;
	private Context context;
	private LayoutInflater inflater;
	private String[] sections;
	private AsyncImageLoader loader;
	
	public String[] getSections() {
		return sections;
	}

	public void setSections(String[] sections) {
		this.sections = sections;
	}

	private HashMap<String, Integer> alphaIndexer;
	 
	public HashMap<String, Integer> getAlphaIndexer() {
		return alphaIndexer;
	}

	public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
		this.alphaIndexer = alphaIndexer;
	}
	
	public LocalFocusAdapter(List<FansFocusBean> dataList,Context context){
		this.dataList=dataList;
		this.context=context;
		inflater=LayoutInflater.from(this.context);
		
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[dataList.size()];
		
		for (int i = 0; i < dataList.size(); i++) {
			String currentStr = getAlpha(dataList.get(i).getSortKey());
			String previewStr = (i - 1) >= 0 ? getAlpha(dataList.get(i - 1).getSortKey()) : " ";
            if (!previewStr.equals(currentStr)) {
            	String name = getAlpha(dataList.get(i).getSortKey());
            	alphaIndexer.put(name, i);  
            	sections[i] = name; 
            }
        }
		loader = new AsyncImageLoader();  
	}
	
	 private String getAlpha(String str){ 
	        if (str == null) {  
	            return "#";  
	        }  
	        if (str.trim().length() == 0) {  
	            return "#";  
	        }  
	        char c = str.trim().substring(0, 1).charAt(0);  
	        Pattern pattern = Pattern.compile("^[A-Za-z]+$");  
	        if (pattern.matcher(c + "").matches()) {  
	            return (c + "").toUpperCase();  
	        } else {  
	            return "#";  
	        }  
	 }
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		final FansFocusBean bean=dataList.get(position);
		ViewHolder holder=null;
		if(null==view){
			holder=new ViewHolder();
			view=inflater.inflate(R.layout.mine_search_focus_list, null);
			holder.alpha=(TextView) view.findViewById(R.id.alpha);
			holder.userImg=(RoundAngleImageView) view.findViewById(R.id.user_icon);
			holder.txtNickName=(TextView) view.findViewById(R.id.nick_name);
			holder.txtSign=(TextView) view.findViewById(R.id.sign);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		String currentStr = getAlpha(bean.getSortKey());
        String previewStr = (position - 1) >= 0 ? getAlpha(dataList.get(position - 1).getSortKey()) : " ";
        
        if (!previewStr.equals(currentStr)){
        	holder.alpha.getBackground().setAlpha(150);
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {  
            holder.alpha.setVisibility(View.GONE);
        }  
		
		holder.txtNickName.setText(bean.getNickName());
		holder.txtSign.setText(bean.getSign());
		if(!Utils.isEmpty(bean.getImg())){
			String filePath=Constant.Url.HOST_URL+bean.getImg();
    		Drawable drawable=loadImage(filePath,holder.userImg);
    		if(null!=drawable){
    			holder.userImg.setImageDrawable(drawable);
    		}
		}else{
			holder.userImg.setImageResource(R.drawable.mine_user_logo);
		}
		view.setBackgroundResource(R.drawable.list_select_color);
		return view;
	}

	
	private static final class ViewHolder{
		private TextView alpha;
		private RoundAngleImageView userImg;
		private TextView txtNickName;
		private TextView txtSign;
	}
	
	
	 private Drawable loadImage(String filePath,final ImageView imageView){
    	 return loader.loadDrawable(filePath,true, new ImageCallback(){
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					// TODO Auto-generated method stub
					if(null!=imageDrawable){
						imageView.setImageDrawable(imageDrawable);  
					}  
				}
	        });
    }
	
}
