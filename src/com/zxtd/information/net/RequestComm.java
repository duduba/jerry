package com.zxtd.information.net;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.bean.RequestBean;
import com.zxtd.information.parse.ObjectToXml;
import com.zxtd.information.parse.ParseData;
import com.zxtd.information.parse.Result;
import com.zxtd.information.parse.doc.ParseXmlToDocument;
import com.zxtd.information.util.Constant;
import com.zxtd.information.util.Utils;

public class RequestComm {
	private RequestBean mRequestBean;
	private String mRequestCode;
	private static Handler handler;
	public RequestComm(RequestBean requestBean, String requestCode){
		mRequestBean = requestBean;
		mRequestCode = requestCode;
	}
	
	public RequestComm(RequestBean requestBean, String requestCode, Handler handler){
		mRequestBean = requestBean;
		mRequestCode = requestCode;
		RequestComm.handler = handler;
	}
	
	public void doPost(ParseData parseData, RequestCallBack requestCallBack){
		int responseCode = Constant.ResponseCode.SUCCESS;
		Result result = null;
		ZXTD_HttpRequest request = new ZXTD_HttpRequest(Constant.Url.REQUEST_URL);
		ObjectToXml objectToXml = new ObjectToXml(initMap(mRequestBean), mRequestCode);
		request.setParmas(objectToXml.getXml());
		try {
			String response = request.doPost();
			if(!Utils.isEmpty(response)){
				result = parseData.parseXmlToMap(response);
			}else{
				responseCode = Constant.ResponseCode.NET_ERROR;
			}
		} catch (ClientProtocolException e) {
			responseCode = Constant.ResponseCode.NET_ERROR;
			e.printStackTrace();
		} catch (IOException e) {
			responseCode = Constant.ResponseCode.NET_ERROR;
			e.printStackTrace();
		} finally{

			if(responseCode == Constant.ResponseCode.SUCCESS && result == null){
				responseCode = Constant.ResponseCode.XML_ERROR;
			}
			setCallBack(responseCode, result, requestCallBack);
		}
	}
	
	public void doPost(ParseXmlToDocument parseData, RequestCallBack requestCallBack){
		int responseCode = Constant.ResponseCode.SUCCESS;
		Result result = null;
		ZXTD_HttpRequest request = new ZXTD_HttpRequest(Constant.Url.REQUEST_URL);
		ObjectToXml objectToXml = new ObjectToXml(initMap(mRequestBean), mRequestCode);
		request.setParmas(objectToXml.getXml());
		try {
			String response = request.doPost();
			if(!Utils.isEmpty(response)){
				result = parseData.parseXmlToDoc(response);
			}else{
				responseCode = Constant.ResponseCode.NET_ERROR;
			}
		} catch (ClientProtocolException e) {
			responseCode = Constant.ResponseCode.NET_ERROR;
			e.printStackTrace();
		} catch (IOException e) {
			responseCode = Constant.ResponseCode.NET_ERROR;
			e.printStackTrace();
		} finally{

			if(responseCode == Constant.ResponseCode.SUCCESS && result == null){
				responseCode = Constant.ResponseCode.XML_ERROR;
			}
			setCallBack(responseCode, result, requestCallBack);
		}
	}
	
	public static void setHandler(Handler handler){
		RequestComm.handler = handler;
	}
	
	public void doPost(ParseData parseData, RequestCallBack requestCallBack, Handler handler){
		RequestComm.handler = handler;
		doPost(parseData, requestCallBack);
	}
	
	private Map<String, String> initMap(RequestBean requestBean){
		Map<String, String> map = requestBean.getMap();
		map.put("uuid", Utils.UUID);
		map.put("version", ZXTD_Application.versionName);
		return map;
	}
	
	private void setCallBack(final int responseCode, final Result result, final RequestCallBack requestCallBack){
		handler.post(new Runnable() {
			
			public void run() {
				if(responseCode == Constant.ResponseCode.SUCCESS){
					requestCallBack.requestSuccess(mRequestCode, result);
				}else{
					requestCallBack.requestError(mRequestCode, responseCode);
				}
			}
		});
	}
	
}
