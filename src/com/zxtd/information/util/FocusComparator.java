package com.zxtd.information.util;

import java.util.Comparator;

import com.zxtd.information.bean.FansFocusBean;

public class FocusComparator implements Comparator<FansFocusBean> {

	@Override
	public int compare(FansFocusBean lhs, FansFocusBean rhs) {
		// TODO Auto-generated method stub
		return lhs.getSortKey().compareToIgnoreCase(rhs.getSortKey());
	}

}
