package com.zxtd.information.ui;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.adapter.NickNameListAdapter;
import com.zxtd.information.manager.UserNickManager;
import com.zxtd.information.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 设置昵称
 * */
public class NickNameActivity extends Activity implements OnClickListener,OnItemClickListener{
	private Button btnNickNameBack;
	private Button btnUseNickName;
	private TextView currentNickNameText;
	private EditText currentNickNameEdit;
	private ListView nickNameList;
	private Button btnClearAllNickName;
	//昵称管理
	private UserNickManager manager;
	
	private List<String> mNickNames;
	private NickNameListAdapter nameListAdapter;
	
	private static final int SELECT_NICK_NAMES = 0;
	private static final int DELETE_NICK_NAMES = 1;
	
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.nick_name_set);
		
		
		btnNickNameBack = (Button) this.findViewById(R.id.btn_nick_name_back);
		btnUseNickName = (Button) this.findViewById(R.id.btn_use_nick_name);
		currentNickNameText = (TextView) this.findViewById(R.id.current_nick_name_text);
		currentNickNameEdit = (EditText) this.findViewById(R.id.current_nick_name_edit);
		nickNameList = (ListView) this.findViewById(R.id.nick_name_list);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View footView = inflater.inflate(R.layout.nick_name_list_footer, null);
		btnClearAllNickName = (Button) footView.findViewById(R.id.btn_clear_all_nick_name);
		
		manager = UserNickManager.getNewInstance();
		
		mNickNames = new ArrayList<String>();
		nameListAdapter = new NickNameListAdapter(this, mNickNames);
		
		nickNameList.addFooterView(footView);
		nickNameList.setAdapter(nameListAdapter);
		
		
		btnNickNameBack.setOnClickListener(this);
		btnUseNickName.setOnClickListener(this);
		btnClearAllNickName.setOnClickListener(this);
		nickNameList.setOnItemClickListener(this);
		
		new Thread(new DoBehind(SELECT_NICK_NAMES)).start();
		
		setCurrentNickName();
		
	}
	
	public void setCurrentNickName(){
		
		currentNickNameText.setText("当前昵称：" + manager.getNickName());
	}
	
	public void onClick(View v) {
		if(v == btnNickNameBack){
			this.finish();
		}else if(v == btnUseNickName){
			String nickName = currentNickNameEdit.getText().toString();
			if(!Utils.isEmpty(nickName) && !"".equals(nickName.trim())){
				manager.setNickName(nickName);
				this.finish();
			}else{
				Toast.makeText(this, "内容不能为空！", Toast.LENGTH_SHORT).show();
			}
		}else if(v == btnClearAllNickName){
			new Thread(new DoBehind(DELETE_NICK_NAMES)).start();
		}
	}
	
	private class DoBehind implements Runnable{
		private int type = -1;
		public DoBehind(int type){
			this.type = type;
		}
		public void run() {
			switch (type) {
			case SELECT_NICK_NAMES:
				addAllNickNames();
				break;
			case DELETE_NICK_NAMES:
				deleteAllNickNames();
				break;
			}
		}
	}
	
	private void addAllNickNames(){
		final List<String> nickNames = manager.getNickNameList();
		handler.post(new Runnable() {
			public void run() {
				if(nickNames.size() != 0){
					btnClearAllNickName.setVisibility(View.VISIBLE);
					mNickNames.addAll(nickNames);
					nameListAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	public void HideListFooter(){
		btnClearAllNickName.setVisibility(View.GONE);
	}
	
	private void deleteAllNickNames(){
		manager.deleteAllNickName();
		handler.post(new Runnable() {
			public void run() {
				mNickNames.clear();
				nameListAdapter.notifyDataSetChanged();
				setCurrentNickName();
				HideListFooter();
			}
		});
	}
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String nickName = mNickNames.get(arg2);
		currentNickNameEdit.setText(nickName);
		currentNickNameEdit.setSelection(nickName.length());
	}
}
