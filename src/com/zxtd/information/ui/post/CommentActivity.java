package com.zxtd.information.ui.post;

import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.InvitationReportParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.me.LoginActivity;
import com.zxtd.information.util.Constant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CommentActivity extends BaseActivity implements OnClickListener {
	private EditText newPostEdit;
	private LinearLayout editHint;
	private Button btnBackPost;
	private Button btnPostNew;
	private String newId;
	private String flag;

	private long currentTime = 0;
	private int userId = -1;

	private ProgressDialog dialog;

	private static final int LOGIN_CODE = 110;

	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				editHint.setVisibility(View.GONE);
			} else {
				editHint.setVisibility(View.VISIBLE);
			}
		}
	};

	private RequestCallBack requestCallBack = new RequestCallBack() {

		public void requestSuccess(String requestCode, Result result) {
			dialog.dismiss();
			if (result == null) {
				return;
			}
			if (result.getBoolean(InvitationReportParseData.IS_SUCCESS)) {
				Toast.makeText(CommentActivity.this, "发布成功", Toast.LENGTH_SHORT)
						.show();
				jumpToPostList();
			} else {
				Toast.makeText(CommentActivity.this, "发布失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void requestError(String requestCode, int errorCode) {
			dialog.dismiss();
			Toast.makeText(CommentActivity.this, "网络异常！", Toast.LENGTH_SHORT)
					.show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.post_comment);

		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			newId = extras.getString(Constant.BundleKey.NEWID);
			flag = extras.getString(Constant.BundleKey.FLAG);
		}

		userId = getUserId();

		newPostEdit = (EditText) this.findViewById(R.id.new_post_edit);
		editHint = (LinearLayout) this.findViewById(R.id.edit_hint);
		btnBackPost = (Button) this.findViewById(R.id.btn_new_post_back);
		btnPostNew = (Button) this.findViewById(R.id.btn_new_post);

		dialog = new ProgressDialog(this);
		dialog.setTitle("正在发布····");
		dialog.setCancelable(false);

		btnBackPost.setOnClickListener(this);
		btnPostNew.setOnClickListener(this);
		newPostEdit.setOnFocusChangeListener(focusChangeListener);
	}

	public void onClick(View v) {
		if (v == btnBackPost) {
			this.finish();
		} else if (v == btnPostNew) {
			post();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_CODE) {
			if (resultCode == RESULT_OK) {
				userId = getUserId();
				Toast("登陆成功！");
			} else {
				Toast("登陆失败！");
			}
			return;
		}
		if (resultCode != RESULT_OK && data == null) {
			this.finish();
			return;
		}
		setResult(resultCode, data);
		this.finish();
	}

	private void post() {

		if (userId == -1) {
			onLogin();
		} else {
			String content = newPostEdit.getText().toString();
			if (content != null && !"".equals(content.trim())) {
				dialog.show();
				RequestManager.newInstance().invitationReportComm(newId,
						userId + "",
						content, flag, requestCallBack);
			} else {
				if (currentTime == 0) {
					currentTime = System.currentTimeMillis();
				} else {
					long deltaTime = System.currentTimeMillis() - currentTime;
					if (deltaTime < 3000) {
						return;
					}
				}
				Toast.makeText(this, "内容不能为空！", Toast.LENGTH_SHORT).show();
				currentTime = System.currentTimeMillis();
			}
		}

	}

	// 跳到登陆界面
	private void onLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("from", "other");
		this.startActivityForResult(intent, LOGIN_CODE);
	}

	private void jumpToPostList() {
		Intent intent = new Intent(this, PostListActivity.class);
		intent.putExtra(Constant.BundleKey.NEWID, newId);
		intent.putExtra(Constant.BundleKey.FLAG, flag);
		this.startActivityForResult(intent, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

}
