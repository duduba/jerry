package com.zxtd.information.ui.setting;

import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DisclaimerActivity extends BaseActivity {
	private Button back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.disclaimer);
		back = (Button)findViewById(R.id.disclaimer_back);
		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				DisclaimerActivity.this.finish();
			}
		});
	}
	
}
