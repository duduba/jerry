package com.zxtd.information.net;

import com.zxtd.information.parse.Result;

public interface RequestCallBack {
	void requestError(String requestCode, int errorCode);
	void requestSuccess(String requestCode, Result result);
}
