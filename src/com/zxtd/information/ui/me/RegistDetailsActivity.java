package com.zxtd.information.ui.me;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zxtd.information.db.DBHelper;
import com.zxtd.information.manager.RegistManager;
import com.zxtd.information.ui.BaseActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.custview.EggCalendar;
import com.zxtd.information.util.Constant;

public class RegistDetailsActivity extends BaseActivity implements 
	OnClickListener,OnTouchListener{

	private EditText edit_nickName;
	private EditText edit_sign;
	private EditText edit_area;
	private EditText edit_work;
	private RadioGroup sexGroup;
	private EditText edit_birth;
	private EggCalendar calendar;
	private PopupWindow popWin;
	private ProgressDialog dialog;
	private int userId;
	private TextView txtNickNameTitle;
	
	private static final int requestCode=1;
	private String from="regist";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.hiddleTitleBar();
		setContentView(R.layout.mine_regist_details);
		userId=getIntent().getIntExtra("userId", -1);
		if(userId==-1){
			userId=getUserId();
		}
		from=getIntent().getStringExtra("from");
		
		initView();
	}

	
	private void initView(){
		Button btnBack=(Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		TextView title=(TextView) findViewById(R.id.title);
		
		sexGroup=(RadioGroup) findViewById(R.id.radioGroup);
		
		edit_nickName=(EditText) findViewById(R.id.mine_regist_nickname);
		edit_sign=(EditText) findViewById(R.id.mine_regist_sign);
		edit_area=(EditText) findViewById(R.id.mine_regist_eare);
		edit_area.setOnTouchListener(this);
		edit_work=(EditText) findViewById(R.id.mine_regist_work);
		edit_birth=(EditText) findViewById(R.id.mine_regist_birth);
		edit_birth.setOnTouchListener(this);
		txtNickNameTitle=(TextView) findViewById(R.id.mine_regdet_nicktitle);
		
		Button btnRegist=(Button) findViewById(R.id.btn_regist);
		btnRegist.setOnClickListener(this);
		
		LinearLayout topLayout=(LinearLayout) findViewById(R.id.top_layout);
		if("mine_main".equals(from)){
			topLayout.setVisibility(View.GONE);
			title.setText("编辑个人资料");
		}else{
			edit_nickName.setVisibility(View.GONE);
			txtNickNameTitle.setVisibility(View.GONE);
			title.setText("完善个人资料");
		}
	}


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.back:{
				finish();
			}break;
			case R.id.btn_regist:{
				finishUserInfo();
			}break;
		}
	}
	
	
	void showCalendar(){
		if(null==popWin){
			View view=LayoutInflater.from(this).inflate(R.layout.mine_regist_calendar_popup, null);
			popWin=new PopupWindow(view,WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT,true);
			calendar=(EggCalendar) view.findViewById(R.id.mycalendar);
			float density = getResources().getDisplayMetrics().density;
			calendar.setLayoutParams(new LinearLayout.LayoutParams((int)(280*density), (int)(260*density)));
		
			calendar.setItemClick(new EggCalendar.OnItemClick() {	
				public void OnClick(EggCalendar item) {
					// TODO Auto-generated method stub
					//Toast();
					edit_birth.setText(new SimpleDateFormat("yyyy-MM-dd").format(item.getSelectedStartDate()));
					popWin.dismiss();
				}
			});
			
			popWin.setBackgroundDrawable(new BitmapDrawable());
			popWin.setFocusable(true); //设置PopupWindow可获得焦点
			popWin.setTouchable(true); //设置PopupWindow可触摸
			popWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}
		popWin.showAtLocation(findViewById(R.id.mine_regist_info_main), Gravity.CENTER, 0, 0);
	}


	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==MotionEvent.ACTION_UP){
			switch(v.getId()){
				case R.id.mine_regist_birth:{
					Calendar cal=Calendar.getInstance();
					cal.set(1989,0, 1);
					new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener(){
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
							// TODO Auto-generated method stub
							monthOfYear++;
							String tempMonth=monthOfYear<10 ? "0"+monthOfYear : String.valueOf(monthOfYear);
							String tempDay=dayOfMonth<10 ? "0"+dayOfMonth : String.valueOf(dayOfMonth);
							String date=year+"-"+tempMonth+"-"+tempDay;
							edit_birth.setText(date);
							edit_birth.setSelection(date.length());
						}
					}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
				}break;
				case R.id.mine_regist_eare:{
					String dbFile = Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/citys.db";
			        File filePath = new File(dbFile);
			        if (!filePath.exists()|| filePath.length()<1024*1024){
			        	DBHelper.copyDataBase();
			        }
					Intent intent=new Intent(RegistDetailsActivity.this,CityActivity.class);
					startActivityForResult(intent, requestCode);
				}break;
			}
		}
		return false;
	}
	
	
	private void finishUserInfo(){
		final String sign=edit_sign.getText().toString().trim();
		final String area=edit_area.getText().toString().trim();
		final String work=edit_work.getText().toString().trim();
		String sex="0";
		switch(sexGroup.getCheckedRadioButtonId()){
			case R.id.radioMale:{
				sex="0";
			}break;
			case R.id.radioFemale:{
				sex="1";
			}break;
			case R.id.radioSecret:{
				sex="-1";
			}break;
		}
		final String sex1=sex;
		final String birth=edit_birth.getText().toString().trim();
		dialog=showProgress(-1, "", "正在保存..");
		dialog.show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				int resultCode=RegistManager.newInstance().completeUserInformation(userId, sign, area, work, sex1, birth);
				Message msg=handler.obtainMessage();
				msg.what=resultCode;
				msg.sendToTarget();
			}
		}.start();
	}


	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK){
			if(reqCode==requestCode){
				String cityName=data.getStringExtra("name");
				edit_area.setText(cityName);
				edit_area.setSelection(cityName.length());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
				case -1:{
					RegistDetailsActivity.this.checkNerWork();
				}break;
				case 2:
				case 0:{
					Toast("完善个人资料出错");
				}break;
				case 1:{
					Toast("保存成功");
					
					RegistDetailsActivity.this.finish();
					Intent intent=new Intent(Constant.UPDATE_MINE_TABHOST_ITEM);
					intent.putExtra("action", "login");
					sendBroadcast(intent);
				}break;
			}
			if(null!=dialog){
				dialog.dismiss();
			}
		}
		
	};
	
	
}
