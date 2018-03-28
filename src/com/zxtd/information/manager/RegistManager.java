package com.zxtd.information.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Xml;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.City;
import com.zxtd.information.bean.District;
import com.zxtd.information.bean.Province;
import com.zxtd.information.db.DBHelper;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.HttpHelper;
import com.zxtd.information.util.ResponseParam;
import com.zxtd.information.util.Utils;

public class RegistManager {

	private static RegistManager instance=null;
	
	private RegistManager(){
	}
	
	public static RegistManager newInstance(){
		if(null==instance){
			instance=new RegistManager();
		}
		return instance;
	}
	
	/**
	 * 注册
	 * @param email
	 * @param nickName
	 * @param password
	 */
	public ResponseParam doRegist(String email,String nickName,String password,int accountType,String avatarImg,String location,int gender,String sign,String work,String birth,String imsi){
		Map<String,String> map=new HashMap<String,String>();
		map.put("useraccount", email);
		map.put("nickname", nickName);
		map.put("password",password);
		map.put("account-type", String.valueOf(accountType));
		map.put("avatar_large", avatarImg);
		map.put("location", location);
		map.put("gender", String.valueOf(gender));
		map.put("aspiration", sign);
		map.put("profession", work);
		map.put("birthday", birth);
		map.put("imsi", imsi);
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_REGIST);
		ResponseParam param=null;
		try{
			String result=helper.doPost(map);
			if(!TextUtils.isEmpty(result)){
				XmlPullParser parser=Xml.newPullParser();
				parser.setInput(new StringReader(result));
				int eventType=parser.getEventType();
				param=new ResponseParam();
				while(eventType!=XmlPullParser.END_DOCUMENT){
					if(eventType==XmlPullParser.START_TAG){
						String tagName=parser.getName();
						if(tagName.equals("regist-status")){
							param.setStatus(parser.nextText());
						}else if(tagName.equals("failure-reason")){
							param.setFailureReason(parser.nextText());
						}else if(tagName.equals("userid")){
							param.setReturnId(Integer.valueOf(parser.nextText()));
						}
					}
					eventType=parser.next();
				}
			}
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return param;
	}
	
	
	/**
	 * 获取热门城市
	 * @param context
	 * @return
	 */
	public List<City> getHotCity(){
		List<City> list=null;
		SQLiteDatabase db=DBHelper.getDataBase();
		if(null!=db){
			list=new ArrayList<City>(50);
			Cursor cursor=db.rawQuery("select * from city where isHotCity<>0 order by sort asc", null);
			while(cursor.moveToNext()){
				City city=new City();
				city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
				city.setCityKey(cursor.getString(cursor.getColumnIndex("cityKey")));
				city.setDistrictId(cursor.getInt(cursor.getColumnIndex("districtId")));
				city.setSort(cursor.getInt(cursor.getColumnIndex("sort")));
				city.setHot(cursor.getInt(cursor.getColumnIndex("isHotCity"))>0 ? true : false);
				city.setPyCityName(cursor.getString(cursor.getColumnIndex("pycityName")));
				city.setPyShort(cursor.getString(cursor.getColumnIndex("pyshort")));
				list.add(city);
			}
			cursor.close();
			db.close();
		}
		return list;
	}
	
	
	/**
	 * 获取省份
	 * @return
	 */
	public List<Province> getProvince(){
		List<Province> list=null;
		SQLiteDatabase db=DBHelper.getDataBase();
		if(null!=db){
			list=new ArrayList<Province>(40);
			Cursor cursor=db.rawQuery("select * from prov order by sort desc", null);
			while(cursor.moveToNext()){
				Province prov=new Province();
				prov.setPrivId(cursor.getInt(cursor.getColumnIndex("id")));
				prov.setProvName(cursor.getString(cursor.getColumnIndex("ProvName")));
				prov.setSort(cursor.getInt(cursor.getColumnIndex("sort")));
				list.add(prov);
			}
			cursor.close();
			db.close();
		}
		return list;
	}
	
	
	/**
	 * 获取省下面的地级市
	 * @param provId
	 * @return
	 */
	public List<District> getDistricts(int provId){
		List<District> list=null;
		SQLiteDatabase db=DBHelper.getDataBase();
		if(null!=db){
			list=new ArrayList<District>(40);
			Cursor cursor=db.rawQuery("select * from district where proId=? order by districtkey", new String[]{String.valueOf(provId)});
			while(cursor.moveToNext()){
				District dis=new District();
				dis.setId(cursor.getInt(cursor.getColumnIndex("id")));
				dis.setDistrictName(cursor.getString(cursor.getColumnIndex("districtName")));
				dis.setDistrictKey(cursor.getString(cursor.getColumnIndex("districtKey")));
				dis.setProId(cursor.getInt(cursor.getColumnIndex("proId")));
				dis.setSort(cursor.getInt(cursor.getColumnIndex("sort")));
				dis.setAreaKey(cursor.getString(cursor.getColumnIndex("areaKey")));
				list.add(dis);
			}
			cursor.close();
			db.close();
		}
		return list;
	}
	
	
	/**
	 * 完善用户资料
	 * @param sign
	 * @param area
	 * @param work
	 * @param sex
	 * @param birth
	 * @return
	 */
	public int completeUserInformation(int userid,String sign,String area,String work,String sex,String birth){
		Map<String,String> map=new HashMap<String,String>();
		map.put("userid", String.valueOf(userid));
		map.put("aspiration",sign);
		map.put("location", area);
		map.put("profession", work);
		map.put("sex", sex);
		map.put("birthday", birth);
		int resultCode=-1;
		HttpHelper helper=new HttpHelper(Constant.RequestCode.MINE_COMPLETE_USER_INFORMATION);
		try{
			String result=helper.doPost(map);
			 if(!TextUtils.isEmpty(result)){
					XmlPullParser parser=Xml.newPullParser();
					parser.setInput(new StringReader(result));
					int eventType=parser.getEventType();
					while(eventType!=XmlPullParser.END_DOCUMENT){
						if(eventType==XmlPullParser.START_TAG){	
							String tagName=parser.getName();
							if(tagName.equals("result-code")){
								resultCode=Integer.valueOf(parser.nextText());
							}
						}
						eventType=parser.next();
					}
			 }
		}catch(Exception ex){
			Utils.printException(ex);
		}
		return resultCode;
	}
	
	
}
