package com.zxtd.information.adapter;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.IMMessageBean;
import com.zxtd.information.manager.MessageManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.RoundAngleImageView;
import com.zxtd.information.ui.me.AsyncImageLoader;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.ui.me.AsyncImageLoader.ImageCallback;
import com.zxtd.information.ui.me.im.AlbumsActivity;
import com.zxtd.information.ui.me.im.ChatActivity;
import com.zxtd.information.ui.me.im.SearchFocusActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ExpressionUtil;
import com.zxtd.information.util.ImageUtils;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IMMessageAdapter extends BaseAdapter {
	
	private List<IMMessageBean> dataList;
	private Context context;
	private LayoutInflater inflater;
	private String otherHeader;
	private String selfHeader;
	private AsyncImageLoader asyncImageLoader=null;
	private Drawable otherDrawable;
	private Drawable selfDrawable;
	private int screenWidth;
	private int screenHeight;
	
	public IMMessageAdapter(List<IMMessageBean> list,Context con,String other,String self){
		this.dataList=list;
		this.context=con;
		this.otherHeader=other;
		this.selfHeader=self;
		inflater=LayoutInflater.from(context);
		asyncImageLoader=new AsyncImageLoader();
	    DisplayMetrics display=context.getResources().getDisplayMetrics();
	    screenWidth=display.widthPixels;
	    screenHeight=display.heightPixels;
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

	/*
	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final IMMessageBean bean=dataList.get(arg0);
		ViewHolder holder=null;
		Drawable drawable=null;
		if(bean.getIsSelf()==IMMessageBean.OTHER){
			if(null==view){
				view=inflater.inflate(R.layout.mine_im_chart_list_left, null);
				holder=new ViewHolder();
				holder.icon=(RoundAngleImageView) view.findViewById(R.id.sender_icon);
				holder.txtMsg=(TextView) view.findViewById(R.id.sender_msg);
				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}
			drawable=loadHeader(otherHeader,holder.icon);
		}else{
			if(null==view){
				view=inflater.inflate(R.layout.mine_im_chat_list_right, null);
				holder=new ViewHolder();
				holder.icon=(RoundAngleImageView) view.findViewById(R.id.self_icon);
				holder.txtMsg=(TextView) view.findViewById(R.id.self_msg);
				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}
			drawable=loadHeader(selfHeader,holder.icon);
		}
		if(bean.getType()==0){
			holder.txtMsg.setText(bean.getContent());
		}
		if(null!=drawable){
			holder.icon.setImageDrawable(drawable);
		}
		return view;
	}

	
	private static final class ViewHolder{
		private RoundAngleImageView icon;
		private TextView txtMsg;
	}
	*/
	
	//String imgUrl="";
	
	private IMMessageBean proBean=null;
	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final IMMessageBean bean=dataList.get(arg0);
		switch(bean.getType()){
			case 0:{
				view=getWordChatView(bean);
			}break;
			case 1:{
				view=getImageChatView(bean);
			}break;
			case 2:{
				view=getVoiceChatView(bean);
			}break;
		}
		TextView txtTime=(TextView) view.findViewById(R.id.msg_time);
		if(arg0==0 && !dataList.isEmpty()){
			txtTime.setVisibility(View.VISIBLE);
			txtTime.setText(dataList.get(0).getTime());
		}
		if(proBean!=null){
			int minutes=TimeUtils.getDiffTimeMinute(proBean.getTime(),bean.getTime());
			if(minutes>5){
				txtTime.setVisibility(View.VISIBLE);
				txtTime.setText(bean.getTime());
			}
		}
		proBean=bean;
		return view;
	}
	
	
	private void headToHomePage(RoundAngleImageView icon,final IMMessageBean im){
		icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FansFocusBean bean=new FansFocusBean();
				bean.setUserId(Integer.valueOf(im.getSender()));
				Intent intent=new Intent(context,OtherMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putParcelable("bean", bean);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}
	
	
	
	/**
	 * 文字聊天
	 * @param bean
	 * @return
	 */
	private View getWordChatView(final IMMessageBean bean){
		RoundAngleImageView icon;
		TextView txtMsg;
		View view=null;
		if(bean.getIsSelf()==IMMessageBean.OTHER){
			view=inflater.inflate(R.layout.mine_im_chart_list_left, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.sender_icon);
			txtMsg=(TextView) view.findViewById(R.id.sender_msg);
			otherDrawable=loadHeader(otherHeader,icon,IMMessageBean.OTHER);
			if(null!=otherDrawable){
				icon.setImageDrawable(otherDrawable);
			}
			headToHomePage(icon,bean);
		}else{
			view=inflater.inflate(R.layout.mine_im_chat_list_right, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.self_icon);
			txtMsg=(TextView) view.findViewById(R.id.self_msg);
			selfDrawable=loadHeader(selfHeader,icon,IMMessageBean.SELF);
			if(null!=selfDrawable){
				icon.setImageDrawable(selfDrawable);
			}
		}
		ExpressionUtil.dealContent(context,bean.getContent(),txtMsg,true);
		txtMsg.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
				builder.setTitle("操作");
				String[] items=new String[]{"删除消息","复制消息","转发消息"};
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1){
						// TODO Auto-generated method stub
						if(bean.getType()==0){
							switch(arg1){
								case 0:{
									deleteMessage(bean);
								}break;
								case 1:{
									ClipboardManager cm=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
									cm.setText(bean.getContent());
									Toast.makeText(context, "文本已复制到剪贴板", Toast.LENGTH_LONG).show();
								}break;
								case 2:{
									forwardMessage(bean);
								}break;
							}
						}
					}
				});
				builder.create().show();
				return false;
			}
		});
		return view;
	}
	
	
	/**
	 * 语音聊天
	 * @param bean
	 * @return
	 */
	int baseWidth=0;
	int baseHeight=0;
	private View getVoiceChatView(final IMMessageBean bean){
		RoundAngleImageView icon;
		LinearLayout voiceLayout;
		ImageView voiceBar;
		TextView txtSecond;
		ProgressBar waitBar=null;
		ImageView imgError=null;
		View view=null;
		int verb=-1;
		if(bean.getIsSelf()==IMMessageBean.OTHER){
			view=inflater.inflate(R.layout.mine_im_chat_voice_left, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.sender_icon);
			voiceLayout=(LinearLayout) view.findViewById(R.id.sender_msg);
			voiceBar=(ImageView) view.findViewById(R.id.im_voice_other_bar);
			txtSecond=(TextView) view.findViewById(R.id.sender_voice_second);
			otherDrawable=loadHeader(otherHeader,icon,IMMessageBean.OTHER);
			if(null!=otherDrawable){
				icon.setImageDrawable(otherDrawable);
			}
			verb=RelativeLayout.RIGHT_OF;
			headToHomePage(icon,bean);
		}else{
			view=inflater.inflate(R.layout.mine_im_chat_voice_right, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.self_icon);
			voiceLayout= (LinearLayout) view.findViewById(R.id.self_msg);
			voiceBar=(ImageView) view.findViewById(R.id.im_voice_self_bar);
			txtSecond=(TextView) view.findViewById(R.id.self_voice_second);
			waitBar=(ProgressBar) view.findViewById(R.id.im_voice_send_wait);
			imgError=(ImageView) view.findViewById(R.id.im_voice_send_error);
			imgError.setVisibility(View.GONE);
			if(bean.getState()==IMMessageBean.SEND){
				waitBar.setVisibility(View.VISIBLE);
			}else{
				waitBar.setVisibility(View.GONE);
				if(bean.getState()==IMMessageBean.ERROR){
					imgError.setVisibility(View.VISIBLE);
				}
			}
			
			selfDrawable=loadHeader(selfHeader,icon,IMMessageBean.SELF);
			if(null!=selfDrawable){
				icon.setImageDrawable(selfDrawable);
			}
			verb=RelativeLayout.LEFT_OF;
		}
		if(baseWidth==0 || baseHeight==0){
			int newWidth=View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			int newHeight=View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			voiceLayout.measure(newWidth, newHeight);
			baseWidth=voiceLayout.getMeasuredWidth();
			baseHeight=voiceLayout.getMeasuredHeight();
		}
		int width=baseWidth+bean.getVoiceSecond()*3;
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width,baseHeight);
		params.addRule(verb,icon.getId());
		voiceLayout.setLayoutParams(params);
		
		txtSecond.setText(bean.getVoiceSecond()+"\"");
		final ImageView imagePlayAnim=voiceBar;
		voiceLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					if(bean.getIsSelf()==IMMessageBean.OTHER){
						imagePlayAnim.setImageResource(R.drawable.voice_play_right);
					}else{
						imagePlayAnim.setImageResource(R.drawable.voice_play_left);
					}
					final AnimationDrawable drawable=(AnimationDrawable) imagePlayAnim.getDrawable();
					drawable.start();
					
					final MediaPlayer mPlayer = new MediaPlayer();
					mPlayer.setDataSource(bean.getAttaFile());
		            mPlayer.prepare();
		            mPlayer.start();
		            
		            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							drawable.stop();
							if(bean.getIsSelf()==IMMessageBean.OTHER){
								imagePlayAnim.setImageResource(R.drawable.mine_im_voice_other);
							}else{
								imagePlayAnim.setImageResource(R.drawable.mine_im_voice_self);
							}
							mPlayer.release();
						}
					});
		          
				}catch(Exception ex){
					Utils.printException(ex);
				}
			}
		});
		voiceLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
				builder.setTitle("操作");
				String[] items=new String[]{"删除语音","设为铃声"};
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						switch(arg1){
							case 0:{
								deleteMessage(bean);
							}break;
							case 1:{
								setMyRingtone(new File(bean.getAttaFile()));	
							}break;
						}
					}
				});
				builder.create().show();
				return false;
			}
		});
		return view;
	}

	
	/**
	 * 发送图片
	 * @param bean
	 * @return
	 */
	private View getImageChatView(final IMMessageBean bean){
		View view=null;
		RoundAngleImageView icon;
		RoundAngleImageView image;
		ProgressBar waitBar=null;
		ImageView imgError=null;
		if(bean.getIsSelf()==IMMessageBean.OTHER){
			view=inflater.inflate(R.layout.mine_im_chat_image_left, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.sender_icon);
			image=(RoundAngleImageView) view.findViewById(R.id.sender_image);
			otherDrawable=loadHeader(otherHeader,icon,IMMessageBean.OTHER);
			if(null!=otherDrawable){
				icon.setImageDrawable(otherDrawable);
			}
			headToHomePage(icon,bean);
		}else{
			view=inflater.inflate(R.layout.mine_im_chat_image_right, null);
			icon=(RoundAngleImageView) view.findViewById(R.id.self_icon);
			image=(RoundAngleImageView) view.findViewById(R.id.self_image);
			
			waitBar=(ProgressBar) view.findViewById(R.id.im_image_send_wait);
			imgError=(ImageView) view.findViewById(R.id.im_image_send_error);
			imgError.setVisibility(View.GONE);
			if(bean.getState()==IMMessageBean.SEND){
				waitBar.setVisibility(View.VISIBLE);
			}else{
				waitBar.setVisibility(View.GONE);
				if(bean.getState()==IMMessageBean.ERROR){
					imgError.setVisibility(View.VISIBLE);
				}
			}
			selfDrawable=loadHeader(selfHeader,icon,IMMessageBean.SELF);
			if(null!=selfDrawable){
				icon.setImageDrawable(selfDrawable);
			}
		}
		if(bean.getState()==IMMessageBean.SUCCESS){
			decodeImage(bean.getAttaFile(),image);
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showImagesAlbum(bean);
				}
			});
		}else if(bean.getState()==IMMessageBean.SEND){
			image.setImageResource(R.drawable.v5_7_rec_feed_more_pic);
		}else{
			image.setImageResource(R.drawable.mine_im_image_error);
		}	
		
		image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
				builder.setTitle("操作");
				String[] items=new String[]{"删除图片"};//,"转发"
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch(arg1){
							case 0:{
								deleteMessage(bean);
							}break;
							//case 1:{
							//	forwardMessage(bean);	
							//}break;
						}
					}
				});
				builder.create().show();
				return false;
			}
		});
		return view;
	}
	
	
	/**
	 * 显示图片相册
	 * @param bean
	 */
	private void showImagesAlbum(IMMessageBean bean){
		Iterator<IMMessageBean> it=dataList.iterator();
		JSONArray array=new JSONArray();
		try{
			int i=0,position=0;
			while(it.hasNext()){
				IMMessageBean temp=it.next();
				if(temp.getType()==1){
					JSONObject jsonObj=new JSONObject();
					jsonObj.put("imageUrl",temp.getAttaFile());
					//Log.e(Constant.TAG, temp.getAttaFile());
					jsonObj.put("describe","");
					array.put(jsonObj);
					if(temp.getMsgId()==bean.getMsgId()){
						position=i;
					}
					i++;
				}
			}
			String json=array.toString();
			Intent intent=new Intent(context,AlbumsActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString(Constant.BundleKey.IMGE_DATA, json);
			bundle.putInt(Constant.BundleKey.IMAGE_INDEX, position);
			bundle.putBoolean("isLocal", true);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}catch(Exception ex){
			Utils.printException(ex);
		}
	}
	
	
	private void deleteMessage(IMMessageBean bean){
		boolean flag=MessageManager.getInstance().deleteMessage(bean.getMsgId());
		if(flag){
			dataList.remove(bean);
			IMMessageAdapter.this.notifyDataSetChanged();
			if(!TextUtils.isEmpty(bean.getAttaFile())){
				File file=new File(bean.getAttaFile());
				if(file.exists()){
					file.delete();
				}
			}
		}
	}
	
	
	private void forwardMessage(IMMessageBean bean){
		Intent intent=new Intent(context,SearchFocusActivity.class);
		intent.putExtra("msgBean", bean);
		context.startActivity(intent);
		ChatActivity activity=(ChatActivity) context;
		activity.finish();
	}
	
	
	private void decodeImage(String filePath,ImageView imageView){
		File file=new File(filePath);
		if(file.exists()){
			Bitmap bitmap=ImageUtils.loadBitmap(filePath);//BitmapFactory.decodeFile(filePath);
			if(bitmap!=null){
				int width=bitmap.getWidth();
				int height=bitmap.getHeight();
				if(width>screenWidth*0.25 || height>screenHeight*0.12f){
					bitmap = Utils.decodeSampledBitmapFromResource(filePath,
							Math.round(screenWidth*0.25f),Math.round(screenHeight*0.12f));
				}
				imageView.setImageBitmap(bitmap);
				if(null!=bitmap && bitmap.isRecycled()){
					bitmap.recycle();
				}
			}else{
				imageView.setImageResource(R.drawable.album_load);
			}
		}else{
			imageView.setImageResource(R.drawable.mini_publisher_cancel_record_view);
		}
		
	}
	
	
	/*
	 private void dealExpression(SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {  
	        Matcher matcher = patten.matcher(spannableString);  
	        while (matcher.find()) {  
	            String key = matcher.group();  
	            if (matcher.start() < start) {  
	                continue;  
	            }  
	            Field field = R.drawable.class.getDeclaredField(key);  
	            int resId =field.getInt(this);       //通过上面匹配得到的字符串来生成图片资源id   
	            if (resId != 0) {  
	                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);      
	                ImageSpan imageSpan = new ImageSpan(bitmap);                //通过图片资源id来得到bitmap，用一个ImageSpan来包装   
	                int end = matcher.start() + key.length();                   //计算该图片名字的长度，也就是要替换的字符串的长度   
	                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);   //将该图片替换字符串中规定的位置中   
	                if (end < spannableString.length()) {                        //如果整个字符串还未验证完，则继续。。   
	                    dealExpression(spannableString,  patten, end);  
	                }  
	                break;  
	            }  
	        }  
	    }*/
	
	
	
	private Drawable loadHeader(String imgUrl,final RoundAngleImageView imgView,final int isSelf){
		if(TextUtils.isEmpty(imgUrl) || "null".equals(imgUrl)){
			return context.getResources().getDrawable(R.drawable.mine_user_logo);
		}else{
			if(isSelf==IMMessageBean.OTHER && null!=otherDrawable){
				return otherDrawable;
			}else if(isSelf==IMMessageBean.SELF && null!=selfDrawable){
				return selfDrawable;
			}
			if(!imgUrl.startsWith("http"))
				imgUrl=Constant.Url.HOST_URL+imgUrl;
			Drawable drawable=asyncImageLoader.loadDrawable(imgUrl,false,new ImageCallback(){
				public void imageLoaded(Drawable imageDrawable, String imageUrl){
					if(null!=imageDrawable){
						imgView.setImageDrawable(imageDrawable); 
						if(isSelf==IMMessageBean.OTHER){
							otherDrawable=imageDrawable;
						}else{
							selfDrawable=imageDrawable;
						}
					}
				}
	        });
			if(null==drawable){
				return context.getResources().getDrawable(R.drawable.mine_user_logo);
			}else{
				return drawable;
			}
		}
	}

	
	public void setMyRingtone(File file){
      ContentValues values = new ContentValues();  
      values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());  
      values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/amr");  
      values.put(MediaStore.Audio.Media.IS_RINGTONE, true);  
      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);  
      values.put(MediaStore.Audio.Media.IS_ALARM, false);  
      values.put(MediaStore.Audio.Media.IS_MUSIC, false);  
    
      Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());  
      Uri newUri = context.getContentResolver().insert(uri, values);  
      RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);  
    }  
	


	public List<IMMessageBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<IMMessageBean> dataList) {
		this.dataList = dataList;
	}

	
}
