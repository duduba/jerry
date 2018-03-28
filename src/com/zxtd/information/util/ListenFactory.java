package com.zxtd.information.util;

import java.util.HashMap;

public class ListenFactory {
	private static ListenFactory listenFactory;
	private HashMap<String, OnGlobalListener> hashMap;
	private ListenFactory(){
		hashMap = new HashMap<String, ListenFactory.OnGlobalListener>();
	}
	public static ListenFactory newInstance(){
		if(listenFactory == null){
			listenFactory = new ListenFactory();
		}
		return listenFactory;
	}
	
	public void addListener(String key, OnGlobalListener globalListener){
		hashMap.put(key, globalListener);
	}
	
	public void removeListener(String key){
		hashMap.remove(key);
	}
	
	public void changeState(String key, Object...objects){
		OnGlobalListener globalListener = hashMap.get(key);
		if(globalListener != null){
			globalListener.onChangeState(objects);
		}
	}
	
	public interface OnGlobalListener{
		void onChangeState(Object... objects);
	}
}
