package com.zxtd.information.parse;

import java.util.Map;

public class ModifyPubParseData extends ParseData {

	@Override
	public Result parseMap(Map<String, Object> params) {
		Result result = new Result();
		if(params.containsKey("ZXTD.result-code")){
			int rep = Integer.parseInt((String)params.get("ZXTD.result-code"));
			result.put(PublishNewsParseData.IS_SUCCESS, rep == 1);
		}
		return result;
	}

}
