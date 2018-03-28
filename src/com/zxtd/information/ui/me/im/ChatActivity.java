package com.zxtd.information.ui.me.im;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zxtd.information.adapter.IMMessageAdapter;
import com.zxtd.information.bean.FansFocusBean;
import com.zxtd.information.bean.IMMessageBean;
import com.zxtd.information.bean.Session;
import com.zxtd.information.manager.MessageManager;
import com.zxtd.information.manager.SessionManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.PullToRefreshListView;
import com.zxtd.information.ui.me.OtherMainActivity;
import com.zxtd.information.util.Base64Utils;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ImageUtils;
import com.zxtd.information.util.TimeUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;


public class ChatActivity extends BaseActivity implements OnClickListener{

	private Session session;
	private ImageView inputType;
	private ImageView imgAdd;
	private EditText editMsg;
	private Button btnSend;
	private PullToRefreshListView listView;

	private LinearLayout optLayout=null;
	private LinearLayout voiceLayout=null;
	private LinearLayout inputLayout=null;
	private LinearLayout faceLayout=null;
	private ProgressBar proBar;
	
	//表情
	private LinearLayout imgFace;
	private LinearLayout imgPic;
	private LinearLayout imgCamera;
	private LinearLayout imgLocation;
	
	private ArrayList<GridView> grids;
	private ViewPager viewPager;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;
	private ImageView page3;
	private Button btnVoice;
	
	private int REQUEST_CODE_ALBUMS=10;
	private int REQUEST_CODE_CAMERA=11;
	private Uri currentImageUri = null;
	
	private PopupWindow menuWindow=null;
	private MediaRecorder mRecorder;
	private String voiceFilePath="";
	
	private int pageIndex=0;
	private IMMessageAdapter adapter;
	
	private Chat chat=null;
	private String basePath = Environment.getExternalStorageDirectory().getPath(); 
	
	//录音提示
	private TextView txtVoiceTime;
	private ImageView recodeImg;
	private TextView recodeRemaind;
	private float startX;
	private float startY;
	private boolean isDelete=false;
	
	private SoundPool sp=null;
	private Map<Integer,Integer> spMap;
	
	private int recodingTime=0;
	private Timer timer=new Timer();
	private TimerTask task;
	private TextView txtTitle;
	
	private int scroolPosition=0;
	//private FileTransferManager filemanager;
	//private OutgoingFileTransfer fileChat=null;
	
	private String lastMsg;
	private String lastTime;
	
