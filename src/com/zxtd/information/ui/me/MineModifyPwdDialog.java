package com.zxtd.information.ui.me;

import java.util.regex.Pattern;

import com.zxtd.information.adapter.AutoTextViewAdapter;
import com.zxtd.information.manager.LoginManager;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class MineModifyPwdDialog extends Dialog {

	//private Button.OnClickListener listener;
	private Context context;
	private AutoTextViewAdapter adapter;
	private AutoCompleteTextView editMail;
	
	/*
	public void setListener(Button.OnClickListener listener) {
		this.listener = listener;
	}*/

	public MineModifyPwdDialog(Context context) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.mine_modify_pwd_dialog);
		setDialogStyle();
			
		editMail=(AutoCompleteTextView) findViewById(R.id.edit_email);
		editMail.setThreshold(1);
		adapter = new AutoTextViewAdapter(context);
		editMail.setAdapter(adapter);
		editMail.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				String input = s.toString();  
		        adapter.mList.clear();  
		        autoAddEmails(input);
		        adapter.notifyDataSetChanged();  
		        editMail.showDropDown();  
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
		
		findViewById(R.id.btn_cancel).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		findViewById(R.id.btn_sure).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final String email=editMail.getText().toString().trim();
				if(TextUtils.isEmpty(email)){
					Toast.makeText(context, "请输入你的邮箱", Toast.LENGTH_LONG).show();
					return;
				}
				String regex="^[a-zA-Z0-9][\\w]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
				Pattern pattern=Pattern.compile(regex);
				if(!pattern.matcher(email).matches()){
					Toast.makeText(context, "输入的电子邮件地址不合法", Toast.LENGTH_LONG).show();
					return;
				}
				dismiss();
				new Thread(){
					@Override
					public void run() {
						super.run();
						int resultCode=LoginManager.getInstance().findPassword(email);
						handler.sendEmptyMessage(resultCode);
					}
				}.start();
			}
		});
	}
	
	private void setDialogStyle() {
		
		this.setCanceledOnTouchOutside(true);
		
		LayoutParams params = this.getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT; // 设置对话框宽度
		params.height = LayoutParams.WRAP_CONTENT; // 设置对话框高度
		//params.dimAmount = 0.0f; // 去掉半透明黑色背景遮盖
		//params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;  //去掉窗体焦点
		//params.gravity = Gravity.BOTTOM;
		//params.softInputMode = LayoutParams.SOFT_INPUT_STATE_UNCHANGED | LayoutParams.SOFT_INPUT_ADJUST_PAN;

		this.getWindow().setAttributes(params);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
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

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		editMail.setText("");
	}
	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					if(!Utils.isNetworkConn()){
						Toast.makeText(context, "网络不给力", Toast.LENGTH_LONG).show();
					}
				}break;
				case 1:{
					Toast.makeText(context, "邮件已发到您的邮箱", Toast.LENGTH_LONG).show();
					/*
					String url=getUrl(editMail.getText().toString().trim());
					Uri uri = Uri.parse(url);  
					Intent intent=new Intent(Intent.ACTION_VIEW,uri);
					context.startActivity(intent);
					*/
				}break;
				default:{
					Toast.makeText(context, "邮箱不存在,请确认..", Toast.LENGTH_LONG).show();
				}break;
			}
		}
	};
	
	
	private String getUrl(String email){
		String url="";
		String suffix=email.split("@")[1];
		if("163.com".equals(suffix)){
			url="http://mail.163.com/";
		}else if("126.com".equals(suffix)){
			url="http://mail.126.com/";
		}else if("yeah.net".equals(suffix)){
			url="http://www.yeah.net/";
		}else if("qq.com".equals(suffix)){
			url="http://m.mail.qq.com/";
		}else if("sina.com".equals(suffix)){
			url="http://mail.sina.com.cn/";
		}else if("vip.sina.com".equals(suffix)){
			url="http://vip.sina.com.cn/";
		}else if("gmail.com".equals(suffix)){
			url="http://gmail.google.com/";
		}else if("hotmail.com".equals(suffix)){
			url="https://login.live.com/";
		}else if("sohu.com".equals(suffix)){
			url="http://mail.sohu.com/";
		}else if("139.com".equals(suffix)){
			url="http://mail.10086.cn/";
		}else if("189.cn".equals(suffix)){
			url="http://webmail29.189.cn/webmail/";
		}else if("21.cn".equals(suffix)){
			url="http://mail.21cn.com/";
		}
		return url;
	}
	
	
}
