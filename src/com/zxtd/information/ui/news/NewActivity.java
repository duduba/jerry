package com.zxtd.information.ui.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.zxtd.information.adapter.NewListViewPagerAdapter;
import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.manager.NewTypeBeansManager;
import com.zxtd.information.manager.RequestManager;
import com.zxtd.information.net.RequestCallBack;
import com.zxtd.information.parse.HotNewsListParseData;
import com.zxtd.information.parse.NetFriendNewsTypeParseData;
import com.zxtd.information.parse.NewsHeadlineParseData;
import com.zxtd.information.parse.NewsTypeParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.ui.MainActivity;
import com.zxtd.information.ui.R;
import com.zxtd.information.ui.TabBaseActivity;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.ListenFactory;
import com.zxtd.information.util.Utils;
import com.zxtd.information.view.NewListViewCreater;
import com.zxtd.information.view.TabPageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 新闻
 * */
public class NewActivity extends TabBaseActivity implements OnClickListener {
	private TabPageIndicator typeList;

	private ViewPager newListViewPager;

	private LinearLayout zxtdLoadingView;
	private RelativeLayout netErrorView;
	private List<Bean> newBeans;
	private List<Bean> headNewBeans;
	private List<Bean> newTypeBeans;
	private HashMap<String, List<Bean>> newBeanMap;

	private NewListViewPagerAdapter pagerAdapter;

	private int currentType = -1;

	private static final int INIT = 0;
	private static final int NONE = 3;

	private RequestCallBack callBack = new RequestCallBack() {

		public void requestSuccess(String requestCode, Result result) {
			int currentState = result.getInteger("state");
			switch (currentState) {
			case INIT:
				if (Constant.RequestCode.REGISTER.equals(requestCode)) {
					return;
				} else if (Constant.RequestCode.NEWS_FIRST.equals(requestCode)) {
					initNewTypesData(result);
					return;
				} else if (Constant.RequestCode.NEWS_LIST.equals(requestCode)) {
					List<Bean> temHeadBeans = result
							.getListBean(NewsHeadlineParseData.NEW_HEAD_LIST_KEY);
					headNewBeans.addAll(temHeadBeans);
					return;
				} else if (Constant.RequestCode.NEW_INFO.equals(requestCode)) {
					initNewData(result);
				} else if (Constant.RequestCode.ZXTD_PUBLISH_NEWS_TYPE.equals(requestCode)){
					List<Bean> netNewTypeBeans = NewTypeBeansManager.getNewInstance().getNewTypeBeans();
					List<Bean> beans = result.getListBean(NetFriendNewsTypeParseData.NET_FRINED_TYPE_LIST_KEY);
					netNewTypeBeans.clear();
					netNewTypeBeans.addAll(beans);
					ListenFactory.newInstance().changeState("updateNewType");
					return;
				}
				break;
			case NONE:
				break;
			}
			zxtdLoadingView.setVisibility(View.GONE);
			currentState = NONE;
		}

		public void requestError(String requestCode, int errorCode) {
			netErrorView.setVisibility(View.VISIBLE);
			zxtdLoadingView.setVisibility(View.GONE);
		}
	};

	private OnClickListener netConnectClick = new OnClickListener() {

		public void onClick(View v) {
			zxtdLoadingView.setVisibility(View.VISIBLE);
			doInit();
		}
	};

	private OnClickListener wlanSettingClick = new OnClickListener() {

		public void onClick(View v) {
			Utils.jumpToWlanSetting(NewActivity.this);
		}
	};

