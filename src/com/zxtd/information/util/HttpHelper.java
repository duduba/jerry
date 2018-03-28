package com.zxtd.information.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlSerializer;

import com.zxtd.information.application.ZXTD_Application;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

public class HttpHelper {

	private static final String ENCODE="utf-8";
	private static final int REQUEST_TIMEOUT=30000;//设置请求超时30秒钟
	private static final int SO_TIMEOUT = 30000;  //设置等待数据超时时间30秒钟  
	
	private String url=null;
	private String mXml="";
	private String requestCode=null;
	
	public HttpHelper(String requestCode){
		this.requestCode=requestCode;
	}
	
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setmXml(String mXml) {
		this.mXml = mXml;
	}
	
	public String doPost(RequestParam param) throws Exception{
		if(null!=param){
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			
			try {
				serializer.setOutput(writer);
				serializer.startDocument("utf-8",null);
				serializer.startTag(null, "ZXTD");
				
				serializer.startTag(null, "action");
				serializer.text(requestCode);
				serializer.endTag(null, "action");
				
				serializer.startTag(null, "DATA");
				
				Class<? extends RequestParam> cla=param.getClass();
				Field selfFields[]=cla.getDeclaredFields();
				Field parentFields[]=cla.getFields();
				Field fields[]=new Field[selfFields.length+parentFields.length];
				System.arraycopy(selfFields, 0, fields, 0, selfFields.length);
				System.arraycopy(parentFields, selfFields.length, fields,selfFields.length , parentFields.length);
				for(Field field : fields){
					String filedName=field.getName();
					String m_filedName=filedName.substring(0,1).toUpperCase(Locale.CHINA)+filedName.substring(1);
					Method method=cla.getMethod("get"+m_filedName, String.class);
					String value=method.invoke(param).toString();
					serializer.startTag(null, filedName);
					if(value != null){
						serializer.text(value);
					}else{
						serializer.text("");
					}
					serializer.endTag(null, filedName);
				}
				
				//System.arraycopy(src, srcPos, dst, dstPos, length)
				/*
				Method methods[]=cla.getMethods();
				for(Method m : methods){
					if(m.getName().startsWith("get")){
						String value=m.invoke(param).toString();
						
					}
				}
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
				}*/
				
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
			Log.i(Constant.TAG, writer.toString());
			mXml=writer.toString();
		}
		return doPost();
	}
	
	
	public String doPost(Map<String,String> map) throws Exception{
		if(null==map){
			map=new HashMap<String,String>();
		}
		map.put("uuid", Utils.UUID);
		map.put("version", ZXTD_Application.versionName);
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("utf-8",null);
			serializer.startTag(null, "ZXTD");
			
			serializer.startTag(null, "action");
			serializer.text(requestCode);
			serializer.endTag(null, "action");
			
			serializer.startTag(null, "DATA");
	
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				serializer.startTag(null, key);
				String text = map.get(key);
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
		mXml=writer.toString();
		return doPost();	
	}
	
	
	
	private String doPost() throws ClientProtocolException, IOException {
		if(TextUtils.isEmpty(url)){
			url=Constant.Url.REQUEST_URL;
		}
		String json = "";
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		HttpPost httpPost = new HttpPost(url);
		if(mXml != null && !mXml.equals("")){
			ByteArrayEntity entity = new ByteArrayEntity(GzipUtil.gzipBytes(mXml.getBytes(ENCODE)));
			httpPost.setEntity(entity);
		}
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);		
		HttpResponse response = httpClient.execute(httpPost);
		byte[] buffer = new byte[1024];
		int length = 0;
		if(response.getStatusLine().getStatusCode() == 200){
			inputStream = response.getEntity().getContent();
			while((length = inputStream.read(buffer)) >0){
				outputStream.write(buffer, 0, length);
			}
			
			byte[] outPutBytes = outputStream.toByteArray();
			if(outPutBytes == null || outPutBytes.length == 0){
				json = "";
			}else {
				byte[] out = GzipUtil.unGzipBytes(outPutBytes);
				if((out == null) || (out.length == 0)) {
					json = "";
				}else {
					json = new String(out, ENCODE);
				}
			}
			Log.e(Constant.TAG,"请求接口参数： "+mXml);
			Log.e(Constant.TAG,"请求接口结果： "+json);
			inputStream.close();
		}else{
			Log.e(Constant.TAG, "请求出错");
		}
		
		outputStream.close();
		return json;
	}
	
	
	

	
}
