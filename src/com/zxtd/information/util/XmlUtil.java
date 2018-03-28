package com.zxtd.information.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.util.Log;


public class XmlUtil {


	/**
	 * 取得xml文件的根节点名称，即消息名称。
	 * 
	 * @param xmlStr
	 *            xml内容
	 * @return String 返回名称
	 */
	public static String getRootName(String xmlStr) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xmlStr));
		Element root = doc.getRootElement();
		return root.getName();
	}

	/**
	 * 把xml文件转换为map形式，其中key为有值的节点名称，并以其所有的祖先节点为前缀，用
	 * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
	 * 
	 * @param xmlStr
	 *            xml内容
	 * @return Map 转换为map返回
	 */
	public static Map<String, String> xml2Map(String xmlStr)
			throws JDOMException, IOException {
		Map<String, String> rtnMap = new HashMap<String, String>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xmlStr));
		// 得到根节点
		Element root = doc.getRootElement();
		String rootName = root.getName();
		rtnMap.put("root.name", rootName);
		// 调用递归函数，得到所有最底层元素的名称和值，加入map中
		convert(root, rtnMap, rootName);

		return rtnMap;
	}

	/**
	 * 递归函数，找出最下层的节点并加入到map中，由xml2Map方法调用。
	 * 
	 * @param e
	 *            xml节点，包括根节点
	 * @param map
	 *            目标map
	 * @param lastname
	 *            从根节点到上一级节点名称连接的字串
	 */
	@SuppressWarnings("unchecked")
	public static void convert(Element e, Map<String, String> map,
			String lastname) {
		if (e.getAttributes().size() > 0) {
			Iterator it_attr = e.getAttributes().iterator();
			while (it_attr.hasNext()) {
				Attribute attribute = (Attribute) it_attr.next();
				String attrname = attribute.getName();
				String attrvalue = e.getAttributeValue(attrname);
				map.put(lastname + "." + attrname, attrvalue);
			}
		}
		List children = e.getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element child = (Element) it.next();
			String name = lastname + "." + child.getName();
			// 如果有子节点，则递归调用
			if (child.getChildren().size() > 0) {
				convert(child, map, name);
			} else {
				// 如果没有子节点，则把值加入map
				map.put(name, child.getText());
				// 如果该节点有属性，则把所有的属性值也加入map
				if (child.getAttributes().size() > 0) {
					Iterator attr = child.getAttributes().iterator();
					while (attr.hasNext()) {
						Attribute attribute = (Attribute) attr.next();
						String attrname = attribute.getName();
						String attrvalue = child.getAttributeValue(attrname);
						map.put(name + "." + attrname, attrvalue);
					}
				}
			}
		}
	}

	/**
	 * 把xml文件转换为list形式，其中每个元素是一个map，map中的key为有值的节点名称，并以其所有的祖先节点为前缀，用
	 * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
	 * 
	 * @param xmlStr
	 *            xml内容
	 * @return Map 转换为map返回
	 */
	public static List<Map<String, String>> xml2List(String xmlStr)
			throws JDOMException, IOException {
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
		Map<String, String> rtnMap = new HashMap<String, String>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xmlStr));
		// 得到根节点
		Element root = doc.getRootElement();
		String rootName = root.getName();
		rtnMap.put("root.name", rootName);

		// 调用递归函数，得到所有最底层元素的名称和值，加入map中
		convert2List(root, rtnMap, rootName, rtnList);
		if (rtnList.size() == 0)
			rtnList.add(rtnMap);
		return rtnList;
	}

	/**
	 * 递归函数，找出最下层的节点并加入到map中，如果有相同的节点，则加入list中， 由xml2List方法调用。
	 * 
	 * @param e
	 *            xml节点，包括根节点
	 * @param map
	 *            目标map
	 * @param lastname
	 *            从根节点到上一级节点名称连接的字串
	 * @param list
	 *            相同节点生成map放入list中
	 */
	@SuppressWarnings("unchecked")
	public static void convert2List(Element e, Map<String, String> map,
			String lastname, List<Map<String, String>> list) {
		if (e.getAttributes().size() > 0) {
			Iterator it_attr = e.getAttributes().iterator();
			while (it_attr.hasNext()) {
				Attribute attribute = (Attribute) it_attr.next();
				String attrname = attribute.getName();
				String attrvalue = e.getAttributeValue(attrname);
				map.put(lastname + "." + attrname, attrvalue);
			}
		}
		List children = e.getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element child = (Element) it.next();
			String name = lastname + "." + child.getName();
			// 如果有子节点，则递归调用
			if (child.getChildren().size() > 0) {
				convert(child, map, name);
			} else {
				// 如果没有子节点，则把值加入map
				map.put(name, child.getText());
				// 如果该节点有属性，则把所有的属性值也加入map
				if (child.getAttributes().size() > 0) {
					Iterator attr = child.getAttributes().iterator();
					while (attr.hasNext()) {
						Attribute attribute = (Attribute) attr.next();
						String attrname = attribute.getName();
						String attrvalue = child.getAttributeValue(attrname);
						map.put(name + "." + attrname, attrvalue);
					}
				}
			}
			// 如果有相同节点，则加入list中，不考虑子节点中又有相同节点的情况
			if (e.getChildren(child.getName()).size() > 1) {
				Map<String, String> aMap = new HashMap<String, String>();
				aMap.putAll(map);
				list.add(aMap);
				map = new HashMap<String, String>();
				map.put("root.name", aMap.get("root.name"));
			}

		}
	}
	
	/**
	 * 
	 * 将xml转换成对象
	 * */
	public static Map<String, Object> xmlObject(String xmlStr) throws JDOMException, IOException{
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xmlStr));
		// 得到根节点

		Element root = doc.getRootElement();
		String rootName = root.getName();
		analysisElement(root, rtnMap, rootName);
		return rtnMap;
	}
	//解析单个节点
	private static void analysisElement(Element element, Map<String, Object> rtnMap, String lastName){
		List list = element.getChildren();
		int size = list.size();
		if(size > 0){
			Map<String, List<Element>> elements = new HashMap<String, List<Element>>();
			for (int i = 0; i < size; i++) {
				Element child = (Element)list.get(i);
				String key = lastName + "." + child.getName();
				if(elements.containsKey(key)){
					List<Element> values = elements.get(key);
					values.add(child);
				}else{
					List<Element> values = new ArrayList<Element>();
					values.add(child);
					elements.put(key, values);
				}
			}
			
			Iterator<String> iterator = elements.keySet().iterator();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				List<Element> values = elements.get(key);
				if(values.size() == 1){
					analysisElement(values.get(0), rtnMap, key);
				}else{
					List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
					rtnMap.put(key, maps);
					analysisListElement(values, maps, key);
				}
			}
			
		}else{
			addValueToMap(element, rtnMap, lastName);
		}
	}
	//解析数组节点
	private static void analysisListElement(List<Element> elements, List<Map<String, Object>> maps, String lastName){
		for(int i = 0; i < elements.size(); i ++){
			Element element = elements.get(i);
			Map<String, Object> rtnMap = new HashMap<String, Object>();
			analysisElement(element, rtnMap, lastName);
			maps.add(rtnMap);
		}
	}
	
	//添加根节点数据
	private static void addValueToMap(Element element, Map<String, Object> rtnMap, String lastName){
		rtnMap.put(lastName, element.getText());
	}

	/**
	 * 打印map 的所有key和value
	 * 
	 * @param map
	 */
	public static void printMap(Map<String, String> map) {
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Log.i(XmlUtil.class.getName(), map.get(key));
		}
	}

	/**
	 * 返回解析内容
	 * @param xmlDoc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List xmlElements(String xmlDoc) {
		// 创建一个新的字符串
		StringReader read = new StringReader(xmlDoc);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		// 创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder();
		try {
			// 通过输入源构造一个Document
			Document doc = sb.build(source);
			// 取的根元素
			Element root = doc.getRootElement();
			Log.i(XmlUtil.class.getName(), root.getName());// 输出根元素的名称（测试）
			// 得到根元素所有子元素的集合
			List list = root.getChildren();
			Element et = null;
			for (int i = 0; i < list.size(); i++) {
				et = (Element) list.get(i);// 循环依次得到子元素
				@SuppressWarnings("unused")
				String action = et.getText();
				//System.out.println("444444  " + et.getText());

			}

			/**
			 * 如要取<row>下的子元素的名称
			 */
//			et = (Element) list.get(0);
//			List list1 = et.getChildren();
//			for (int j = 0; j < list1.size(); j++) {
//				Element xet = (Element) list1.get(j);
//				
//			}
			return list;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> map;

		try {
			map = XmlUtil
					.xml2Map("<IICA-Req>" +
							  "<action>" +
							     "<id>a</id>"+
							     "<id>b</id>"+
							     "<id>c</id>"+
							     "<id>d</id>"+
							  "</action>" +
							  "<cred>1000</cred>" +
							   "<uname>lujun</uname>" +
							"</IICA-Req>");
			String action = map.get("IICA-Req.action.id");
			String uname = map.get("IICA-Req.uname");
			System.out.println(action+"======"+uname);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
