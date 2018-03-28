package com.zxtd.information.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;
import com.zxtd.information.util.Utils;

public class NetFriendNewsTypeParseData extends ParseData {
	/**
	 * 获取网友发布类型
	 * */
	public final static String NET_FRINED_TYPE_LIST_KEY = "netFriendTypeBeans";
	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		List<Bean> netFriendTypeBeans = new ArrayList<Bean>();
		result.put(NET_FRINED_TYPE_LIST_KEY, netFriendTypeBeans);
		
		List<Map<String, Object>> data = (List<Map<String, Object>>)params.get("ZXTD.DATA.item");
		if(data != null){
			for(int i = 0; i < data.size(); i++) {
				Map<String, Object> item = data.get(i);
				netFriendTypeBeans.add(getNetFriendNewsTypeBean(item));
			}
		} else {
			String id = (String)params.get("ZXTD.DATA.item.id");
			if(Utils.isEmpty(id)){
				return result;
			}
			netFriendTypeBeans.add(getNetFriendNewsTypeBean(params));
		}
		return result;
	}
	
	private NewTypeBean getNetFriendNewsTypeBean(Map<String, Object> data){
		NewTypeBean newTypeBean = new NewTypeBean();
		newTypeBean.id = (String)data.get("ZXTD.DATA.item.id");
		newTypeBean.name = (String)data.get("ZXTD.DATA.item.name");
		newTypeBean.flag = (String)data.get("ZXTD.DATA.item.flag");
		return newTypeBean;
	}

}
