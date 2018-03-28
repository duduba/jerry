package com.zxtd.information.ui.me;

import java.util.regex.Pattern;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zxtd.information.adapter.AutoTextViewAdapter;
import com.zxtd.information.manager.CacheManager;
import com.zxtd.information.manager.RegistManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ResponseParam;

public class RegistActivity extends BaseActivity implements OnClickListener{

	private AutoCompleteTextView edit_email;
	private EditText edit_nickName;
	private EditText edit_password;
	private EditText edit_rePassword;
	private ProgressDialog dialog;
	//private String[] stringArray = { "@163.com", "@126.com", "@qq.com", "@sina.com", "@taobao.com" };
	private AutoTextViewAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_regist);
		
		initView();
	}

	void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		
		TextView txtTitle=(TextView)findViewById(R.id.title);
		txtTitle.setText("注册");
		
		edit_email=(AutoCompleteTextView) findViewById(R.id.edit_regist_email);

		edit_email.setThreshold(1);
		adapter = new AutoTextViewAdapter(this);
		edit_email.setAdapter(adapter);
		
		edit_email.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				String input = s.toString();  
		        adapter.mList.clear();  
		        autoAddEmails(input);
		        adapter.notifyDataSetChanged();  
		        edit_email.showDropDown();  
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
		});
		
		edit_nickName=(EditText) findViewById(R.id.edit_regist_nickname);
		edit_password=(EditText) findViewById(R.id.edit_regist_password);
		edit_rePassword=(EditText) findViewById(R.id.edit_regist_repassword);
		Button btnRegist=(Button) findViewById(R.id.btn_regist);
		btnRegist.setOnClickListener(this);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.btn_regist:{
				doRegist();
			}break;
		}
	}
	
	/**
	 * 自动填充邮箱列表
	 * @param input
	 */
	private void autoAddEmails(String input) {
		String autoEmail = "";
        if (input.length() > 0) {  
            for (int i = 0; i < Constant.EMAIL_SUFFIX.length; ++i) {  
            	if(input.contains("@")) {//包含“@”则开始过滤
            		String filter = input.substring(input.indexOf("@") + 1 , input.length());//获取过滤器，即根据输入“@”之后的内容过滤出符合条件的邮箱
            		if(Constant.EMAIL_SUFFIX[i].contains(filter)) {//符合过滤条件
            			autoEmail = input.substring(0, input.indexOf("@")) + Constant.EMAIL_SUFFIX[i];//用户输入“@”之前的内容加上自动填充的内容即为最后的结果
            			adapter.mList.add(autoEmail);
            		}
            	}else {
            		autoEmail = input + Constant.EMAIL_SUFFIX[i];
            		adapter.mList.add(autoEmail);
            	}
            }  
        } 
	}
	
	private void doRegist(){
		final String email=edit_email.getText().toString().trim();
		final String nickName=edit_nickName.getText().toString().trim();
		final String password=edit_password.getText().toString().trim();
		final String rePassword=edit_rePassword.getText().toString().trim();
		if(TextUtils.isEmpty(email)){
			Toast("注册邮箱不能为空");
			return;
		}
		String regex="^[a-zA-Z0-9][\\w]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern pattern=Pattern.compile(regex);
		if(!pattern.matcher(email).matches()){
			Toast("输入的电子邮件地址不合法");
			return;
		}
		if(TextUtils.isEmpty(nickName)){
			Toast("注册昵称不能为空");
			return;
		}
		if(nickName.length()<1 || nickName.length()>24){
			Toast("注册昵称长度应小于24位");
			return;
		}
		if(TextUtils.isEmpty(password)){
			Toast("注册密码不能为空");
			return;
		}
		if(password.length()<6){
			Toast("注册密码至少为6位");
			return;
		}
		if(!password.equals(rePassword)){
			Toast("两次密码输入不一致,请重新填写");
			edit_password.setText("");
			edit_rePassword.setText("");
			return;
		}
		dialog=this.showProgress(-1, "", "正在注册...");
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				ResponseParam param=RegistManager.newInstance().doRegist(email, nickName, password,0,"","",2,"","","",getImsi());
				Message msg=handler.obtainMessage();
				if(null==param){
					msg.what=-1;
				}else{
					msg.what=1;
					msg.obj=param;
				}
				msg.sendToTarget();
			}
		}.start();
	}
	
	
	private Handler handler=new Handler(Looper.myLooper()){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					checkNerWork();
				}break;
				case 1:{
					ResponseParam param=(ResponseParam) msg.obj;
				    if(param.isSuccess()){
				    	
				    	SharedPreferences shareds=RegistActivity.this.getSharedPreferences(Constant.SHAREDPREFERENCES, Context.MODE_PRIVATE);
						Editor editor=shareds.edit();
						editor.putInt("userId", param.getReturnId());
						editor.commit();
						
						CacheManager.getInstance().initVersions();
						
						RegistActivity.this.finish();
						
				    	Intent intent=new Intent(RegistActivity.this,RegistDetailsActivity.class);
				    	intent.putExtra("userId", param.getReturnId());
				    	intent.putExtra("from", "regist");
				    	startActivity(intent);
				    }else{
				    	String reason="注册失败";
				    	if("0".equals(param.getFailureReason())){
				    		reason+=",邮箱已被注册.";
				    	}
				    	Toast(reason);
				    }
				}break;
			}
			dialog.dismiss();
		}
		
	};	

}