	private OnPageChangeListener changeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			currentType = arg0;
			clickCounts(currentType);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_main);
		initData();
		initView();
	}

	private void initData() {
		
		String[] values = Utils.load(this, new String[] {
				Constant.DataKey.UUID, Constant.DataKey.REGISTERDATE });

		if (!values[0].equals("")) {
			Utils.UUID = values[0];
		}
		
		// 初始化各list对象
		newBeans = new ArrayList<Bean>();
		newTypeBeans = new ArrayList<Bean>();
		headNewBeans = new ArrayList<Bean>();
		newBeanMap = new HashMap<String, List<Bean>>();

		newBeanMap.put("newHotList", headNewBeans);

		pagerAdapter = new NewListViewPagerAdapter(this, newTypeBeans,
				newBeanMap);
	}

	private void initView() {
		TextView titleText = (TextView) this.findViewById(R.id.title);
		titleText.setText(R.string.new_title);
		Button btnCompose = (Button) findViewById(R.id.btn_compose);
		typeList = (TabPageIndicator) this.findViewById(R.id.new_type_list);
		zxtdLoadingView = (LinearLayout) this.findViewById(R.id.loading_view);
		netErrorView = (RelativeLayout) this.findViewById(R.id.net_error);
		TextView btnWlanSetting = (TextView) this
				.findViewById(R.id.btn_wlan_setting);
		newListViewPager = (ViewPager) this.findViewById(R.id.new_view_pager);
		newListViewPager.setAdapter(pagerAdapter);

		typeList.setViewPager(newListViewPager);

		netErrorView.setOnClickListener(netConnectClick);
		btnWlanSetting.setOnClickListener(wlanSettingClick);
		typeList.setOnPageChangeListener(changeListener);
		btnCompose.setOnClickListener(this);
		doInit();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_compose) {
			if(getUserId() != -1){
				Utils.jumpPubNews(this);
			}else{
				ListenFactory.newInstance().changeState("tabChange", 2);
			}
		}
	}

	// 初始化新闻类型，头条，热门
	private void doInit() {
		netErrorView.setVisibility(View.GONE);
		RequestManager.newInstance().firstComm(Utils.getRegisterData(this),
				callBack, NewListViewCreater.getNoImage());
	}

	//统计某新闻类型点击次数
		private void clickCounts(int index) {
			switch (index) {
			case 0:
				MobclickAgent.onEvent(this, "tab1_id");
				break;
			case 1:
				MobclickAgent.onEvent(this, "tab2_id");
				break;
			case 2:
				MobclickAgent.onEvent(this, "tab3_id");
				break;
			case 3:
				MobclickAgent.onEvent(this, "tab4_id");
				break;
			case 4:
				MobclickAgent.onEvent(this, "tab5_id");
				break;
			case 5:
				MobclickAgent.onEvent(this, "tab6_id");
				break;
			case 6:
				MobclickAgent.onEvent(this, "tab7_id");
				break;
			case 7:
				MobclickAgent.onEvent(this, "tab8_id");
				break;
			default:
				break;
			}
		}

	// 设置新闻类型数据
	private void initNewTypesData(Result result) {
		List<Bean> beans = result
				.getListBean(NewsTypeParseData.NEW_TYPE_LIST_KEY);
		newTypeBeans.addAll(beans);
		currentType = Utils.getHotNewTypeIndex(newTypeBeans);

	}

	// 设置新闻热门数据
	private void initNewData(Result result) {
		List<Bean> beans = result
				.getListBean(HotNewsListParseData.HOT_NEWS_LIST_KEY);
		newBeans.addAll(beans);
		if (currentType != -1) {
			NewTypeBean newTypeBean = (NewTypeBean) newTypeBeans
					.get(currentType);
			newBeanMap.put(newTypeBean.id, beans);
		}
		pagerAdapter = new NewListViewPagerAdapter(this, newTypeBeans,
				newBeanMap);
		newListViewPager.setAdapter(pagerAdapter);
		typeList.notifyDataSetChanged();
	}

	// 获取当前类型的标记
	public String getCurrentFlag() {
		if (currentType != -1) {
			return ((NewTypeBean) newTypeBeans.get(currentType)).flag;
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void updateAdapter() {
		if (Utils.isDataChange) {
			if (newTypeBeans != null && newTypeBeans.size() > 0) {
				newBeanMap.put(((NewTypeBean) newTypeBeans.get(0)).id, null);
			}
			newListViewPager.setAdapter(new NewListViewPagerAdapter(this,
					new ArrayList<Bean>(), new HashMap<String, List<Bean>>()));
			newListViewPager.setAdapter(new NewListViewPagerAdapter(this,
					newTypeBeans, newBeanMap));

			typeList.notifyDataSetChanged();
			Utils.isDataChange = false;
		}
	}

	public void onLockStatus(boolean isLock) {
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		this.sendBroadcast(new Intent(MainActivity.MENU_CLICK_ACTION));
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Utils.isGoToNetFriend) {
			for (int i = 0; i < newTypeBeans.size(); i++) {
				NewTypeBean newTypeBean = (NewTypeBean) newTypeBeans.get(i);
				if (Constant.Flag.NET_FRIEND_TYPE.equals(newTypeBean.flag)) {
					currentType = i;
					break;
				}

			}
			newListViewPager.setCurrentItem(currentType);
			Utils.isGoToNetFriend = false;
		}
		updateAdapter();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
