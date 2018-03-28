package com.zxtd.information.ui;

import com.umeng.analytics.MobclickAgent;
import com.zxtd.information.util.Constant;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {

	protected void hiddleTitleBar(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
		 //     WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/**
	 * 获取屏幕大小
	 * @return
	 */
	protected int[] getScreenSize(){
		int[] dimision=new int[2];
		DisplayMetrics display=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		dimision[0]=display.widthPixels;
		dimision[1]=display.heightPixels;
		return dimision;
	}
	
	
	protected void Toast(String msg){
		Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	protected void Toast(int resId){
		Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_LONG).show();
	}
	
	
	
	/**
	 * 显示进度条
	 * @param icon
	 * @param title
	 * @param message
	 * @return
	 */
	protected ProgressDialog showProgress(int icon,String title,String message){
		ProgressDialog dialog=new ProgressDialog(BaseActivity.this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		if(icon!=-1){
			dialog.setIcon(icon);
		}
		if(!TextUtils.isEmpty(title)){
			dialog.setTitle(title);
		}
		dialog.setMessage(message);
		return dialog;
	}
	
	
	/**
	 * 
	 * @param icon
	 * @param title
	 * @param message
	 * @return
	 */
	protected ProgressDialog showProgress(int icon,int title,int message){
		ProgressDialog dialog=new ProgressDialog(BaseActivity.this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		if(icon!=-1){
			dialog.setIcon(icon);
		}
		if(title!=-1){
			dialog.setTitle(title);
		}
		dialog.setMessage(getString(message));
		return dialog;
	}
	
	
	protected ProgressDialog showProgress(int icon,String title,int message){
		ProgressDialog dialog=new ProgressDialog(BaseActivity.this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		if(icon!=-1){
			dialog.setIcon(icon);
		}
		if(!TextUtils.isEmpty(title)){
			dialog.setTitle(title);
		}
		dialog.setMessage(getString(message));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return dialog;
	}
	
	/**
	 * 检查是否有可用网络
	 * @return
	 */
	protected boolean checkNerWork(){
		ConnectivityManager manager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		if(null==info || !info.isAvailable()){
			Toast("网络已断开,请检查网络");
			return false;
		}else{
			Toast("网络不稳定或系统程序异常");
			return true;
		}
	}
	
	protected boolean hasNetWork(){
		ConnectivityManager manager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		if(null==info || !info.isAvailable())
			return false;
		return true;
	}
	
	protected int getUserId(){
		if(null==Constant.loginUser || Constant.loginUser.getUserId()<1){
			SharedPreferences shareds=getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			return shareds.getInt("userId", -1);
		}else{
			return Constant.loginUser.getUserId();
		}
	}
	
	
	/**
	 * 检查sdcard是否存在
	 * @return
	 */
	protected boolean checkSdcard(){
		final String state=Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}
	
	
	protected void setNetwork(){
		AlertDialog.Builder builder=new AlertDialog.Builder(BaseActivity.this);
		builder.setTitle("开启网络");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS),0);
			}
		});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	
	protected void hideKeyBoard(){
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		//Log.e(Constant.TAG, "  "+imm.isActive());
		if (imm.isActive()){  //一直是true
			   imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
			     InputMethodManager.HIDE_NOT_ALWAYS);
		}
		//int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; 
		//getWindow().addFlags(flags); 
	}
	
	
	protected void editToHideKeyBoard(final EditText edit){
		edit.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				String str=s.toString();
				String temp="";
				if(!TextUtils.isEmpty(str)){
					temp=str.substring(str.length()-1);
				}
				if(temp.equals("\n")){
					hideKeyBoard();
					edit.setText(str.substring(0,str.length()-1));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	protected String getImsi(){
		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getSubscriberId();
	}
	
	
	protected void titleToCenter(TextView txtView){
		int width=getScreenSize()[0];
		float btnBackWidth=getResources().getDimension(R.dimen.new_info_btn_width);
		float marginLeft=10;
		float marginRight=10;
		float btnBackMarginLeft=getResources().getDimension(R.dimen.new_list_item_pad14);
		
		float size=txtView.getTextSize()*txtView.getTextScaleX();
		
		Paint paint = new Paint();
		paint.setTextSize(size);
		float text_width = paint.measureText(txtView.getText().toString().trim());//得到总体长度
		if(text_width<(width-btnBackWidth-marginLeft-marginRight-btnBackMarginLeft)){
			float paddingLeft=Math.abs((width/2.0f)-btnBackWidth-20-text_width/2);
			txtView.setPadding((int)Math.floor(paddingLeft), 0, 0, 0);
		}
	}
	
}
