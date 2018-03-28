package com.zxtd.information.ui.netfriend;

import java.util.HashMap;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.zxtd.information.adapter.NeFriListViewPagerAdapter;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.manager.NewTypeBeansManager;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.NetFriendNewsTypeParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.TabBaseActivity;
import com.zxtd.information.util.ListenFactory;
import com.zxtd.information.util.ListenFactory.OnGlobalListener;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 网友
 * */
public class NetFriendActivity extends TabBaseActivity implements OnClickListener{
	private TabPageIndicator typeList;
	
	private List<Bean> newTypeBeans;
	private HashMap<String, List<Bean>> newBeanMap;

	private ViewPager newListViewPager;
	private LinearLayout zxtdLoadingView;
	private RelativeLayout netErrorView;
	
	private NeFriListViewPagerAdapter pagerAdapter;
	
	private int currentType = -1;
	
	private RequestCallBack mCallBack = new RequestCallBack() {
		
		public void requestSuccess(String requestCode, Result result) {
			getNewTypes(result);
			zxtdLoadingView.setVisibility(View.GONE);
		}
		
		public void requestError(String requestCode, int errorCode) {
			netErrorView.setVisibility(View.VISIBLE);
			zxtdLoadingView.setVisibility(View.GONE);
		}
	};
	
	private OnClickListener netConnectClick = new OnClickListener() {

		public void onClick(View v) {
			zxtdLoadingView.setVisibility(View.VISIBLE);
			requestNewType();
		}
	};

	private OnClickListener wlanSettingClick = new OnClickListener() {

		public void onClick(View v) {
			Utils.jumpToWlanSetting(NetFriendActivity.this);
		}
	};

	private OnPageChangeListener changeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			currentType = arg0;
			addClickCounts(currentType);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.net_friend_main);
		initData();
		initView();
	}
	
	private void initView(){
		TextView titleText = (TextView)this.findViewById(R.id.title);
		titleText.setText(R.string.new_title);
		typeList = (TabPageIndicator) this.findViewById(R.id.new_type_list);
		zxtdLoadingView = (LinearLayout) this.findViewById(R.id.loading_view);
		netErrorView = (RelativeLayout) this.findViewById(R.id.net_error);
		TextView btnWlanSetting = (TextView) this
				.findViewById(R.id.btn_wlan_setting);
		newListViewPager = (ViewPager) this.findViewById(R.id.new_view_pager);
		
		pagerAdapter = new NeFriListViewPagerAdapter(this, newTypeBeans,
				newBeanMap);
		newListViewPager.setAdapter(pagerAdapter);

		typeList.setViewPager(newListViewPager);

		netErrorView.setOnClickListener(netConnectClick);
		btnWlanSetting.setOnClickListener(wlanSettingClick);
		typeList.setOnPageChangeListener(changeListener);
		Button btnCompose=(Button) findViewById(R.id.btn_compose);
		btnCompose.setOnClickListener(this);
		//requestNewType();
		zxtdLoadingView.setVisibility(View.GONE);
	}
	
	private void initData(){
		newTypeBeans = NewTypeBeansManager.getNewInstance().getNewTypeBeans();
		newBeanMap = new HashMap<String, List<Bean>>();
		ListenFactory.newInstance().addListener("changeNetFriendType", globalListener);
		ListenFactory.newInstance().addListener("updateNewType", updateNewType);
	}
	
	private void requestNewType(){
		netErrorView.setVisibility(View.GONE);
		RequestManager.newInstance().netFriendNewsTypeComm(mCallBack);
	}

	private void getNewTypes(Result result){
		List<Bean> beans = result.getListBean(NetFriendNewsTypeParseData.NET_FRINED_TYPE_LIST_KEY);
		newTypeBeans.clear();
		newTypeBeans.addAll(beans);
		pagerAdapter = new NeFriListViewPagerAdapter(this, newTypeBeans,
				newBeanMap);
		newListViewPager.setAdapter(pagerAdapter);
		typeList.notifyDataSetChanged();
	}
	
	public void onClick(View v) {
		if(v.getId() == R.id.btn_compose){
			if(getUserId() != -1){
				Utils.jumpPubNews(this);
			}else{
				ListenFactory.newInstance().changeState("tabChange", 2);
			}
		}
	}
	
	private OnGlobalListener globalListener = new OnGlobalListener() {
		
		@Override
		public void onChangeState(Object... objects) {
			int type = (Integer)objects[0];
			if(type != currentType){
				newListViewPager.setCurrentItem(type);
			}
			pagerAdapter.refreshData(type);
		}
	};
	
	private OnGlobalListener updateNewType = new OnGlobalListener() {
		
		@Override
		public void onChangeState(Object... objects) {
			typeList.notifyDataSetChanged();
		}
	};
	
	
		// 统计某新闻类型点击次数
		private void addClickCounts(int index) {
			switch (index) {
			case 0:
				MobclickAgent.onEvent(this, "netfriend_list_item1");
				break;
			case 1:
				MobclickAgent.onEvent(this, "netfriend_list_item2");
				break;
			case 2:
				MobclickAgent.onEvent(this, "netfriend_list_item3");
				break;
			case 3:
				MobclickAgent.onEvent(this, "netfriend_list_item4");
				break;
			case 4:
				MobclickAgent.onEvent(this, "netfriend_list_item5");
				break;
			case 5:
				MobclickAgent.onEvent(this, "netfriend_list_item6");
				break;
			case 6:
				MobclickAgent.onEvent(this, "netfriend_list_item7");
				break;
			default:
				break;
			}
		}
}
