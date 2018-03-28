package com.zxtd.information.parse;

import java.io.IOException;
import java.util.Map;

import org.jdom2.JDOMException;

import com.zxtd.information.util.Constant;
import com.zxtd.information.util.XmlUtil;


public class XmlToObject {
	private String mXml;
	private String mAction;
	public XmlToObject(String xml){
		mXml = xml;
	}
	
	public String getAction(){
		return mAction;
	}
	public Map<String, Object> parse() throws JDOMException, IOException{
		Map<String, Object> map = XmlUtil.xmlObject(mXml);
		mAction = (String)map.get(Constant.XmlMapKey.ACTION);
		return map;
	}
}
