package com.zxtd.information.parse;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public class RegisterParseData extends ParseData {

	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		String uuid = (String)params.get("ZXTD.DATA.uuid");
		String registerdate = (String)params.get("ZXTD.DATA.registerdate");
		Log.i(this.getClass().getName(), "uuid：" + uuid + ";registerdate：" + registerdate);
		Map<String, String> mapSave = new HashMap<String, String>();
		mapSave.put(Constant.DataKey.UUID, uuid);
		Utils.UUID = uuid;
		Utils.save(ZXTD_Application.getMyContext(), mapSave);
		return result;
	}

}
