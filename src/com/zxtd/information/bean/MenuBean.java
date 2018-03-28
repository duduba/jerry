package com.zxtd.information.bean;

public class MenuBean {
	public String title = null;
	public int icon = -1;
	
	public MenuBean(){
		
	}
	public MenuBean(String title, int resId){
		this.title = title;
		this.icon = resId;
	}
}
