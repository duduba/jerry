package com.zxtd.information.bean;

import java.util.HashMap;
import java.util.Map;

public class RegisterData extends RequestBean{
	public String si = "";
	public String di = "";
	public String ct = "";
	public String imsi = "";
	public String number = "";
	public String mnc = "";
	public String sc = "";
	public String version = "";
	public String pt = "";
	public String phonetype = "";
	public Map<String, String> getMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("si", si);
		map.put("di", di);
		map.put("ct", ct);
		map.put("imsi", imsi);
		map.put("number", number);
		map.put("mnc", mnc);
		map.put("sc", sc);
		map.put("version", version);
		map.put("pt", pt);
		map.put("phonetype", phonetype);
		return map;
	}
}