	static final KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_im_layout);
		Bundle bundle=this.getIntent().getExtras();
		session=bundle.getParcelable("session");
		Constant.currentChatJid=session.getSessionName();
		initView();
		loadChatData();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.RECIVEIMMSG);
		filter.setPriority(Integer.MAX_VALUE); 
		registerReceiver(receiver, filter);
		createChat();
		
		final IMMessageBean bean=bundle.getParcelable("msgBean");
		if(null!=bean){
			if(bean.getType()==0){
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						send(bean.getContent(),false);
					}
				}, 2000);
			}else{
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						sendFile(new File(bean.getAttaFile()),bean.getType());
					}
				}, 2000);
			}
		}
		clearSessionNoReadCount();
		
		sp=new SoundPool(2,AudioManager.STREAM_MUSIC,100);
		spMap = new HashMap<Integer,Integer>();
	    spMap.put(1, sp.load(this, R.raw.beep, 1));
	}

	
	/**
	 * 
	 */
	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText(session.getNickName());
		proBar=(ProgressBar) findViewById(R.id.im_send_wait);
		inputType=(ImageView) findViewById(R.id.input_type);
		inputType.setOnClickListener(this);
		imgAdd=(ImageView) findViewById(R.id.im_add);
		imgAdd.setOnClickListener(this);
		
		findViewById(R.id.chat_sender_details).setOnClickListener(this);
		
		editMsg=(EditText) findViewById(R.id.im_chart_inputmsg);
		
		btnSend=(Button) findViewById(R.id.im_chart_send);
		btnSend.setOnClickListener(this);
		
		listView=(PullToRefreshListView) findViewById(R.id.im_chart_list);
		listView.setonRefreshListener(new PullToRefreshListView.OnRefreshListener(){
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				loadChatData();
			}
		});
		
		optLayout=(LinearLayout) findViewById(R.id.im_opt_layout);
		
		
		inputLayout=(LinearLayout) findViewById(R.id.im_input_layout);
		voiceLayout=(LinearLayout) findViewById(R.id.ll_yuyin);
		
		btnVoice=(Button) findViewById(R.id.btn_yuyin);
		btnVoice.setOnTouchListener(touchListener);
		
		imgFace=(LinearLayout) findViewById(R.id.im_face);
		imgFace.setOnClickListener(this);
		
		imgPic=(LinearLayout) findViewById(R.id.im_pic);
		imgPic.setOnClickListener(this);
		
		imgCamera=(LinearLayout) findViewById(R.id.im_camera);
		imgCamera.setOnClickListener(this);
		
		imgLocation=(LinearLayout) findViewById(R.id.im_location);
		imgLocation.setOnClickListener(this);
		
		faceLayout=(LinearLayout) findViewById(R.id.im_face_layout);
		
		listView.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction()==MotionEvent.ACTION_DOWN){
					faceLayout.setVisibility(View.GONE);
					optLayout.setVisibility(View.GONE);
				}
				return false;
			}
		});
		
		
		
		/*
		listView.setOnScrollListener(new PullToRefreshListView.OnScrollListener(){
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					scroolPosition= listView.getFirstVisiblePosition();
                }
			}
		});
		*/
		
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
		page3 = (ImageView) findViewById(R.id.page3_select);
		
		initViewPager();
	}

	private View.OnTouchListener touchListener=new View.OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int action = event.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:{
					btnVoice.setBackgroundResource(R.drawable.im_voice_normal);
					stopRecord();
				}break;
				case MotionEvent.ACTION_DOWN:{
					btnVoice.setBackgroundResource(R.drawable.im_voice_press);
					isDelete=false;
					playSound();
					startX=event.getX(0);
					startY=event.getY(0);
					ViewGroup root = (ViewGroup) findViewById(R.id.im_chart);
					if(null==menuWindow){
						View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.audio_recorder_ring, null);
						txtVoiceTime=(TextView) view.findViewById(R.id.im_voice_time);
						recodeImg=(ImageView) view.findViewById(R.id.background_image);
						recodeRemaind=(TextView) view.findViewById(R.id.im_voice_remaind);
						
						int[] display=ChatActivity.this.getScreenSize();
						int width=View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
						int height=View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
						view.measure(width, height);
						menuWindow = new PopupWindow(view, display[0]/2, view.getMeasuredHeight());
					}
					recodeImg.setImageResource(R.drawable.talk_ring);
					recodeImg.setLayoutParams(new LinearLayout.LayoutParams(56, 120));
					txtVoiceTime.setText("最长1分钟");
					txtVoiceTime.setVisibility(View.VISIBLE);
					recodeRemaind.setText("手指上滑,取消发送");
					
					menuWindow.showAtLocation(root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					if(checkSdcard()){
						saveVoice();
					}else{
						Toast("请先装载sdcard");
					}
				}break;
				case MotionEvent.ACTION_MOVE:{
					float deltaX =Math.abs(event.getX(event.getPointerCount() - 1) - startX) ;
					float deltaY = Math.abs(event.getY(event.getPointerCount() - 1) - startY);
					if(deltaY>150){
						recodeImg.setLayoutParams(new LinearLayout.LayoutParams(110, 130));
						recodeImg.setImageResource(R.drawable.im_voice_up_delete);
						txtVoiceTime.setVisibility(View.GONE);
						recodeRemaind.setText("松开手指,取消发送");
						isDelete=true;
					}
					if(isDelete=true){
						if(deltaY<50){
							recodeImg.setImageResource(R.drawable.talk_ring);
							recodeImg.setLayoutParams(new LinearLayout.LayoutParams(56, 120));
							//txtVoiceTime.setText("最长1分钟");
							txtVoiceTime.setVisibility(View.VISIBLE);
							recodeRemaind.setText("手指上滑,取消发送");
							isDelete=false;
						}
					}
				}break;
			}
			return true;
		}
	};
	
	
	private void playSound(){
		AudioManager am = (AudioManager)this.getSystemService(AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
        sp.play(spMap.get(1), volumnRatio/10.0f, volumnRatio/10.0f, 1, 1, 1);
	}
	
	
	/**
	 * 停止录音，发送录音
	 */
	private void stopRecord(){
		if (menuWindow != null)
			menuWindow.dismiss();
		 if(null!=mRecorder){
			 task.cancel();
			 
			 mRecorder.stop();
             mRecorder.release();
             mRecorder = null;
             if(XmppUtils.getSimpleConnection().isAuthenticated()){
            	 if(!isDelete && recodingTime>1){
            		 sendFile(new File(voiceFilePath),2);
            	 }else{
            		 new File(voiceFilePath).delete();
            		 recodingTime=0;
            	 }
             }else{
            	 recodingTime=0;
            	 Toast("未成功连接到服务器..");
             }
		 }
	}
	

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.input_type:{
				faceLayout.setVisibility(View.GONE);
				optLayout.setVisibility(View.GONE);
				dismissKeyBoard(editMsg);
				if(inputLayout.getVisibility()==View.VISIBLE){
					inputLayout.setVisibility(View.GONE);
					voiceLayout.setVisibility(View.VISIBLE);
					inputType.setImageResource(R.drawable.im_word);
					
				}else{
					inputLayout.setVisibility(View.VISIBLE);
					voiceLayout.setVisibility(View.GONE);
					inputType.setImageResource(R.drawable.im_voice);
				}
			}break;
			case R.id.im_add:{
				dismissKeyBoard(editMsg);
				if(inputLayout.getVisibility()==View.GONE){
					inputLayout.setVisibility(View.VISIBLE);
					voiceLayout.setVisibility(View.GONE);
					inputType.setImageResource(R.drawable.im_voice);
				}
				faceLayout.setVisibility(View.GONE);
				if(optLayout.getVisibility()==View.GONE ){
					optLayout.setVisibility(View.VISIBLE);
				}
				else
					optLayout.setVisibility(View.GONE);
			}break;
			case R.id.im_chart_send:{
				final String content=editMsg.getText().toString().trim();
				if(TextUtils.isEmpty(content)){
					Toast("发送内容不能为空..");
				}else{
					send(content,false);
				}
			}break;
			case R.id.im_face:{
				optLayout.setVisibility(View.GONE);
				faceLayout.setVisibility(View.VISIBLE);
			}break;
			case R.id.im_pic:{
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				this.startActivityForResult(intent, REQUEST_CODE_ALBUMS);
			}break;
			case R.id.im_camera:{
				getPicFormCamera();
			}break;
			case R.id.im_location :{
				getLocation();
			}break;
			case R.id.chat_sender_details:{
				if(null!=session){
					FansFocusBean bean=new FansFocusBean();
					bean.setUserId(Integer.valueOf(session.getSessionName()));
					Intent intent=new Intent(this,OtherMainActivity.class);
					Bundle bundle=new Bundle();
					bundle.putParcelable("bean", bean);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}break;
		}
	}
	
	
	private void dismissKeyBoard(View view){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
	}
	
	
	private void getPicFormCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file= new File(Constant.SDCARD_CAMERA_PATH, getPhotoFileName());
		currentImageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
		this.startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}
	
	private static String getPhotoFileName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			return;
		}
		String imagePath = null;
		if( requestCode == REQUEST_CODE_ALBUMS){
			imagePath = getPathFromPhoto(data);
			if(!Utils.isEmpty(imagePath)){
				//showPictureDialog.show(imagePath);
				uploadPhoto(imagePath);
			}
			
		}else if(requestCode == REQUEST_CODE_CAMERA){
			imagePath = getPathFromCamera();
			if(!Utils.isEmpty(imagePath)){
				//addPicture(imagePath);
				uploadPhoto(imagePath);
			}
		}
	}
	
	
	private String getPathFromCamera(){
		//this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, currentImageUri));
		return currentImageUri.getPath();
	}
	
	private String getPathFromPhoto(Intent data){
		if(data != null){
			Uri uri = data.getData();
			String[] projection = {MediaStore.Images.Media.DATA };
			Cursor cursor = this.managedQuery(uri, projection, null, null, null);
			cursor.moveToFirst();
			String path = cursor.getString(0);
			return path;
		}
		return null;
	}
	
	
	private void uploadPhoto(String filePath){
		//图片压缩处理
		boolean isDecode=false;
		String sendFilePath=filePath;
		
		String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zxtd_zase/images/";
		Date date=new Date();
		String savePath=directory+date.getYear()+"/"+(date.getMonth()+1)+"/";
		File file=new File(savePath);
		if(!file.exists()){
			file.mkdirs();
		}
		File tpFile=new File(filePath);
		long baseFileSize=tpFile.length();
		Bitmap bitmap=ImageUtils.loadBitmap(filePath);
		
		//Log.e(Constant.TAG,"未压缩图片大小:"+(baseFileSize/1024.f)+"k     宽度:"+bitmap.getWidth()+" 高度:"+bitmap.getHeight());
		//先缩放
		/*
		int[] screen=getScreenSize();
		if(bitmap.getWidth()>screen[0] || bitmap.getHeight()>screen[1]){
			if(isDecode){
				String fristSavePath=savePath+tpFile.getName();
				bitmap=Utils.decodeSampledBitmapFromResource(fristSavePath,screen[0],screen[1]);
				String lastSavePath=savePath+System.currentTimeMillis()+tpFile.getName();
				isDecode=compressImage(lastSavePath,bitmap,100);
				if(isDecode){
					if(new File(fristSavePath).delete()){
						new File(lastSavePath).renameTo(new File(fristSavePath));
					}
					sendFilePath=fristSavePath;
				}
			}else{
				bitmap=Utils.decodeSampledBitmapFromResource(filePath,screen[0],screen[1]);
				String firstSavePath=savePath+tpFile.getName();
				isDecode=compressImage(firstSavePath,bitmap,getImageQuality(baseFileSize));
				if(isDecode){
					sendFilePath=firstSavePath;
				}
			}
		}*/
		//Log.e(Constant.TAG,"缩小后图片大小:"+(new File(sendFilePath).length()/1024.f)+"k     宽度:"+bitmap.getWidth()+" 高度:"+bitmap.getHeight());
		
		if(baseFileSize>40*1024){
			String firstSavePath=savePath+tpFile.getName();
			isDecode=compressImage(firstSavePath,bitmap,getImageQuality(baseFileSize));
			if(isDecode){
				sendFilePath=firstSavePath;
			}
		}
		
		//Log.e(Constant.TAG,"压缩后图片大小:"+(new File(sendFilePath).length()/1024.f)+"k     宽度:"+bitmap.getWidth()+" 高度:"+bitmap.getHeight());

		if(null!=bitmap && bitmap.isRecycled()){
			bitmap.recycle();
			bitmap=null;
		}
		sendFile(new File(sendFilePath), 1);
	}
	
	
	private boolean compressImage(String filePath,Bitmap bitmap,int quality){
		boolean flag=false;
		try{
			File file=new File(filePath);
			if(!file.exists()){
				file.createNewFile();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			int options = 100;  
			if(file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")){
				flag=bitmap.compress(CompressFormat.JPEG, options, baos);
        	}else if(file.getName().endsWith(".png")){
        		flag=bitmap.compress(CompressFormat.PNG, options, baos);
        	}
			//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
			while(baos.toByteArray().length / 1024>40){
				 baos.reset();//重置baos即清空baos  
				 if(file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")){
					flag=bitmap.compress(CompressFormat.JPEG, options, baos);
		         }else if(file.getName().endsWith(".png")){
		        	flag=bitmap.compress(CompressFormat.PNG, options, baos);
		        }
				if(options>10)
					options -= 10;
				else{
					if(options<=5)
						break;
					else
					options -= 1;
				}
			}
			FileOutputStream outStream=new FileOutputStream(file);
			baos.writeTo(outStream);
			outStream.flush();
			outStream.close();
			
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return flag;
	} 
	
	
	private int getImageQuality(long filesize){
		if(filesize>1024*1024)
			return 5;
		else if(filesize>500*1024){
			return 10;
		}else if(filesize>200*1024)
			return 20;
		else if(filesize>80*1024)
			return 40;
		else if(filesize>30*1024)
			return 50;
		else 
			return 100;
	}
	
	
	
	
	private void getLocation(){
			Toast("正在定位..");
			proBar.setVisibility(View.VISIBLE);
			final LocationClient mLocationClient = new LocationClient(this);
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);								//打开gps
	        option.setCoorType("bd09ll");							//设置坐标类型为bd09ll
	        option.setPriority(LocationClientOption.NetWorkFirst);	//设置网络优先
	        option.setProdName("android_map");						//设置产品线名称
	        option.setScanSpan(5000);
	        option.setTimeOut(30000);
	        option.disableCache(true);
	        
	        //定时定位，每隔5秒钟定位一次。
	        mLocationClient.setLocOption(option);
	        mLocationClient.registerLocationListener(new BDLocationListener() {
				
				public void onReceiveLocation(BDLocation location) {
					if (location == null){
						handler.sendEmptyMessage(-1);
						return ;
					}
					else{
						mLocationClient.stop();
						mLocationClient.unRegisterLocationListener(this);
					}
					if (location.getLocType() == BDLocation.TypeGpsLocation){
						handler.sendEmptyMessage(-2);
					}else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
						Message msg=handler.obtainMessage();
						msg.what=1;
						msg.obj=location.getAddrStr();
						msg.sendToTarget();
					}
				}
				
		        public void onReceivePoi(BDLocation location){
		        }
			});
	          
	        mLocationClient.start();
	        mLocationClient.requestLocation();
	}
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -2:{
					Toast("获取位置信息失败..");
					proBar.setVisibility(View.GONE);
				}break;
				case -1:{
					//Toast("获取位置信息失败..");
					proBar.setVisibility(View.GONE);
				}break;
				case 1:{
					proBar.setVisibility(View.GONE);
					String addr=msg.obj.toString();
					editMsg.append(addr);
				}break;
				case 2:{
					proBar.setVisibility(View.GONE);
					@SuppressWarnings("unchecked")
					List<IMMessageBean> list=(List<IMMessageBean>) msg.obj;
					if(null==adapter){
						adapter=new IMMessageAdapter(list, ChatActivity.this, session.getImg(), getSelfHeader());
						listView.setAdapter(adapter); 
						listView.setSelection(adapter.getCount()-1);
					}else{
						listView.onRefreshComplete();
						adapter.getDataList().addAll(0, list);
						adapter.notifyDataSetChanged();
						
						//scroolPosition=listView.getFirstItemIndex();
						listView.requestFocusFromTouch();
						listView.setSelection(0);
					}
					pageIndex++;
					if(list.isEmpty()){
						Toast("没有查询到聊天记录..");
						/*if(pageIndex!=0){
							listView.onRefreshComplete();
						}*/
						return;
					}
				}break;
				case 3:{
					txtVoiceTime.setText("最长1分钟,已录:"+recodingTime+"秒");
				}break;
				case 4:{
					stopRecord();
				}break;
				case 5:{
					adapter.notifyDataSetChanged();
					IMMessageBean bean=(IMMessageBean) msg.obj;
					MessageManager.getInstance().modifyMsgState(bean.getMsgId(),bean.getState());
				}break;
				case 8:{
					editMsg.setText("");
				}break;
			}
		}
	};
	
	
	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		
		final DecimalFormat format=new DecimalFormat("000");
		// 生成30个表情
		for(int j=0;j<4;j++){
			GridView grid = (GridView) inflater.inflate(R.layout.mine_im_facegrid, null);
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			for (int i =(30*j+1-j); i < (j+1)*30-j; i++) {
				if(i>105)
					continue;
				int faceid=0;
	    		String getid="f"+format.format(i);
	    		try {
	    			faceid=R.drawable.class.getDeclaredField(getid).getInt(this);
	    			Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", faceid);
					listItems.add(listItem);
	    		}catch(Exception ex){
	    			Utils.printException(ex);
	    		}
			}
			Map<String, Object> mapDelImg = new HashMap<String, Object>();
			mapDelImg.put("image", R.drawable.mine_im_face_delete);
			listItems.add(mapDelImg);
			
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
					R.layout.mine_im_facegrid_item, new String[] { "image" },
					new int[] { R.id.image });
			grid.setAdapter(simpleAdapter);
			final int pageIndex=j;
			grid.setOnItemClickListener(new OnItemClickListener(){		
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					if(arg2==arg0.getAdapter().getCount()-1){
						editMsg.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
					}else{
						@SuppressWarnings("unchecked")
						Map<String,Integer> map=(HashMap<String, Integer>) arg0.getAdapter().getItem(arg2);
						int faceid=map.get("image");
						Bitmap bitmap=BitmapFactory.decodeResource(getResources(), faceid);
						ImageSpan imageSpan=new ImageSpan(bitmap);
						//SpannableString可以把其中的几个字符换成ImageSpan（表情图片）但实际它还是以字符串保存的，
						//如果吧这个文本框中的内容发送到另一个Activity中，你就会接收到一个字符串，然后你在解析字符串吧字符串替换成表情图片
						String str="[f"+format.format(pageIndex*29+arg2+1)+"]";
						SpannableString text=new SpannableString(str);
						text.setSpan(imageSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						editMsg.append(text);
					}
				}
			});
			grids.add(grid);
		}

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}
		};

		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}
	



	// ** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
			//System.out.println("页面滚动" + arg0);

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			//System.out.println("换页了" + arg0);
		}

		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));

				break;
			case 1:
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				break;
			case 2:
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				
				break;
			case 3:
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
			break;

			}
		}
	}
	
	
	/**
	 * 录音
	 */
	private void saveVoice(){
		try{
			Date date=new Date();
			String fileName =basePath+"/zxtd_zase/voices/"+date.getYear()+"/"+(date.getMonth()+1)+"/"; 
			File file=new File(fileName);
			if(!file.exists()){
				file.mkdirs();
			}
			File voiceFile=new File(fileName+session.getSessionId()+"_"+getUserId()+"_"+date.getTime()+".amr");
			// instance
            mRecorder = new MediaRecorder();
            // 设置麦克风
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 输出文件格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 音频文件编码
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);     
            voiceFilePath=voiceFile.getAbsolutePath();
            mRecorder.setOutputFile(voiceFilePath);
            mRecorder.prepare();
            mRecorder.start();
            
            startTiming();
		}catch(Exception ex){
			ex.printStackTrace();
			Log.e(Constant.TAG, ex.getMessage());
		}
	}
	
	/**
	 * 开始计时
	 */
	private void startTiming(){
		task=new TimerTask(){
			@Override
			public void run() {
				recodingTime++;
				handler.sendEmptyMessage(3);
				if(recodingTime>=60){
					//超过60秒是否应该停止录音
					handler.sendEmptyMessage(4);
				}
			}
		};
		timer.schedule(task, new Date(), 1000);
	}
		
	
	/**
	 * 收到广播的处理方法
	 */
	private BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(Constant.RECIVEIMMSG.equals(intent.getAction())){
				String type=intent.getStringExtra("type");
				IMMessageBean bean=intent.getParcelableExtra("immsg");
				if(Constant.currentChatJid.equals(bean.getSender())){
					if(type.equals("add")){
						if(adapter!=null){
							lastMsg=bean.getContent();
							lastTime=bean.getTime();
			
							adapter.getDataList().add(bean);
							adapter.notifyDataSetChanged();
							listView.setSelection(adapter.getCount()-1);
						}
					}else if(type.equals("refreshImg")){
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	};

	
	/**
	 * 重新打开Activity的处理
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		//保存最后一条消息到session
		modifySessionLastMsg();
		lastMsg="";
		lastTime="";
		pageIndex=0;
		session=intent.getExtras().getParcelable("session");
		txtTitle.setText(session.getNickName());
		Constant.currentChatJid=session.getSessionName();
		if(null!=adapter){
			adapter.getDataList().clear();
		}
		chat=null;
		loadChatData();
		createChat();
		
		//设置未读数量为0 更新会话列表数量和主页数量
		clearSessionNoReadCount();
	}

	
	private void clearSessionNoReadCount(){
		if(session.getNoReadMsgCount()>0){
			boolean isSuccess=SessionManager.getInstance().clearNoRead(session.getSessionId());
			if(isSuccess){
				session.setNoReadMsgCount(0);
				Constant.CACHESESSION.put(session.getSessionName(), session);
				Intent newintent=new Intent(Constant.REFRESH_SESSION);
				newintent.putExtra("type", "clear");
				newintent.putExtra("session", session);
				sendBroadcast(newintent);
				
				if(Constant.loginUser==null){
					int count=MessageManager.getInstance().getTotalNoReadCount();
					Intent tempIntent=new Intent(Constant.UPDATE_TOTAL_NO_READ);
					tempIntent.putExtra("count", count);
					sendBroadcast(tempIntent);
				}
			}
		}
	}
	
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constant.currentChatJid="";
		unregisterReceiver(receiver);
	}
	
	
    @Override 
    public void onPause() { 
        super.onPause(); 
        //Activity暂停时释放录音和播放对象  
        if (mRecorder != null) { 
            mRecorder.release(); 
            mRecorder = null; 
        } 
    } 
	
	/**
	 * 加载聊天记录
	 */
	private void loadChatData(){
		proBar.setVisibility(View.VISIBLE);
		new Thread(){
			@Override
			public void run() {
				List<IMMessageBean> list=MessageManager.getInstance().getMessagesBySession(session.getSessionId(), pageIndex);
				Message msg=handler.obtainMessage();
				msg.what=2;
				msg.obj=list;
				msg.sendToTarget();
			}
		}.start();
	}
	
	
	

	
	/**
	 * 获取自己的头像
	 * @return
	 */
	private String getSelfHeader(){
		if(null!=Constant.loginUser && !TextUtils.isEmpty(Constant.loginUser.getHeader()))
			return Constant.loginUser.getHeader();
		else{
			SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			return shareds.getString("header", "");
		}
			
	}
	
	/**
	 * 创建聊天对象
	 */
	private void createChat(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				chat=XmppUtils.getSimpleConnection().getChatManager().createChat(session.getJid(), null);
			}
		}.start();
	}
	
	/**
	 * 创建文件聊天
	 */
	
	/*
	private void createFileChat(){
		FileTransferManager filemanager=new FileTransferManager(XmppUtils.getSimpleConnection());
		//FileTransferNegotiator.setServiceEnabled(XmppUtils.getSimpleConnection(), true);
		fileChat=filemanager.createOutgoingFileTransfer(session.getJid()+"/Smack");
	}*/
	
	/**
	 * 发送消息
	 */
	private void send(String content,boolean isFile){ 
			try{
				if(!XmppUtils.getSimpleConnection().isConnected()){
					Toast("未成功连接到服务器..");							
					return;
				}else{
					org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
					message.setProperty(IMMessageBean.KEY_TIME, TimeUtils.getNow());
					message.setBody(content);
					chat.sendMessage(message);
					//int state=0;
					if(org.jivesoftware.smack.packet.Message.Type.error == message.getType()) {
						state=IMMessageBean.ERROR;
					} else {
						if(!isFile){
							lastMsg=content;
							lastTime=TimeUtils.getNow();
						}
						state=IMMessageBean.SUCCESS;
						handler.sendEmptyMessage(8);
						sendCount=0;
					}
					if(!isFile){
						saveMessage(content,0,state,"",0);
					}
				}
			}catch(Exception ex){
				if(sendCount<1){
					doSendError(content,isFile);
				}
				sendCount++;
				Utils.printException(ex);
			}
	}
	
	private int sendCount=0;
	private void doSendError(final String content,final boolean isFile){
		new Thread(){
			@Override
			public void run() {
				super.run();
				if(Utils.isNetworkConn()){
					try{
						chat=XmppUtils.getSimpleConnection().getChatManager().createChat(session.getJid(), null);
						send(content,isFile);
					}catch(Exception ex){
						Utils.printException(ex);
					}
				}
			}
		}.start();
	}
	
	
	private IMMessageBean saveMessage(String content,int type,int state,String attfile,int voiceSecond){
		//入库
		IMMessageBean bean=new IMMessageBean();
		bean.setTime(TimeUtils.getNow());
		bean.setContent(content);
		bean.setState(state);
		bean.setSender(String.valueOf(getUserId()));
		bean.setType(type);
		bean.setAttaFile(attfile);
		bean.setIsSelf(IMMessageBean.SELF);
		bean.setSessionId(session.getSessionId());
		bean.setVoiceSecond(voiceSecond);
		int msgId=MessageManager.getInstance().insertMessage(bean);
		bean.setMsgId(msgId);
		
		adapter.getDataList().add(bean);
		adapter.notifyDataSetChanged();
		listView.setSelection(adapter.getCount()-1);
		return bean;
	}
	
	
	/**
	 * 发送文件
	 * @param file
	 */
	private int state=IMMessageBean.SEND;
	private static final String NOT_ONLINE_EXCEPTIION="service-unavailable(503)";
	
	
	private void sendFile(final File file,final int type){
		if(Utils.isNetworkConn() && XmppUtils.getSimpleConnection().isConnected()){
			final IMMessageBean bean=saveMessage("",type,state,file.getAbsolutePath(),recodingTime);
			FileTransferManager filemanager=new FileTransferManager(XmppUtils.getSimpleConnection());
			FileTransferNegotiator.setServiceEnabled(XmppUtils.getSimpleConnection(), true);
			final OutgoingFileTransfer fileChat=filemanager.createOutgoingFileTransfer(session.getJid()+"/Smack");
			new Thread(){
				@Override
				public void run() {
					try{
						super.run();
						if(type==2){
							fileChat.sendFile(file, String.valueOf(recodingTime));
							lastMsg="[语音]";
							//recodingTime=0;
						}else{
							fileChat.sendFile(file, file.getName());
							lastMsg="[图片]";
						}
						//Log.e("ZQ", "发送文件未完成"+fileChat.getStatus()+"===>"+fileChat.getError()+"====>"+fileChat.getException());
						long timeOut = 200000;
						long sleepMin = 500;
						long spTime = 0;
						while(true){
							Status status = fileChat.getStatus();
							Log.e("ZQ", "发送文件未完成"+status+"===>"+fileChat.getError()+"====>"+fileChat.getException()+"---->");
							
							//fileChat.getError().compareTo(FileTransfer.Error.not_acceptable)
							if ((status == FileTransfer.Status.error)
									|| (status == FileTransfer.Status.complete)
									|| (status == FileTransfer.Status.cancelled)
									|| (status == FileTransfer.Status.refused)) {
								if(status == FileTransfer.Status.complete){
									state=IMMessageBean.SUCCESS;
									lastTime=TimeUtils.getNow();
								}else{
									if(status == FileTransfer.Status.error){
										Exception exception=fileChat.getException();
										if(null!=exception && NOT_ONLINE_EXCEPTIION.equals(exception.getMessage())){
											base64ToSendFile(file,type);
										}else{
											state=IMMessageBean.ERROR;
										}
									}else{
										state=IMMessageBean.ERROR;
									}
								}
								Message msg=handler.obtainMessage();
								bean.setState(state);
								msg.what=5;
								msg.obj=bean;
								msg.sendToTarget();
								
								recodingTime=0;
								break;
							}
							
							else if(status == FileTransfer.Status.negotiating_transfer){
								//..
							}else if(status == FileTransfer.Status.negotiated){							
								//..
							}else if(status == FileTransfer.Status.initial){
								//..
							}else if(status == FileTransfer.Status.negotiating_stream){							
								//..
							}
							else if(status == FileTransfer.Status.in_progress){
								long p = fileChat.getBytesSent() * 100L / fileChat.getFileSize();													
								android.os.Message message = handler.obtainMessage();
								message.obj = Math.round((float) p);
								message.what = 6;
								message.sendToTarget();
								//Log.e(Constant.TAG, "发送文件......................."+Math.round((float) p));
							}
							spTime = spTime + sleepMin;
							if(spTime > timeOut) {
								recodingTime=0;
								state=IMMessageBean.ERROR;
							   break;
							}
							Thread.sleep(sleepMin);
						}
					}catch(Exception ex){
						recodingTime=0;
						Utils.printException(ex);
					}
				}
			}.start();
			state=IMMessageBean.SEND;
		}else{
			Toast("网络已断开或失去同服务器的连接");
		}
	}
	
	
	private void base64ToSendFile(File file,int fileType){
		//将文件转Base64
		String PREFIX_STAG=Constant.PREFIX_STAG+"_"+XmppUtils.getUserId()+"_"+fileType+"_"+recodingTime+"_"+ImageUtils.getImageSuffix(file.getPath());
		String SUFFIX_STAG=Constant.SUFFIX_STAG+"_"+XmppUtils.getUserId();
		String base64=Base64Utils.encodeFile(file);
		StringBuffer sb=new StringBuffer();
		sb.append(Base64Utils.formatString(PREFIX_STAG));
		sb.append(base64);
		sb.append(SUFFIX_STAG);
		send(sb.toString(),true);
	}
	


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(optLayout.getVisibility()==View.VISIBLE){
				optLayout.setVisibility(View.GONE);
				return true;
			}else if(faceLayout.getVisibility()==View.VISIBLE){
				faceLayout.setVisibility(View.GONE);
				return true;
			}else{
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		modifySessionLastMsg();
		super.finish();
	}


	private void modifySessionLastMsg(){
		if(!TextUtils.isEmpty(lastMsg) && !TextUtils.isEmpty(lastTime)){
			SessionManager.getInstance().modifySessionLastMsg(lastMsg, lastTime, session.getSessionId());
		}
	}
	
	
}
