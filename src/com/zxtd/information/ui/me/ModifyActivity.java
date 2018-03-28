package com.zxtd.information.ui.me;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

public class ModifyActivity extends BaseActivity implements OnClickListener{

	private EditText txtNickName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_main_modify);
		initView();
	}

	private void initView(){
		findViewById(R.id.back).setOnClickListener(this);
		TextView txtTitle=(TextView) findViewById(R.id.title);
		txtTitle.setText("修改个人资料");
		
		
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
		}
	}
	
	
}
