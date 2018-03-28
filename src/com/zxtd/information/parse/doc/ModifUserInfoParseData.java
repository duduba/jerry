package com.zxtd.information.parse.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.util.Log;

import com.zxtd.information.parse.Result;

public class ModifUserInfoParseData extends ParseXmlToDocument {
	public static final String RESULT_CODE = "resultCode";
	private Result mResult;
	public ModifUserInfoParseData(){
		mResult = new Result();
	}
	@Override
	public Result parseDoc(Document doc) {
		mResult = new Result();
		Element root = doc.getDocumentElement();
		Node node = root.getFirstChild();
		Node text = node.getFirstChild();
		String resultCode = text.getNodeValue();
		Log.i(this.getClass().getName(), "resultCode : " + resultCode);
		mResult.put(RESULT_CODE, resultCode);
		return mResult;
	}
	
	public void addResult(String key, String value){
		mResult.put(key, value);
	}

}
