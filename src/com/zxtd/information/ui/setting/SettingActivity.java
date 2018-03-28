package com.zxtd.information.ui.setting;

import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.TabBaseActivity;
import com.zxtd.information.ui.me.im.SearchUserActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.XmppUtils;
import com.zxtd.information.view.FontTypeSettingDialog;
import com.zxtd.information.view.FontTypeSettingDialog.FontTypeCallback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * 设置
 * */
public class SettingActivity extends TabBaseActivity implements OnClickListener{
	private TextView fontTypeText;
	private TextView switchState;
	private TextView aboutTextView;
	private TextView feedbackTextView;
	private TextView checkUpdateTextView;
	private TextView noImageModeText;
	private TextView clickDisclaimerText;
	private CheckBox checkBox;
	private CheckBox noImageModeState;
	private LinearLayout fontSizeLinearLayout;
	
	private TextView btnBindUser;
	private TextView btnSearchUser;
	private TextView btnLoginOut;
	
	private TextView soundOnoffText;
	private TextView shockOnoffText;
	private TextView privateLetterOnoffText;
	private TextView fansOnoffText;
	private TextView commentOnoffText;
	private CheckBox soundOnoffCheck;
	private CheckBox shockOnoffCheck;
	private CheckBox privateLetterOnoffCheck;
	private CheckBox fansOnoffCheck;
	private CheckBox commentOnoffCheck;
	
	
	private String fontType = null;
	private boolean mIsImageMode  = true;
	private boolean isSoundOn = false;
	private boolean isShockOn = false;
	private boolean isPrivateLetterOn = false;
	private boolean isFansOn = false;
	private boolean isCommentOn = false;
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.check_push_onoff_switch){
				if(isChecked) {
					switchState.setText("开启");
					Utils.isPush = true;
				}else {
					switchState.setText("关闭");
					Utils.isPush = false;
				}
				Utils.changePushState(SettingActivity.this);
			}else if(buttonView.getId() == R.id.check_no_image_onoff_switch){
				if(isChecked){
					mIsImageMode = false;
					noImageModeText.setText("开启");
				}else{
					mIsImageMode = true;
					noImageModeText.setText("关闭");
				}
			}else if(buttonView == soundOnoffCheck){
				//设置声音
				stateView(isChecked, soundOnoffText);
				isSoundOn = isChecked;
				Utils.save(getApplicationContext(), Constant.DataKey.IS_SOUND_ON, isSoundOn);
			}else if(buttonView == shockOnoffCheck){
				//设置震动
				stateView(isChecked, shockOnoffText);
				isShockOn = isChecked;
				Utils.save(getApplicationContext(), Constant.DataKey.IS_SHOCK_ON, isShockOn);
			}else if(buttonView == privateLetterOnoffCheck){
				//设置私信音
				stateView(isChecked, privateLetterOnoffText);
				isPrivateLetterOn = isChecked;
				Utils.save(getApplicationContext(), Constant.DataKey.IS_PRIVATE_LETTER_ON, isPrivateLetterOn);
			}else if(buttonView == fansOnoffCheck){
				//设置粉丝音
				stateView(isChecked, fansOnoffText);
				isFansOn = isChecked;
				Utils.save(getApplicationContext(), Constant.DataKey.IS_FANS_ON, isFansOn);
			}else if(buttonView == commentOnoffCheck){
				//设置回复音
				stateView(isChecked, commentOnoffText);
				isCommentOn = isChecked;
				Utils.save(getApplicationContext(), Constant.DataKey.IS_COMMENT_ON, isCommentOn);
			}
		}
	};
	
	private FontTypeCallback typeCallback = new FontTypeCallback() {
		
		public void refreshFontTypeTextView(String text) {
			fontType = text;
			fontTypeText.setText(text);
		}
	};
	
	private OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			if (v.getId() == R.id.setting_about_id) {
				Intent intent = new Intent(SettingActivity.this,
						AboutActivity.class);
				startActivity(intent);
			} else if (v.getId() == R.id.setting_feedback_id) {
				// 调用反馈提供的接口，进入反馈界面
				 FeedbackAgent agent = new
				 FeedbackAgent(SettingActivity.this);
				 agent.startFeedbackActivity();
			} else if (v.getId() == R.id.setting_check_update_id) {
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(
									SettingActivity.this, updateInfo);
							break;
						case 1: // has no update
							Toast.makeText(SettingActivity.this, "没有更新",
									Toast.LENGTH_SHORT).show();
							break;
						case 2: // none wifi
							Toast.makeText(SettingActivity.this,
									"没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
									.show();
							break;
						case 3: // time out
							Toast.makeText(SettingActivity.this, "超时",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});

				UmengUpdateAgent
						.setDownloadListener(new UmengDownloadListener() {
							@Override
							public void OnDownloadEnd(int arg0, String arg1) {
								Toast.makeText(SettingActivity.this,
										"下载路径：" + arg1, Toast.LENGTH_SHORT)
										.show();
							}

							@Override
							public void OnDownloadStart() {
							}

							@Override
							public void OnDownloadUpdate(int arg0) {
							}
						});
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.update(SettingActivity.this);
			} else if (v.getId() == R.id.font_setting_linearlayout) {
				FontTypeSettingDialog typeSettingDialog = new FontTypeSettingDialog(
						SettingActivity.this, typeCallback, fontType);
				typeSettingDialog.show();
			} else if (v.getId() == R.id.setting_disclaimer_id) {
				Intent intent = new Intent(SettingActivity.this,
						DisclaimerActivity.class);
				startActivity(intent);
			} else if (v == btnBindUser) {
				// 绑定用户
				Intent intent = new Intent(SettingActivity.this,
						ThirdBindActivity.class);
				SettingActivity.this.startActivity(intent);
			} else if (v == btnSearchUser) {
				// 搜索用户
				Intent intent = new Intent(SettingActivity.this,
						SearchUserActivity.class);
				startActivity(intent);
			} else if (v == btnLoginOut) {
				
				btnLoginOut.setVisibility(View.GONE);
				SharedPreferences shareds = getSharedPreferences(
						Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
				Editor editor = shareds.edit();
				// editor.remove("xmpp"+XmppUtils.getUserId());
				editor.remove("userId");
				editor.remove("xmpppassword");
				editor.remove("xmppRegisted");

				Constant.loginUser = null;
				XmppUtils.closeConnection();
				editor.commit();

				CacheManager.CACHEVERSION.clear();
				CacheManager.LOCALVERSION.clear();
				
				Intent intent = new Intent(Constant.UPDATE_MINE_TABHOST_ITEM);
				intent.putExtra("action", "loginout");
				sendBroadcast(intent);
				
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setting_main);
		initData();
		initView();
	}
	
	private void initData(){
		String tempFontType = Utils.load(this, Constant.DataKey.FONTTYPESIZE);
		if(Utils.isEmpty(tempFontType)){
			fontType = "中号";
		}else{
			fontType = tempFontType;
		}
		mIsImageMode = Utils.isImageMode;
		
		isSoundOn = Utils.load(this, Constant.DataKey.IS_SOUND_ON, false);
		isShockOn = Utils.load(this, Constant.DataKey.IS_SHOCK_ON, false);
		isPrivateLetterOn = Utils.load(this, Constant.DataKey.IS_PRIVATE_LETTER_ON, false);
		isFansOn = Utils.load(this, Constant.DataKey.IS_FANS_ON, false);
		isCommentOn = Utils.load(this, Constant.DataKey.IS_COMMENT_ON, false);
	}
	
	private void initView(){
		TextView titleText = (TextView)this.findViewById(R.id.title);
		titleText.setText(R.string.setting_title);
		switchState = (TextView)findViewById(R.id.push_state);
		aboutTextView = (TextView)findViewById(R.id.setting_about_id);
		feedbackTextView = (TextView)findViewById(R.id.setting_feedback_id);
		checkUpdateTextView = (TextView)findViewById(R.id.setting_check_update_id);
		noImageModeText = (TextView)findViewById(R.id.setting_no_image_state);
		fontTypeText = (TextView)findViewById(R.id.setting_font_type);
		checkBox = (CheckBox)findViewById(R.id.check_push_onoff_switch);
		noImageModeState = (CheckBox)findViewById(R.id.check_no_image_onoff_switch);
		fontSizeLinearLayout = (LinearLayout)findViewById(R.id.font_setting_linearlayout);
		clickDisclaimerText = (TextView)findViewById(R.id.setting_disclaimer_id);
		//账户
		btnBindUser = (TextView) this.findViewById(R.id.setting_bind_user);
		btnSearchUser = (TextView) this.findViewById(R.id.setting_search_user);
		btnLoginOut = (TextView) this.findViewById(R.id.setting_login_out);
		//新消息提醒
		soundOnoffText = (TextView) this.findViewById(R.id.sound_onoff_text);
		shockOnoffText = (TextView) this.findViewById(R.id.shock_onoff_text);
		privateLetterOnoffText = (TextView) this.findViewById(R.id.private_letter_onoff_text);
		fansOnoffText = (TextView) this.findViewById(R.id.fans_onoff_text);
		commentOnoffText = (TextView) this.findViewById(R.id.comment_onoff_text);
		soundOnoffCheck = (CheckBox) this.findViewById(R.id.check_no_sound_onoff_switch);
		shockOnoffCheck = (CheckBox) this.findViewById(R.id.check_no_shock_onoff_switch);
		privateLetterOnoffCheck = (CheckBox) this.findViewById(R.id.check_no_private_letter_onoff_switch);
		fansOnoffCheck = (CheckBox) this.findViewById(R.id.check_no_fans_onoff_switch);
		commentOnoffCheck = (CheckBox) this.findViewById(R.id.check_no_comment_onoff_switch);
		
		Button btnCompose=(Button) findViewById(R.id.btn_compose);
		btnCompose.setVisibility(View.GONE);
		
		checkBox.setChecked(Utils.isPush);
		noImageModeState.setChecked(!mIsImageMode);
		//消息提醒设置初始值
		soundOnoffCheck.setChecked(isSoundOn);
		shockOnoffCheck.setChecked(isShockOn);
		privateLetterOnoffCheck.setChecked(isPrivateLetterOn);
		fansOnoffCheck.setChecked(isFansOn);
		commentOnoffCheck.setChecked(isCommentOn);
		stateView(isSoundOn, soundOnoffText);
		stateView(isShockOn, shockOnoffText);
		stateView(isPrivateLetterOn, privateLetterOnoffText);
		stateView(isFansOn, fansOnoffText);
		stateView(isCommentOn, commentOnoffText);
		
		fontTypeText.setText(fontType);
		
		checkBox.setOnCheckedChangeListener(checkedChangeListener); 
		noImageModeState.setOnCheckedChangeListener(checkedChangeListener);
		soundOnoffCheck.setOnCheckedChangeListener(checkedChangeListener);
		shockOnoffCheck.setOnCheckedChangeListener(checkedChangeListener);
		privateLetterOnoffCheck.setOnCheckedChangeListener(checkedChangeListener);
		fansOnoffCheck.setOnCheckedChangeListener(checkedChangeListener);
		commentOnoffCheck.setOnCheckedChangeListener(checkedChangeListener);
		
		aboutTextView.setOnClickListener(clickListener);
		feedbackTextView.setOnClickListener(clickListener);
		checkUpdateTextView.setOnClickListener(clickListener);
		fontSizeLinearLayout.setOnClickListener(clickListener);
		clickDisclaimerText.setOnClickListener(clickListener);
		btnBindUser.setOnClickListener(clickListener);
		btnSearchUser.setOnClickListener(clickListener);
		btnLoginOut.setOnClickListener(clickListener);
	}

	
	private void stateView(boolean isChecked, TextView textView){
		if(isChecked){
			textView.setText("当前：开启");
		}else{
			textView.setText("当前：关闭");
		} 
	}
	
	public void onClick(View v) {
		if(v.getId() == R.id.btn_compose){
			Utils.jumpPubNews(this);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(mIsImageMode != Utils.isImageMode){
			Utils.isImageMode = mIsImageMode;
			Utils.isDataChange = true;
			Utils.save(this, Constant.DataKey.IMAGE_MODE, String.valueOf(Utils.isImageMode));
		}
		Utils.save(this, Constant.DataKey.ISPUSH, String.valueOf(Utils.isPush));
		Utils.save(this, Constant.DataKey.FONTTYPESIZE, fontType);
		Utils.fontSize = getFontSizeInt(fontType);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getUserId() != -1){
			btnLoginOut.setVisibility(View.VISIBLE);
		}
	} 

	public static int getFontSizeInt(String fontType){
		if("大号".equals(fontType)){
			return 3;
		}else if("中号".equals(fontType)){
			return 2;
		}else if("小号".equals(fontType)){
			return 1;
		}else{
			return 2;
		}
	}
}
