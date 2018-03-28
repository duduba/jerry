package com.zxtd.information.ui.setting;

import com.renn.rennsdk.RennClient;
import com.tencent.tauth.Tencent;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.AuthUtitl;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.AuthUtitl.OnAuthListener;
import com.zxtd.information.util.AuthUtitl.OnAuthListener.AuthType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ThirdBindActivity extends Activity implements OnClickListener{
	
	private Button btnSinaBind;
	private Button btnTencentBind;
	private Button btnRennBind;
	private Button btnQQBind;
	
	private SsoHandler ssoHandler;
	
	private boolean isRegister = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.third_bind_list);
		
		initData();
		initView();
		initViewData();
	}
	
	private void initData(){
		
	}
	
	private void initView(){
		Button back = (Button)this.findViewById(R.id.back);
		TextView title = (TextView) this.findViewById(R.id.title);
		btnSinaBind = (Button) this.findViewById(R.id.btn_sina_bind);
		btnTencentBind = (Button) this.findViewById(R.id.btn_tencent_bind);
		btnRennBind = (Button) this.findViewById(R.id.btn_renn_bind);
		btnQQBind = (Button) this.findViewById(R.id.btn_qq_bind);
		
		back.setOnClickListener(this);
		btnSinaBind.setOnClickListener(this);
		btnTencentBind.setOnClickListener(this);
		btnRennBind.setOnClickListener(this);
		btnQQBind.setOnClickListener(this);
		
		title.setText(R.string.third_bind);
	}
	
	private void initViewData(){
		setBtnState(btnSinaBind, AuthUtitl.checkSinaAuth(this));
		setBtnState(btnTencentBind, AuthUtitl.checkTencentAuth(this));
		setBtnState(btnRennBind, AuthUtitl.checkRennAuth(this));
		setBtnState(btnQQBind, AuthUtitl.checkQQAuth(this));
	}
	
	private void setBtnState(Button button, boolean check){
		if(check){
			button.setText("取消绑定");
			button.setBackgroundResource(R.drawable.btn_bind_selector);
			button.setTag(true);
		}else{
			button.setText("+绑定");
			button.setBackgroundResource(R.drawable.btn_bind_selector);
			button.setTag(false);
		}
	}
	
	private void changeBtnState(Button button, AuthType authType){
		boolean check = (Boolean)button.getTag();
		if(check){
			clearToken(authType);
			setBtnState(button, !check);
		}else{
			switch (authType) {
			case SINA:
				Weibo sinaweibo = Weibo.getInstance(Constant.CONSUMER_KEY, Constant.REDIRECT_URL);
				Weibo.setApp_secret(Constant.SINA_KEY);
				ssoHandler = new SsoHandler(this, sinaweibo);
				AuthUtitl.doSinaOAuthLogin(this, sinaweibo, ssoHandler, mAuthListener);
				break;
			case TENCENT:
				AuthUtitl.doTencentOAuthLogin(this, mAuthListener);
				isRegister = true;
				break;
			case RENN:
				AuthUtitl.doRennOAuthLogin(this, mAuthListener);
				break;
			case QQ:
				AuthUtitl.doQQLogin(Tencent.createInstance(Constant.APP_ID, this.getApplicationContext()),this, mAuthListener);
				break;
			}
		}
	}
	
	private void clearToken(AuthType authType){
		switch (authType) {
		case SINA:
			SharedPreferences preferences = this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_APPEND);
			Editor editor = preferences.edit();
			editor.remove("SINA_uid");
			editor.remove("SINA_token");
			editor.remove("SINA_expires_in");
			editor.commit();
			break;
		case TENCENT:
			Util.clearSharePersistent(this);
			break;
		case RENN:
			RennClient.getInstance(this).logout();
			break;
		case QQ:
			SharedPreferences shareds = this.getSharedPreferences(
					Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			Editor editorQQ = shareds.edit();
			editorQQ.remove("QQ_ret");
			editorQQ.remove("QQ_pay_token");
			editorQQ.remove("QQ_pf");
			editorQQ.remove("QQ_expires_in");
			editorQQ.remove("QQ_openid");
			editorQQ.remove("QQ_pfkey");
			editorQQ.remove("QQ_access_token");
			editorQQ.commit();
			break;
		}
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if(id == R.id.back){
			this.finish();
		}else if(id == R.id.btn_sina_bind){
			changeBtnState(btnSinaBind, AuthType.SINA);
		}else if(id == R.id.btn_tencent_bind){
			changeBtnState(btnTencentBind, AuthType.TENCENT);
		}else if(id == R.id.btn_renn_bind){
			changeBtnState(btnRennBind, AuthType.RENN);
		}else if(id == R.id.btn_qq_bind){
			changeBtnState(btnQQBind, AuthType.QQ);
		}
	}
	
	private OnAuthListener mAuthListener = new OnAuthListener() {
		
		@Override
		public void success(AuthType type) {
			switch (type) {
			case SINA:
				setBtnState(btnSinaBind, true);
				break;
			case TENCENT:
				setBtnState(btnTencentBind, true);
				break;
			case RENN:
				setBtnState(btnRennBind, true);
				break;
			case QQ:
				setBtnState(btnQQBind, true);
				break;
			}
		}
		
		@Override
		public void fail(AuthType type) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(ssoHandler != null){
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		if(requestCode == 1000){
			setBtnState(btnTencentBind, AuthUtitl.checkTencentAuth(this));
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isRegister){
			AuthHelper.unregister(this);
		}
	}
	
}
