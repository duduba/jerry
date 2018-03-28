package com.zxtd.information.parse;

import java.util.HashMap;
import java.util.List;
import com.zxtd.information.bean.Bean;


public class Result {
	private HashMap<String, Object> mapParams;
	
	public Result(){
		mapParams = new HashMap<String, Object>();
	}
	
	public void put(String key, Object value){
		mapParams.put(key, value);
	}
	
	public Bean getBean(String key){
		Object object = mapParams.get(key);
		if(object == null){
			return null;
		}
		if(object instanceof Bean){
			return (Bean)object;
		}
		return null;
	}
	
	public List<Bean> getListBean(String key){
		Object object = mapParams.get(key);
		if(object == null){
			return null;
		}
		if(object instanceof List){
			return (List<Bean>)object;
		}
		return null;
	}
	
	public String getString(String key){
		Object object = mapParams.get(key);
		if(object == null){
			return null;
		}
		if(object instanceof String){
			return (String)object;
		}
		return null;
	}
	
	public int getInteger(String key){
		Object object = mapParams.get(key);
		if(object == null){
			return 0;
		}
		if(object instanceof Integer){
			return (Integer)object;
		}
		return 0;
	}
	
	public Boolean getBoolean(String key){
		Object object = mapParams.get(key);
		if(object == null){
			return false;
		}
		if(object instanceof Boolean){
			return (Boolean)object;
		}
		return false;
	}

	/*
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Iterator<Entry<String, Object>> it=mapParams.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> entry=it.next();
			Log.e(Constant.TAG, entry.getKey()+"               "+entry.getValue());
		}
		return super.toString();
	}*/
	
	
}
