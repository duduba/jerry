package com.zxtd.information.parse;

import java.io.IOException;
import java.util.Map;

import org.jdom2.JDOMException;

public abstract class ParseData {
	public final Result parseXmlToMap(String data){
		XmlToObject toObject = new XmlToObject(data);
		try {
			Map<String, Object> map = toObject.parse();
			return parseMap(map);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract Result parseMap(Map<String, Object> params);
}
