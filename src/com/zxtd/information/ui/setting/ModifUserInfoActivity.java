package com.zxtd.information.ui.setting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zxtd.information.bean.UserInfo;
import com.zxtd.information.bean.UserInfoBean;
import com.zxtd.information.db.DBHelper;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.down.zxtd_AsyncImageLoader;
import com.zxtd.information.down.zxtd_AsyncImageLoader.ImageListCallback;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.manager.UserInfoManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.Result;
import com.zxtd.information.parse.doc.ModifUserInfoParseData;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.CityActivity;
import com.zxtd.information.ui.me.MineNewActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.PictureDialog;

public class ModifUserInfoActivity extends BaseActivity implements
		OnClickListener {
	private TextView mineShowAccount;
	private EditText mineModifNickname;
	private EditText mineModifSign;
	private TextView mineModifAddress;
	private EditText mineModifWork;
	private RadioGroup radioGroup;
	private TextView mineModifBirth;
	private ImageView mindeModifPortrait;

	private PictureDialog mPictureDialog;

	private UserInfoBean infoBean;
	
	private zxtd_AsyncImageLoader imageLoader;

	private static final int requestCode = 1;
	private static final int GET_CROP_PIC_CODE = 100;
	private final static int CAMERA_WITH_DATA = 1000;
	private final static int PHOTO_PICKED_WITH_DATA = 1001;

	private Uri currentImageUri = null;

	private static final int CROP = 117;
	private static final String PIC_OUT_PATH = Constant.SDCARD_CAMERA_PATH
			+ "tem/";

	private static String newHeader="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modif_user_details);
		initData();
		initView();
		initViewData();
	}

	private void initData() {
		mPictureDialog = new PictureDialog(this);
		File dirPath = new File(PIC_OUT_PATH);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		imageLoader = new zxtd_AsyncImageLoader();
	}

	private void initView() {
		mineShowAccount = (TextView) this
				.findViewById(R.id.mine_show_account);
		mineModifNickname = (EditText) this
				.findViewById(R.id.mine_modif_nickname);
		mineModifNickname.setOnKeyListener(new EditText.OnKeyListener(){
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent keyEvent) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
					mineModifSign.setFocusable(true);
					mineModifSign.setFocusableInTouchMode(true);
					mineModifSign.requestFocus();
					mineModifSign.setSelection(mineModifSign.getText().toString().trim().length());
					return true;
				}
				return false;
			}
		});
		
		mineModifSign = (EditText) this.findViewById(R.id.mine_modif_sign);
		mineModifSign.setOnKeyListener(new EditText.OnKeyListener(){
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent keyEvent) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
					mineModifWork.setFocusable(true);
					mineModifWork.setFocusableInTouchMode(true);
					mineModifWork.requestFocus();
					mineModifWork.setSelection(mineModifWork.getText().toString().trim().length());
					return true;
				}
				return false;
			}
			
		});
		mineModifAddress = (TextView) this
				.findViewById(R.id.mine_modif_address);
		mineModifWork = (EditText) this.findViewById(R.id.mine_modif_work);
		mineModifWork.setOnKeyListener(new EditText.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
				if(keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
					ModifUserInfoActivity.this.hideKeyBoard();
					return true;
				}
				return false;
			}
		});
		
		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
		mineModifBirth = (TextView) this.findViewById(R.id.mine_modif_birth);
		LinearLayout mineModifPassword = (LinearLayout) this
				.findViewById(R.id.mine_modif_password);
		mindeModifPortrait = (ImageView) this
				.findViewById(R.id.mine_modif_portrait);
		Button back = (Button) this.findViewById(R.id.back);
		Button finish = (Button) this.findViewById(R.id.finish);
		TextView title = (TextView) this.findViewById(R.id.title);

		mineModifAddress.setOnClickListener(this);
		mineModifBirth.setOnClickListener(this);
		mineModifPassword.setOnClickListener(this);
		mindeModifPortrait.setOnClickListener(this);
		back.setOnClickListener(this);
		finish.setOnClickListener(this);
		mPictureDialog.setOnClickListener(mSelectGetPicWay);

		title.setText(R.string.modif_user_info);
		
		
		RadioGroup sexGroup=(RadioGroup) findViewById(R.id.radioGroup);
		sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				//Toast(""+checkedId+"    "+Constant.loginUser.getHeader());
				UserInfo info=Constant.loginUser;
				String header=info.getHeader();
				if(header.endsWith("M20130903.png") || header.endsWith("W20130903.png")){
					int index=header.lastIndexOf("/");
					String domin=header.substring(0,index+1);
					
					switch(checkedId){
						case R.id.radioFemale:{
							mindeModifPortrait.setImageResource(R.drawable.default_gril_head);
							newHeader=domin+"W20130903.png";
						}break;
						case R.id.radioMale:{
							//男
						}
						default:{
							mindeModifPortrait.setImageResource(R.drawable.default_boy_head);
							newHeader=domin+"M20130903.png";
						}break;
					}
				}
			}
		});
	}
	
	private void initViewData(){
		UserInfo userInfo = Constant.loginUser;
		if(userInfo != null){
			newHeader=userInfo.getHeader();
			mineShowAccount.setText(userInfo.getUserAccount());
			mineModifNickname.setText(userInfo.getNickName());
			mineModifSign.setText(userInfo.getSign());
			mineModifAddress.setText(userInfo.getArea());
			mineModifBirth.setText(userInfo.getBirth());
			mineModifWork.setText(userInfo.getWork());
			radioGroup.check(sexToRadioId(userInfo.getSex()));
			setUrlImage( userInfo.getHeader(), mindeModifPortrait, 0);
		}
	}
	
	private int sexToRadioId(String sex){
		
		if("0".equals(sex)){
			return R.id.radioMale;
		}else if("1".equals(sex)){
			return R.id.radioFemale;
		}
		return R.id.radioSecret;
	}
	
	private String radioIdToSex(int checkId){
		if(R.id.radioMale == checkId){
			return "0";
		}else if(R.id.radioFemale == checkId){
			return "1";
		}
		return "-1";
	}
	
	private void submitModif(){
		infoBean = new UserInfoBean();
		infoBean.userAccount = mineShowAccount.getText().toString();
		infoBean.aspiration = mineModifSign.getText().toString();
		infoBean.birthday = mineModifBirth.getText().toString();
		infoBean.sex = radioIdToSex(radioGroup.getCheckedRadioButtonId());
		infoBean.nickname = mineModifNickname.getText().toString();
		infoBean.userid = Constant.loginUser.getUserId() + "";
		infoBean.location = mineModifAddress.getText().toString();
		infoBean.profession = mineModifWork.getText().toString();
		RequestManager.newInstance().modifUserInfoComm(infoBean, mCallBack);
	}
	
	private void modifUserInfo(){
		
		hideSoftKeyboard();
		
		UserInfo userInfo = Constant.loginUser;
		userInfo.setArea(infoBean.location);
		userInfo.setBirth(infoBean.birthday);
		userInfo.setNickName(infoBean.nickname);
		userInfo.setSex(infoBean.sex);
		userInfo.setSign(infoBean.aspiration);
		userInfo.setWork(infoBean.profession);
		if(!TextUtils.isEmpty(newHeader)){
			userInfo.setHeader(newHeader);
		}
		UserInfoManager.getInstance().modifyUser(userInfo);
		
		
		Intent intent=new Intent(ModifUserInfoActivity.this,MineNewActivity.class);
		setResult(RESULT_OK,intent);
		finish();
	}

	private void showCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(1989, 0, 1);
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				monthOfYear++;
				String tempMonth = monthOfYear < 10 ? "0" + monthOfYear
						: String.valueOf(monthOfYear);
				String tempDay = dayOfMonth < 10 ? "0" + dayOfMonth
						: String.valueOf(dayOfMonth);
				String date = year + "-" + tempMonth + "-" + tempDay;
				mineModifBirth.setText(date);
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)).show();
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (reqCode == requestCode) {
				String cityName = data.getStringExtra("name");
				mineModifAddress.setText(cityName);
			} else if (reqCode == GET_CROP_PIC_CODE) {
				Bitmap bmp = data.getParcelableExtra("data");
				mindeModifPortrait.setImageBitmap(bmp);
				RequestManager.newInstance().modifPortraitComm(Constant.loginUser.getUserId() + "", bmp, mCallBack);
			} else if (reqCode == PHOTO_PICKED_WITH_DATA) {
				String imagePath = getPathFromPhoto(data);
				if (!Utils.isEmpty(imagePath)) {
					addPicture(imagePath);
				}
			} else if (reqCode == CAMERA_WITH_DATA) {
				String imagePath = getPathFromCamera();
				if (!Utils.isEmpty(imagePath)) {
					addPicture(imagePath);
				}
			}
		}
	}

	private void addPicture(String fileName) {
		Intent intent = getImageClipIntent(fileName);
		this.startActivityForResult(intent, GET_CROP_PIC_CODE);
	}

	private String getPathFromCamera() {
		// this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
		// currentImageUri));
		return currentImageUri.getPath();
	}

	private String getPathFromPhoto(Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = this
					.managedQuery(uri, projection, null, null, null);
			cursor.moveToFirst();
			String path = cursor.getString(0);
			return path;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.mine_modif_address) {
			// 修改所在地
			String dbFile = Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/citys.db";
	        File filePath = new File(dbFile);
	        if (!filePath.exists() || filePath.length()<1024*1024){
	        	DBHelper.copyDataBase();
	        }
			Intent intent = new Intent(this, CityActivity.class);
			startActivityForResult(intent, requestCode);
		} else if (id == R.id.mine_modif_birth) {
			// 修改出生日期
			showCalendar();
		} else if (id == R.id.mine_modif_password) {
			// 修改密码
			Intent intent = new Intent(this, ModifPwdActivity.class);
			this.startActivity(intent);
		} else if (id == R.id.mine_modif_portrait) {
			// 设置头像
			mPictureDialog.show();
		} else if (id == R.id.back) {
			hideSoftKeyboard();
			this.finish();
		} else if (id == R.id.finish) {
			submitModif();
		}
	}

	// 取图片
	private void getPicture(int which) {
		switch (which) {
		case 1:
			doTakePhoto();
			break;

		case 0:
			doPickPhotoFromGallery();
			break;
		}

	}
	//获取剪切图片的Intent
	public Intent getImageClipIntent(String fileName) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("return-data", true);
		return intent;
	}

	// 从相机中获取
	private void doTakePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Constant.SDCARD_CAMERA_PATH, getPhotoFileName());
		currentImageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
		this.startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	// 从相册中获取
	private void doPickPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		this.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	}

	//获取图片名
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";

	}

	public void hideSoftKeyboard(){
		InputMethodManager methodManager = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
		methodManager.hideSoftInputFromWindow(mineModifNickname.getWindowToken(), 0);
	}
	
	private DialogInterface.OnClickListener mSelectGetPicWay = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			getPicture(which);
		}
	};
	
	/**
	 * 设置网络图片
	 * */
	private void setUrlImage(String url, ImageView imageView, int position){
		//Log.i(this.getClass().getName(), "图片url：" + url);
		Drawable drawable = imageLoader.loadCacheDrawable(url, imageView, position,new ImageListCallback() {
			public void imageLoaded(Drawable imageDrawable, View v, int position) {
				if(imageDrawable != null){
					((ImageView) v).setImageDrawable(imageDrawable);
					imageDrawable = null;
				}else{
					((ImageView) v).setImageResource(R.drawable.mine_user_logo);
				}
			}
		}, zxtd_ImageCacheDao.Instance());
		if(drawable != null){
			imageView.setImageDrawable(drawable);
			drawable = null;
		}else{
			imageView.setImageResource(R.drawable.mine_user_logo);
		}
	}
	
	private RequestCallBack mCallBack = new RequestCallBack() {
		
		@Override
		public void requestSuccess(String requestCode, Result result) {
			if(Constant.RequestCode.MINE_MODIF_USERINFO.equals(requestCode)){
				String resultCode = result.getString(ModifUserInfoParseData.RESULT_CODE);
				if("1".equals(resultCode)){
					Toast("修改成功！");
					modifUserInfo();
				}else{
					Toast("修改失败！");
				}
			}else if(Constant.RequestCode.MINE_MODIF_PORTRAIT.equals(requestCode)){
				String url = result.getString("imageUrl");
				newHeader=url;
				Constant.loginUser.setHeader(url);
				UserInfoManager.getInstance().modifyUser(Constant.loginUser);
				Toast("修改头像成功！");
			}
		}
		
		@Override
		public void requestError(String requestCode, int errorCode) {
			Toast("网络不给力！");
		}
	};
}
