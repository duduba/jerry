package com.zxtd.information.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class TimeUtils {

	public static String formatDate(String datetime){
		if(TextUtils.isEmpty(datetime)){
			return "";
		}
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try {
			date = format.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date today=new Date();
		format.applyPattern("yyyy-MM-dd");
		
		String todayDate=format.format(today);
		String otherDate=format.format(date);
		if(todayDate.equals(otherDate)){
			format.applyPattern("HH:mm");
			return format.format(date);
		}else{
			return datetime;//otherDate;
		}	
	}
	
	public static String getNow(){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	
	public static int getDiffTimeMinute(String time1,String time2){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1=null;
		Date date2=null;
		try {
			date1 = format.parse(time1);
			date2=format.parse(time2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		Calendar cal1=Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2=Calendar.getInstance();
		cal2.setTime(date2);
		cal2.
		if(cal2.)
		*/
		long minute=(date2.getTime()-date1.getTime())/1000/60;
		return (int)minute;
	}
	
	/*
	private static int compareDate(String dateTime){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date date=format.parse(dateTime);
			Date now=new Date();
			

		}catch(Exception ex){
			Utils.printException(ex);
		}
		return 0;
	}
	
	
	public static String parseDateTime(String dateTime){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date date=format.parse(dateTime);
			Date now=new Date();
			

		}catch(Exception ex){
			Utils.printException(ex);
		}
		return null;
	}
	*/
	
}
