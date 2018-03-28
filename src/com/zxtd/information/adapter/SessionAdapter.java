package com.zxtd.information.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zxtd.information.bean.Session;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.ui.me.im.SessionsActivity;
import com.zxtd.information.util.ExpressionUtil;
import com.zxtd.information.util.TimeUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


public class SessionAdapter extends BaseAdapter {

	private Context context;
	private List<Session> dataList;
	private LayoutInflater inflater;
	private AsyncImageLoader asyncImageLoader;
	private float scale;
	private boolean isShowCheckBox=false;
	private SessionsActivity activity;
	public void setShowCheckBox(boolean isShowCheckBox) {
		this.isShowCheckBox = isShowCheckBox;
	}

	public SessionAdapter(Context context,List<Session> dataList){
		this.context=context;
		this.dataList=dataList;
		inflater=LayoutInflater.from(this.context);
		asyncImageLoader = new AsyncImageLoader(); 
	    scale=context.getResources().getDisplayMetrics().density; 
	    activity=(SessionsActivity)context;
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
	
	private int checkedCount=0;

	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final Session session=dataList.get(arg0);
		ViewHolder holder=null;
		if(null==view){
			view=inflater.inflate(R.layout.mine_imsession_list, null);
			holder=new ViewHolder();
			holder.img=(RoundAngleImageView) view.findViewById(R.id.session_icon);
			holder.txtTitle=(TextView) view.findViewById(R.id.session_name);
			holder.txtLastSendDate=(TextView) view.findViewById(R.id.session_date);
			holder.txtLastMsg=(TextView) view.findViewById(R.id.session_msg);
			holder.txtNoRead=(TextView) view.findViewById(R.id.mine_im_noread);
			holder.checkBox=(CheckBox) view.findViewById(R.id.mine_session_chk_delete);
			holder.arrowImg=(ImageView) view.findViewById(R.id.arrow_right);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		if(!TextUtils.isEmpty(session.getImg())){
			setImg(holder,session.getImg());
		}else{
			holder.img.setImageResource(R.drawable.mine_user_logo);
		}
		holder.txtTitle.setText(session.getNickName());
		holder.txtLastSendDate.setText(formatTime(session.getLastSendTime()));
		if(isShowCheckBox){
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.arrowImg.setVisibility(View.GONE);
			holder.checkBox.setChecked(session.isChecked());
			holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					session.setChecked(arg1);
					if(arg1){
						checkedCount++;
					}else{
						checkedCount--;
					}
					activity.setButtonText(checkedCount);
				}
			});
		}else{
			holder.checkBox.setVisibility(View.GONE);
			holder.arrowImg.setVisibility(View.VISIBLE);
		}
		//holder.txtLastMsg.setText(session.getLastMsg());
		ExpressionUtil.dealContent(context, session.getLastMsg(), holder.txtLastMsg,false);
		if(session.getNoReadMsgCount()>0){
			holder.txtNoRead.setVisibility(View.VISIBLE);
			holder.txtNoRead.setText(String.valueOf(session.getNoReadMsgCount()));
		}else{
			holder.txtNoRead.setVisibility(View.GONE);
		}
		view.setBackgroundResource(R.drawable.list_selector);//R.drawable.listbg
		return view;
	}

	private static final class ViewHolder{
		private RoundAngleImageView img;
		private TextView txtTitle;
		private TextView txtLastSendDate;
		private TextView txtLastMsg;
		private TextView txtNoRead;
		private CheckBox checkBox;
		private ImageView arrowImg;
	}
	
	
	
	/**
	 * 
	 * @param holder
	 * @param imgUrl
	 */
	private void setImg(final ViewHolder holder,String imgUrl){
		/*int index=imgUrl.lastIndexOf("/");
		String imgName=imgUrl.substring(index+1);
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String dirPath=Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/imgcache";
			File dirFile=new File(dirPath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			String imgPath=dirPath+"/"+imgName;
			File imgFile=new File(imgPath);
			if(imgFile.exists()){
				Bitmap bitmap=BitmapFactory.decodeFile(imgPath);
				if(bitmap!=null && bitmap.isRecycled()){
					bitmap.recycle();
				}
				holder.img.setImageBitmap(bitmap);
			}else{*/
				Drawable drawable=asyncImageLoader.loadDrawable(imgUrl,true,new ImageCallback(){
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						// TODO Auto-generated method stub
						if(null!=imageDrawable){
							holder.img.setImageDrawable(imageDrawable);  
						}
						//holder.img.setScaleType(ImageView.ScaleType.FIT_XY);   
					}
		        });
				if(null!=drawable){
					holder.img.setImageDrawable(drawable); 
				}
			//}
		//}
	}

	public List<Session> getDataList() {
		return dataList;
	}

	public void setDataList(List<Session> dataList) {
		this.dataList = dataList;
	}
	
	
	public String formatTime(String lastSendTime){
		if(!TextUtils.isEmpty(lastSendTime)){
			if(TimeUtils.getNow().substring(0, 10).equals(lastSendTime.substring(0, 10))){
				int hour=Integer.valueOf(lastSendTime.substring(11, 13));
				int minute=Integer.valueOf(lastSendTime.substring(14, 16));
				String name="";
				if(hour>=0 && hour<6){
					name="凌晨";
				}
				else if(hour>=6 &&hour<12){
					name="早上";
				}else if(hour>=12 && hour<18){
					name="下午";
				}else{
					name="晚上";
				}
				return name+hour+":"+((minute<10) ? "0"+minute : minute);
			}else{
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date=format.parse(lastSendTime);
					format=new SimpleDateFormat("MM月dd日");
					return format.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "";
				}
			}
		}
		return lastSendTime;
	}
	
}
