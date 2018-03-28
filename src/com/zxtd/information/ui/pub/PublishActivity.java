package com.zxtd.information.ui.pub;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.bean.PublicBean;
import com.zxtd.information.bean.PublishBean;
import com.zxtd.information.db.zxtd_PublishInfoDao;
import com.zxtd.information.manager.NewTypeBeansManager;
import com.zxtd.information.manager.PublicManager;
import com.zxtd.information.manager.PublishManager;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.ProgressRequestCallBack;
import com.zxtd.information.parse.PublishNewsParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.pub.ImagesContainer.OnItemClickListener;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ListenFactory;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.MenuDialog;
import com.zxtd.information.view.MenuDialog.OnMenuItemChangeListener;
import com.zxtd.information.view.PublishListDialog.OnPublishItemSelectedListener;
import com.zxtd.information.view.ShowPictureDialog.OnIsOkListener;
import com.zxtd.information.view.ExitDialog;
import com.zxtd.information.view.PictureDialog;
import com.zxtd.information.view.PublishListDialog;
import com.zxtd.information.view.ShowPictureDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PublishActivity extends BaseActivity implements OnClickListener{
	//private LinearLayout btnPublishType;
	private Button btnPublishNew;
	private Button btnSavePublish;
	private Button btnLoadPublish;
	private RelativeLayout btnAddPicture;
	private RelativeLayout btnAddTitle;
	private RelativeLayout btnAddContent;
	private RelativeLayout btnAddMorePic;
	private LinearLayout pubImageArea;
	private ImagesContainer pubImageContainer;
	private Button btnBack;
	private TextView titleText;
	private TextView pubTitleText;
	private TextView pubContentText;
	
	private MenuDialog menuDialog;
	private PublishListDialog listDialog;
	private PictureDialog pictureDialog;
	private ShowPictureDialog showPictureDialog;
	private ProgressDialog progressDialog;
	
	private ArrayList<String> imageFileNames;
	
	private int currentType = 0;
	private int curMode;
	
	private PublishBean currentPublish = null;
	
	private NewTypeBeansManager newTypeBeansManager;
	
	private zxtd_PublishInfoDao publishInfoDao;
	
	private final static int CAMERA_WITH_DATA = 1000;
	private final static int PHOTO_PICKED_WITH_DATA  = 1001;
	private final static int ADD_TITLE_DATA = 1002;
	private final static int ADD_CONTENT_DATA = 1003;
	private final static int SHOW_IMAGE_DATA = 1004;
	
	public final static int PUB_INFO_FLAG_SAVE = 100;
	public final static int PUB_INFO_FLAG_SEND = 101;
	
	//编写模式
	public final static int EDIT_MODE = 0;
	//修改模式
	public final static int MODIFY_MODE = 1;
	
	private Uri currentImageUri = null;
	
	private Handler mHandler = new Handler();
	
	private boolean isContentChanged = false;
	private ExitDialog mExitDialog;
	
	private ProgressRequestCallBack callBack = new ProgressRequestCallBack() {
		
		public void requestSuccess(String requestCode, Result result) {
			progressDialog.dismiss();
			if(result.getBoolean(PublishNewsParseData.IS_SUCCESS)){
				if(currentPublish != null && Utils.isEmpty(currentPublish.newsId)){
					curMode = EDIT_MODE;
				}
				if(curMode==EDIT_MODE){
					//MobclickAgent.onEvent(PublishActivity.this, "publish_success");
					
					if(currentPublish != null){
						if(!publishInfoDao.isHasPublish(currentPublish.id)){
							publishInfoDao.delete(currentPublish.id);
						}
					}
					
					Toast.makeText(PublishActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
					NewTypeBean typeBean = (NewTypeBean)newTypeBeansManager.getNewTypeBeans().get(currentType);
					//Utils.jumpToNetFriendSubList(PublishActivity.this, (NewTypeBean)newTypeBeansManager.getNewTypeBeans().get(currentType));
					ListenFactory.newInstance().changeState("tabChange", 1, currentType);
					String pubUrl = result.getString(PublishNewsParseData.PUB_URL);
					String pubTime = result.getString(PublishNewsParseData.PUB_TIME);
					String pubId = result.getString(PublishNewsParseData.PUB_ID);
					String pubTitle = result.getString("pubTitle");
					String content=result.getString("pubContent");
					
					PublicBean bean=new PublicBean();
					bean.setNewsId(Integer.valueOf(pubId));
					bean.setNewsTitle(pubTitle);
					bean.setContent(content);
					bean.setChannel(typeBean.name);
					bean.setHasNewReplay(false);
					bean.setNewsUrl(pubUrl);
					bean.setPublicTime(pubTime);
					bean.setReplayCount(0);
					boolean isSuccess=PublicManager.getInstance().insertPublic(bean);
					if(isSuccess){
						//通知主页和我的发布页面
						Intent intent=new Intent(Constant.NOTIFY_CACHE_DATA_CHANGED);
						intent.putExtra("type", 0);
						intent.putExtra("data", 1);
						PublishActivity.this.sendBroadcast(intent);
						finish();
					}
				}else{
					if(null!=currentPublish){
						if(!publishInfoDao.isHasPublish(currentPublish.id)){
							publishInfoDao.delete(currentPublish.id);
						}
						PublicBean bean=new PublicBean();
						bean.setNewsId(Integer.valueOf(currentPublish.newsId));
						bean.setNewsTitle(currentPublish.title);
						bean.setChannel(currentPublish.type.name);
						bean.setContent(currentPublish.content);
						boolean isSuccess=PublicManager.getInstance().modifyPublic(bean);
						if(isSuccess){
							Intent intent=new Intent();
							intent.putExtra("publicBean", bean);
							setResult(RESULT_OK,intent);
							finish();
						}
					}
				}
			}else{
				//MobclickAgent.onEvent(PublishActivity.this, "publish_fail");
				Toast.makeText(PublishActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
			}
		}
		
		public void requestError(String requestCode, int errorCode) {
			//MobclickAgent.onEvent(PublishActivity.this, "publish_fail");
			Toast.makeText(PublishActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}
		
		public void progress(String requestCode, String progress) {
			progressDialog.setMessage(progress);
		}
	};
	
	private OnMenuItemChangeListener onMenuItemChangeListener = new OnMenuItemChangeListener() {
		
		public void menuItemChange(String item, int index) {
			currentType = index;
		}
	};
	
	private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			getPicture(which);
		}
	};
	
	private OnPublishItemSelectedListener mOnPublishItemSelectedListener = new OnPublishItemSelectedListener() {
		
		public void onPublishItemSelected(PublishBean publishBean) {
			currentPublish = publishBean;
			loadPublishInfo(publishBean);
		}
	};
	
	private OnIsOkListener mIsOkListener = new OnIsOkListener() {
		
		public void isOk(String fileName) {
			if(!Utils.isEmpty(fileName)){
				addPicture(fileName);
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_pub);
		
		imageFileNames = new ArrayList<String>();
		//btnPublishType = (LinearLayout) this.findViewById(R.id.btn_publish_type);
		btnPublishNew = (Button) this.findViewById(R.id.btn_publish_new);
		btnSavePublish = (Button) this.findViewById(R.id.btn_save_publish);
		btnLoadPublish = (Button) this.findViewById(R.id.btn_load_publish);
		btnAddPicture = (RelativeLayout) this.findViewById(R.id.btn_add_picture);
		btnAddTitle = (RelativeLayout) this.findViewById(R.id.btn_add_title);
		btnAddContent = (RelativeLayout) this.findViewById(R.id.btn_add_content);
		btnAddMorePic = (RelativeLayout)  this.findViewById(R.id.btn_add_more_picture);
		btnBack = (Button) this.findViewById(R.id.back);
		titleText = (TextView) this.findViewById(R.id.title);
		pubImageArea = (LinearLayout) this.findViewById(R.id.pub_news_image_area);
		pubImageContainer = (ImagesContainer) this.findViewById(R.id.pub_news_image_container);
		pubTitleText = (TextView) this.findViewById(R.id.pub_news_title_text);
		pubContentText = (TextView) this.findViewById(R.id.pub_news_content_text);
		
		
		newTypeBeansManager = NewTypeBeansManager.getNewInstance();
		publishInfoDao = new zxtd_PublishInfoDao();
		
		menuDialog = new MenuDialog(this);
		//menuDialog.setView(btnPublishType);
		menuDialog.setMenuValues(newTypeBeansManager.getNewTypeNames());
		
		
		listDialog = new PublishListDialog(this);
		pictureDialog = new PictureDialog(this);
		showPictureDialog = new ShowPictureDialog(this);
		showPictureDialog.setOnIsOkListener(mIsOkListener);
		
		progressDialog = new ProgressDialog(this);
		mExitDialog = new ExitDialog(this);
		mExitDialog.setOnClickListener(mDialogClick);
		
		titleText.setText(R.string.pub_news);
		
		menuDialog.setOnMenuItemChangeListener(onMenuItemChangeListener);
		pictureDialog.setOnClickListener(clickListener);
		btnSavePublish.setOnClickListener(this);
		btnLoadPublish.setOnClickListener(this);
		btnAddPicture.setOnClickListener(this);
		btnAddTitle.setOnClickListener(this);
		btnAddContent.setOnClickListener(this);
		pubTitleText.setOnClickListener(this);
		pubContentText.setOnClickListener(this);
		btnAddMorePic.setOnClickListener(this);
		btnPublishNew.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		listDialog.setOnPublishItemSelectedListener(mOnPublishItemSelectedListener);
		pubImageContainer.setOnItemClickListener(mImageItemClick);
		
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null && bundle.containsKey("id")){
			progressDialog.setMessage("加载数据···");
			progressDialog.show();
			String newsId = bundle.getInt("id") + "";
			loadPublish(newsId);
			curMode = MODIFY_MODE;
		}else{
			curMode = EDIT_MODE;
		}
		
	}

	public boolean setPublishBean(PublishBean publishBean,int flag){
		
		String newTitle = pubTitleText.getText().toString();
		String newContent = pubContentText.getText().toString();
		
		if((Utils.isEmpty(newContent) || Utils.isEmpty(newContent.trim())) && !Utils.isHasImage(imageFileNames)){
			Toast.makeText(this, "内容和图片不能同时为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(Utils.isEmpty(newTitle) || "".equals(newTitle.trim())){
			if(flag == PUB_INFO_FLAG_SAVE){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				newTitle = dateFormat.format(new Date());
			}else{
				Toast.makeText(this, "新闻标题为空", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		
		publishBean.title = newTitle;
		publishBean.content = newContent;
		
		if(!Utils.isHasImage(imageFileNames) && newContent.trim().length() < 10){
			Toast.makeText(this, "请最少输入10个字", Toast.LENGTH_SHORT).show();
			return false;
		}
		publishBean.setImageUrls(imageFileNames);
		Log.i(this.getClass().getName(), "内容：" + publishBean.content);
		if(currentType == -1){
			if(flag == PUB_INFO_FLAG_SAVE){
				publishBean.type = new NewTypeBean();
				publishBean.type.id = "";
				publishBean.type.name = "";
				
			}else{
				
				Toast.makeText(this, "新闻类型加载有误！", Toast.LENGTH_SHORT).show();
				return false;
			}
		}else{
			publishBean.type = (NewTypeBean)newTypeBeansManager.getNewTypeBeans().get(currentType);
		}
		return true;
	}
	
	//保存数据！
	private void savePublishInfo(){
		if(currentPublish == null){
			currentPublish = new PublishBean();
		}
		if(!setPublishBean(currentPublish,PUB_INFO_FLAG_SAVE)){
			return;
		}
		if(currentPublish.id == -1 || publishInfoDao.isHasPublish(currentPublish.id)){
			long id = publishInfoDao.saveInfos(currentPublish);
			if(id == -1){
				Toast.makeText(this, "保存失败！", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
			}
			currentPublish.id = id;
		}else{
			publishInfoDao.updataInfo(currentPublish);
			Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	//加载草稿箱的新闻类容
	private void loadPublishInfo(PublishBean publishBean){
		if(publishBean != null){
			NewTypeBean newTypeBean = publishBean.type;
			currentType = newTypeBeansManager.getNewTypeIndex(newTypeBean);
			setNewsTitle(publishBean.title, currentType);
			setNewsContent(publishBean.content);
			imageFileNames.clear();
			imageFileNames.addAll(publishBean.resolveImageUrls());
			resetImages();
		}
		currentPublish = publishBean;
	}
	
	private void loadPublishList(){
		listDialog.setPublishBeans(publishInfoDao.getInfos());
		listDialog.show();
	}
	
	
	//取图片
	private void getPicture(int which){
		switch (which) {
		case 1:
			doTakePhoto();
			break;

		case 0:
			doPickPhotoFromGallery();
			break;
		}
		
	}
	
	//从相机中获取
	private void doTakePhoto(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file= new File(Constant.SDCARD_CAMERA_PATH, getPhotoFileName());
		currentImageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
		this.startActivityForResult(intent, CAMERA_WITH_DATA);
	}
	
	//从相册中获取
	private void doPickPhotoFromGallery(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		this.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			return;
		}
		String imagePath = null;
		if( requestCode == PHOTO_PICKED_WITH_DATA){
			imagePath = getPathFromPhoto(data);
			if(!Utils.isEmpty(imagePath)){
				showPictureDialog.show(imagePath);
			}
		}else if(requestCode == CAMERA_WITH_DATA){
			imagePath = getPathFromCamera();
			if(!Utils.isEmpty(imagePath)){
				addPicture(imagePath);
			}
		}else if(requestCode == ADD_TITLE_DATA){
			String newsTitle = data.getStringExtra("title");
			int checkedType = data.getIntExtra("newType", 0);
			setNewsTitle(newsTitle, checkedType);
			isContentChanged = true;
		}else if(requestCode == ADD_CONTENT_DATA){
			String content = data.getStringExtra("content");
			setNewsContent(content);
			isContentChanged = true;
		}else if(requestCode == SHOW_IMAGE_DATA){
			ArrayList<String> tem = data.getStringArrayListExtra("images");
			boolean change = data.getBooleanExtra("change", true);
			if(change){
				imageFileNames.clear();
				imageFileNames.addAll(tem);
				resetImages();
				isContentChanged = true;
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
			//System.out.println(uri.getScheme() +  ":" + uri.getPath());
			String scheme = uri.getScheme();
			if("file".equals(scheme)){
				return uri.getPath();
			}else if("content".equals(scheme)){
				String[] projection = {MediaStore.Images.Media.DATA };
				Cursor cursor = this.managedQuery(uri, projection, null, null, null);
				cursor.moveToFirst();
				String path = cursor.getString(0);
				return path;
			}
		}
		return null;
	}
	//添加新闻标题和新闻类型
	private void setNewsTitle(String title, int checkedType){
		changeBtnAndText(btnAddTitle, pubTitleText, Utils.isEmpty(title));
		currentType = checkedType;
		pubTitleText.setText(title);
	}
	//添加新闻内容
	private void setNewsContent(String content){
		changeBtnAndText(btnAddContent, pubContentText, Utils.isEmpty(content));
		pubContentText.setText(content);
	}
	//添加图片
	private void addPicture(String fileName) {
		imageFileNames.add(fileName);
		if(fileName.startsWith("http:")){
			pubImageContainer.addUrl(fileName);
		}else{
			pubImageContainer.addImage(fileName);
		}
		
		changeBtnAndText(btnAddPicture, pubImageArea, imageFileNames.size() == 0);
		if(imageFileNames.size() >= 8){
			btnAddMorePic.setVisibility(View.GONE);
		}else if(btnAddMorePic.getVisibility() == View.GONE){
			btnAddMorePic.setVisibility(View.VISIBLE);
		}
		isContentChanged = true;
	}
	//刷新图片列表
	private void resetImages(){
		pubImageContainer.removeAllViews();
		changeBtnAndText(btnAddPicture, pubImageArea, imageFileNames.size() == 0);
		for (String imageFileName : imageFileNames) {
			if(imageFileName.startsWith("http:")){
				pubImageContainer.addUrl(imageFileName);
			}else{
				pubImageContainer.addImage(imageFileName);
			}
		}
	}
	
	//替换按钮和文本的显示状态
	private void changeBtnAndText(RelativeLayout button, View text, boolean isEmpty){
		int btnVisibile = button.getVisibility();
		int textVisibile = text.getVisibility();
		if(isEmpty){
			if(btnVisibile == View.GONE){
				button.setVisibility(View.VISIBLE);
			}
			if(textVisibile == View.VISIBLE){
				text.setVisibility(View.GONE);
			}
		}else{
			if(btnVisibile == View.VISIBLE){
				button.setVisibility(View.GONE);
			}
			if(textVisibile == View.GONE){
				text.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void onClick(View v) {
		if(v == btnSavePublish){
			savePublishInfo();
			isContentChanged = false;
		}else if(v == btnLoadPublish){
			loadPublishList();
		}else if(v == btnAddPicture || v == btnAddMorePic){
			pictureDialog.show();
		}else if(v == btnPublishNew){
			if(curMode == EDIT_MODE){
				PublishBean pubBean = new PublishBean();
				if(setPublishBean(pubBean,PUB_INFO_FLAG_SEND)){
					progressDialog.show();
					RequestManager.newInstance().publishNewsComm(pubBean,curMode, callBack, mHandler);
				}
			}else{
				if(setPublishBean(currentPublish,PUB_INFO_FLAG_SEND)){
					progressDialog.setMessage("正在发布···");
					progressDialog.show();
					RequestManager.newInstance().publishNewsComm(currentPublish,curMode, callBack, mHandler);
				}
			}
			
		}else if(v == btnBack){
			if(isContentChanged){
				mExitDialog.show();
			}else{
				this.finish();
			}
		}else if(v == btnAddTitle || v == pubTitleText){
			Intent intent = new Intent(this, WriteTitleActivity.class);
			intent.putExtra("title", pubTitleText.getText().toString());
			intent.putExtra("newType", currentType);
			this.startActivityForResult(intent, ADD_TITLE_DATA);
		}else if(v == btnAddContent || v == pubContentText){
			Intent intent = new Intent(this, WriteContentActivity.class);
			intent.putExtra("content", pubContentText.getText().toString());
			this.startActivityForResult(intent, ADD_CONTENT_DATA);
		}
	}
	
	private DialogInterface.OnClickListener mDialogClick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == ExitDialog.CLICK_EXIT){
				PublishActivity.this.finish();
			}else if(which == ExitDialog.CLICK_FINISH){
				savePublishInfo();
				PublishActivity.this.finish();
			}
		}
	};
	
	//缓存发布数据
	private void saveMyPub(String pubUrl, String id, String pubTime, String putTitle, String typeId, String typeName){
		
	}
	
	public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isContentChanged){
				mExitDialog.show();
			}else{
				this.finish();
			}
			return true; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnItemClickListener mImageItemClick = new OnItemClickListener() {
		
		@Override
		public void onItemClick(View v, int position) {
			Intent intent = new Intent(PublishActivity.this, ManageImagesActivity.class);
			intent.putExtra("images", imageFileNames);
			intent.putExtra("index", position);
			PublishActivity.this.startActivityForResult(intent, SHOW_IMAGE_DATA);
		}
	};

	private void loadPublish(final String newsId){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				currentPublish = PublishManager.getPublishBean(newsId);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						progressDialog.dismiss();
						loadPublishInfo(currentPublish);
					}
				});
			}
		}).start();
	}
}
