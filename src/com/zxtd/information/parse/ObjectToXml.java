package com.zxtd.information.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class ObjectToXml {
	private Map<String, String> mMap;
	private String mRequestCode ;
	private static final String TAG = "com.zxtd.information.parse.ObjectToXml";
	public ObjectToXml(Map<String, String> map, String requestCode){
		this.mMap = map;
		this.mRequestCode = requestCode;
	}
	
	public String getXml(){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("utf-8",null);
			serializer.startTag(null, "ZXTD");
			
			serializer.startTag(null, "action");
			serializer.text(mRequestCode);
			serializer.endTag(null, "action");
			
			serializer.startTag(null, "DATA");
			
			Iterator<String> iterator = mMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				serializer.startTag(null, key);
				String text = mMap.get(key);
				if(text != null){
					serializer.text(text);
				}else{
					serializer.text("");
				}
				
				serializer.endTag(null, key);
			}
			
			serializer.endTag(null, "DATA");
			serializer.endTag(null, "ZXTD");
			serializer.endDocument(); 
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, writer.toString());
		return writer.toString();
	}
}
