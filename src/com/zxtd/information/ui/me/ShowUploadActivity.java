package com.zxtd.information.ui.me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.FileUploadHelper;
import com.zxtd.information.util.ImageUtils;
import com.zxtd.information.util.Utils;

import dalvik.system.VMRuntime;

public class ShowUploadActivity extends BaseActivity implements OnClickListener{

	private String filePath="";
	private Bitmap source=null;
	private ImageView uploadImg;
	private float degrees=0;
	private Bitmap savebitmap=null;
	private Button btnUpload;
	private ProgressBar progressBar;
	private long fileSize=0;
	private long hasDownSize=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_showupload_layout);
		VMRuntime.getRuntime().setMinimumHeapSize(24*1024*1024);
		VMRuntime.getRuntime().setTargetHeapUtilization(0.75f);
		initView();
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.rotate).setOnClickListener(this);
		btnUpload=(Button) findViewById(R.id.btn_save);
		btnUpload.setOnClickListener(this);
		
		progressBar=(ProgressBar) findViewById(R.id.upload_progress);
	
		TextView txtTitle=(TextView) findViewById(R.id.title);
		String title=getIntent().getStringExtra("title");
		txtTitle.setText(title);
		filePath=getIntent().getStringExtra("filepath");
		
		uploadImg=(ImageView) findViewById(R.id.upload_img);
		
		
		try{
			source=ImageUtils.compressImage(filePath);//
			savebitmap=source;
			//source=BitmapFactory.decodeFile(filePath);
		}catch(Exception ex){
			Utils.printException(ex);
		}
		uploadImg.setImageBitmap(source);
		if(source!=null && source.isRecycled()){
			source.recycle();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.btn_save:{
				if(null!=savebitmap){
					int index=filePath.lastIndexOf(".");
					String suffix=filePath.substring(index);
					String saveTempFile=Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/imgcache/temp";
					File file=new File(saveTempFile);
					if(!file.exists()){
						file.mkdirs();
					}
					saveTempFile+="/"+System.currentTimeMillis()+suffix;
					try{
						file=new File(saveTempFile);
						file.createNewFile();
						FileOutputStream fOut = new FileOutputStream(file); 
						if(suffix.equals(".png")){
							savebitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); 
						}else{
							savebitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
						}
						fOut.flush();
						fOut.close();
						filePath=saveTempFile;
						if(savebitmap!=null && savebitmap.isRecycled()){
							savebitmap.recycle();
						}
					}catch(Exception ex){
						Utils.printException(ex);
					}
				}
				Toast("正在开始上传.");
				btnUpload.setClickable(false);
				File upFile=new File(filePath);
				fileSize=upFile.length();
				final Map<String, File> files = new HashMap<String, File>();
                files.put(upFile.getName(), upFile);
                final Map<String, String> params = new HashMap<String, String>();
                params.put("NAME", upFile.getName());
                progressBar.setVisibility(View.VISIBLE);
				new Thread(){
					@Override
					public void run() {
						super.run();
						FileUploadHelper helper=new FileUploadHelper();
						try {
							NewImageBean bean=helper.uploadFile(params, files, handler, 1,1,ShowUploadActivity.this.getUserId());
							Message msg=handler.obtainMessage();
							msg.what=2;
							msg.obj=bean;
							msg.sendToTarget();
						} catch (IOException e) {
							Utils.printException(e);
						}
					}
				}.start();
				
			}break;
			case R.id.rotate:{
				degrees+=90;
				Matrix matrix = new Matrix(); 
				matrix.setRotate(degrees);
				savebitmap=Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
				uploadImg.setImageBitmap(savebitmap);
			}break;
		}
	}
	
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case 1:{
					//hasDownSize+=(Integer) msg.obj;
					float progress=(Float) msg.obj;//(hasDownSize/fileSize)*100;
					DecimalFormat format=new DecimalFormat("###");
					String temp=format.format(progress);
					int percent=Integer.valueOf(temp);
					progressBar.setProgress(percent);
				}break;
				case 2:{
					NewImageBean bean=(NewImageBean) msg.obj;
					if(null==bean){
						Toast("上传失败..");
					}else{
						Toast("上传成功..");
						Intent intent=new Intent(ShowUploadActivity.this,MineNewActivity.class);
						Bundle bundle=new Bundle();
						bundle.putParcelable("imgBean", bean);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					}
				}break;
				
			}
		}
		
	};
	
}
