package com.zxtd.information.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.Result;
import com.zxtd.information.parse.doc.ModifUserInfoParseData;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public class ModifPwdActivity extends BaseActivity implements OnClickListener{
	private EditText oldPwdEdit;
	private EditText newPwdEdit;
	private EditText doublePwdEdit;
	private String reslutMsg[];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modif_password);
		initData();
		initView();
	}
	
	private void initData(){
		reslutMsg = new String[]{"密码修改成功！","密码未填写","确认密码未填写","密码不少于6位","两次密码不一致"};
	}
	
	private void initView(){
		Button back = (Button) this.findViewById(R.id.back);
		TextView title = (TextView) this.findViewById(R.id.title);
		Button btnSave = (Button) this.findViewById(R.id.btn_save_modif);
		oldPwdEdit = (EditText) this.findViewById(R.id.old_pwd_edit);
		newPwdEdit = (EditText) this.findViewById(R.id.new_pwd_edit);
		doublePwdEdit = (EditText) this.findViewById(R.id.double_new_pwd_edit);
		
		back.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		title.setText(R.string.modif_password);
 	}

	
	private int checkPwd(String first, String second){
		if(Utils.isEmpty(first)){
			return 1;
		}
		if(Utils.isEmpty(second)){
			return 2;
		}
		
		if(first.length() < 6){
			return 3;
		}
		
		if(!first.equals(second)){
			return 4;
		}
		return 0;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back){
			this.finish();
		}else if(id == R.id.btn_save_modif){
			//保存密码
			String newPwd = newPwdEdit.getText().toString();
			String doubleNewPwd = doublePwdEdit.getText().toString();
			
			int code = checkPwd(newPwd, doubleNewPwd);
			if(code == 0){
				String oldPwd = oldPwdEdit.getText().toString();
				RequestManager.newInstance().modifPwdComm(Constant.loginUser.getUserId() + "", oldPwd, newPwd, mCallBack);
			}else{
				Toast(reslutMsg[code]);
			}
			
		}
	}
	
	private RequestCallBack mCallBack = new RequestCallBack() {
		
		@Override
		public void requestSuccess(String requestCode, Result result) {
			String resultCode = result.getString(ModifUserInfoParseData.RESULT_CODE);
			if("1".equals(resultCode)){
				Toast("修改成功！");
				ModifPwdActivity.this.finish();
			}else{
				Toast("修改失败！");
			}
		}
		
		@Override
		public void requestError(String requestCode, int errorCode) {
			Toast("网络不给力！");
		}
	};
}
