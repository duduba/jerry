package com.zxtd.information.ui.news;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.Tencent;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.ImageBean;
import com.zxtd.information.bean.NewBean;
import com.zxtd.information.db.zxtd_ImageCacheDao;
import com.zxtd.information.manager.CurrentManager;
import com.zxtd.information.parse.json.ParseInfoImages;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.LoadingDialog;
import com.zxtd.information.util.AuthUtitl;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ShareUtils;
import com.zxtd.information.util.Utils;
import com.zxtd.information.util.AuthUtitl.OnAuthListener;
import com.zxtd.information.view.RadioImageSelector;

public class ShareActivity extends BaseActivity implements OnClickListener{
	
	private RadioImageSelector mRadioImageSelector;
	private TextView mTxtWordCount;
	private EditText mEdtShareContent;
	
	private String shareContent;
	private String shareImageUrl;
	
	private int mWhich_channel = 0;
	private int wordCount = 0;
	private int txtMaxLen = Integer.MAX_VALUE;
	private List<String> imgUrls;
	
	private zxtd_ImageCacheDao imageCacheDao;
	private SsoHandler ssoHandler;
	
	private LoadingDialog loadingDialog;
	
	private String infoUrl;
	private Tencent tencent;
	
	private boolean isRegister = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.share_edit_view);
		initData();
		try {
			initView();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initData(){
		Bundle extras = this.getIntent().getExtras();
		String imageJson = "";
		if (extras != null) {
			imageJson = extras.getString(Constant.BundleKey.IMGE_DATA);
			mWhich_channel = extras.getInt(Constant.BundleKey.WHICH_CHANNEL);
			Log.i(this.getClass().getName(), "json:" + imageJson);
		}
		imgUrls = new ArrayList<String>();
		if(!Utils.isEmpty(imageJson) && !Utils.isEmpty(imageJson.trim())){
			ParseInfoImages infoImages = new ParseInfoImages();
			infoImages.parse(imageJson);
			List<ImageBean> imageBeans = infoImages.getImageBeans();
			for (int i = 0; i < 3 && i < imageBeans.size(); i++) {
				ImageBean imageBean = imageBeans.get(i);
				imgUrls.add(imageBean.imageUrl);
			}
		}
		imageCacheDao = zxtd_ImageCacheDao.Instance();
		
	}
	
	private void initView() throws UnsupportedEncodingException{
		Button btnShare = (Button) this.findViewById(R.id.finish);
		Button btnBack = (Button) this.findViewById(R.id.back);
		TextView title = (TextView) this.findViewById(R.id.title);
		mRadioImageSelector = (RadioImageSelector) this.findViewById(R.id.image_selector);
		mTxtWordCount = (TextView) this.findViewById(R.id.txt_word_count);
		mEdtShareContent = (EditText) this.findViewById(R.id.edt_share_content);
		
		btnShare.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		mEdtShareContent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				wordCount -= arg2;
				wordCount += arg3;
				mTxtWordCount.setText(wordCount + "/" + txtMaxLen);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		mRadioImageSelector.addImages(imgUrls);
		mRadioImageSelector.setItemSpace(10);
		btnShare.setText("分享");
		title.setText(R.string.new_share);
		NewBean newBean = (NewBean)CurrentManager.newInstance().getBean();
		if(newBean != null){
			if(mWhich_channel == 1){
				int idx = newBean.infoUrl.indexOf("?");
				infoUrl = newBean.infoUrl.substring(0, idx + 1) + URLEncoder.encode( newBean.infoUrl.substring(idx + 1)+ "&version=" + ZXTD_Application.versionName + "&device=0&channel=" + mWhich_channel, "utf-8");
			}else{
				infoUrl = newBean.infoUrl + "&version=" + ZXTD_Application.versionName + "&device=0&channel=" + mWhich_channel;
			}
			mEdtShareContent.setText("【" + newBean.newTitle + "】");
		}else{
			Toast("新闻是空的");
			this.finish();
		}
		
		mEdtShareContent.setSelection(0);
		
		if(mWhich_channel > 1 && mWhich_channel < 5){
			mTxtWordCount.setVisibility(View.GONE);
		}else{
			txtMaxLen = 100;
		}
		mEdtShareContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(txtMaxLen)});
		mTxtWordCount.setText(wordCount + "/" + txtMaxLen);
		loadingDialog = new LoadingDialog(this, R.style.loaddialog);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.finish){
			//分享
			shareOnClick();
		}else if(id == R.id.back){
			this.finish();
		}
	}
	
	private String getTag(String userName){
		return  "分享自" + userName + "，下载查看：http://www.zxtd.net/product.html ";
	}
	
	private void shareOnClick(){
		shareContent = mEdtShareContent.getText().toString();
		int index = mRadioImageSelector.getSelectIndex();
		if(index != -1){
			shareImageUrl = imgUrls.get(index);
		}
		switch (mWhich_channel) {
		case 0:
			doSinaShare();
			break;
		case 1:
			doTencentShare();
			break;
		case 4:
			doQQShare();
			break;
		case 5:
			doRennShare();
			break;
		}
	}
	//新浪微博分享
		private void doSinaShare(){
			SharedPreferences shareds=this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			String accessToken = shareds.getString("SINA_token", null);
			String expiresIn = shareds.getString("SINA_expires_in", null);
			Oauth2AccessToken accessToken2 = new Oauth2AccessToken();
			accessToken2.setToken(accessToken);
			if(!Utils.isEmpty(expiresIn)){
				accessToken2.setExpiresIn(expiresIn);
			}
			Weibo sinaweibo = Weibo.getInstance(Constant.CONSUMER_KEY, Constant.REDIRECT_URL);
			Weibo.setApp_secret(Constant.SINA_KEY);
			if(accessToken2.isSessionValid()){
				loadingDialog.show();
				sinaweibo.accessToken = accessToken2;
				ShareUtils.doSinaShare(sinaweibo, shareContent + infoUrl + getTag("@杂色客户端"), imageCacheDao.getImageCacheFile(shareImageUrl) , new RequestListener() {
					
					public void onIOException(IOException e) {
						AuthUtitl.showAycToast(e.getMessage());
						loadingDialog.dismiss();
					}
					
					public void onError(WeiboException e) {
						AuthUtitl.showAycToast(e.getMessage());
						loadingDialog.dismiss();
					}
					
					public void onComplete(String response) {
						AuthUtitl.showAycToast("分享成功！");
						loadingDialog.dismiss();
						ShareActivity.this.finish();
					}
				});
			}else{
				ssoHandler = new SsoHandler(this, sinaweibo);
				AuthUtitl.doSinaOAuthLogin(this, sinaweibo, ssoHandler, mAuthListener);
			}
			
		}
		
		
		
		
		//腾讯微博分享
		private void doTencentShare(){
			String accessToken = Util.getSharePersistent(this, "ACCESS_TOKEN");
			if(!Utils.isEmpty(accessToken)){
				loadingDialog.show();
				//getTag("@BJ_zxtd")
				try {
					ShareUtils.doTencentShare(accessToken,  shareContent + infoUrl + "分享自@BJ_zxtd，下载查看：" + URLEncoder.encode("http://www.zxtd.net", "utf-8") , shareImageUrl, new HttpCallback() {
						
						@Override
						public void onResult(Object arg0) {
							AuthUtitl.showToast("已分享");
							loadingDialog.dismiss();
							ShareActivity.this.finish();
						}
					});
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				AuthUtitl.doTencentOAuthLogin(this, mAuthListener);
				isRegister = true;
			}
			
		}
		
		
		//人人网分享
		private void doRennShare(){
			RennClient client = RennClient.getInstance(this);
			if(client.isLogin()){
				loadingDialog.show();
				NewBean newBean = (NewBean)CurrentManager.newInstance().getBean();
				String description = newBean.newContent;
				if(Utils.isEmpty(description)){
					description = "分享来自杂色";
				}else if(description.length() > 200){
					description = description.substring(0, 190);
				}
				ShareUtils.doRenRenShare( client, newBean.newTitle, infoUrl,description, shareContent , shareImageUrl, new CallBack() {
					@Override
					public void onSuccess(RennResponse arg0) {
						AuthUtitl.showToast("已分享");
						Log.i("RenRen", arg0.toString());
						loadingDialog.dismiss();
						ShareActivity.this.finish();
					}
					
					@Override
					public void onFailed(String arg0, String arg1) {
						Log.e("RenRen", arg0 + " : " + arg1);
						loadingDialog.dismiss();
					}
				});
			}else{
				AuthUtitl.doRennOAuthLogin(this, mAuthListener);
			}
		}
		
		
		
		private void doQQShare(){
			SharedPreferences shareds= this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
			String token = shareds.getString("QQ_access_token", null);
			String expires_in = shareds.getString("QQ_expires_in", null);
			String openId = shareds.getString("QQ_openid", null);
			if(tencent == null){
				tencent = Tencent.createInstance(Constant.APP_ID, this.getApplicationContext());
			}
			
			
			if(!Utils.isEmpty(token) || tencent.isSessionValid()){
				if(tencent.getAccessToken() == null || tencent.getOpenId() == null){
					tencent.setAccessToken(token, expires_in);
					tencent.setOpenId(openId);
				}
				NewBean newBean = (NewBean)CurrentManager.newInstance().getBean();
				loadingDialog.show();
				ShareUtils.doQQShare(tencent, newBean.newTitle, infoUrl, newBean.newContent, shareContent + getTag("杂色"), shareImageUrl, new IRequestListener() {

					@Override
					public void onComplete(JSONObject arg0, Object arg1) {
						loadingDialog.dismiss();
						AuthUtitl.showAycToast("已分享");
						ShareActivity.this.finish();
					}

					@Override
					public void onConnectTimeoutException(
							ConnectTimeoutException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onHttpStatusException(HttpStatusException arg0,
							Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onIOException(IOException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onJSONException(JSONException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onMalformedURLException(
							MalformedURLException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onNetworkUnavailableException(
							NetworkUnavailableException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onSocketTimeoutException(
							SocketTimeoutException arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}

					@Override
					public void onUnknowException(Exception arg0, Object arg1) {
						AuthUtitl.showAycToast("分享失败");
						loadingDialog.dismiss();
					}
					
				});
			}else{
				AuthUtitl.doQQLogin(tencent, this, mAuthListener);
			}
		}

		private OnAuthListener mAuthListener = new OnAuthListener() {
			@Override
			public void fail(AuthType type) {
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
			@Override
			public void success(AuthType type) {
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
			
		};

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if(ssoHandler != null){
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}else if(tencent != null){
				tencent.onActivityResult(requestCode, resultCode, data);
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
