package com.zxtd.information.ui;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;

public class TabBaseActivity extends BaseActivity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		this.sendBroadcast(new Intent(MainActivity.MENU_CLICK_ACTION));
		return false;
	}
}
