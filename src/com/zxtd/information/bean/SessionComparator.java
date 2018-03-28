package com.zxtd.information.bean;

import java.util.Comparator;

public class SessionComparator implements Comparator<Session> {

	@Override
	public int compare(Session arg0, Session arg1) {
		// TODO Auto-generated method stub
		int flag=String.valueOf(arg0.getSort()).compareToIgnoreCase(String.valueOf(arg1.getSort()));
		if(flag==0){
			return -arg0.getLastSendTime().compareToIgnoreCase(arg1.getLastSendTime());
		}else{
			return -flag;
		}
		/*
		if(arg0.getSort()>arg1.getSort())
			return 1;
		else if(arg0.getSort()==arg1.getSort()){
			return -arg0.getLastSendTime().compareToIgnoreCase(arg1.getLastSendTime());
		}else
			return -1;
		 */
	}

}
