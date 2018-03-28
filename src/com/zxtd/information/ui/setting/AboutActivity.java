package com.zxtd.information.ui.setting;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {
	private TextView textView;
	private Button buttonBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		textView = (TextView) this.findViewById(R.id.about_version_id);
		buttonBack = (Button)findViewById(R.id.about_back);
		
		textView.setText("杂色  Android版" + ZXTD_Application.versionName);
		
		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});
	}
}
