package com.zxtd.information.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jdom2.JDOMException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.zxtd.information.parse.XmlToObject;
import com.zxtd.information.util.GzipUtil;
import com.zxtd.information.util.Utils;

public class ZXTD_HttpRequest {
	private String url;
	private String mXml;
	private static String TAG = "com.zxtd.information.net.zxtd_HttpRequest";
	private String encode = "UTF-8";
	public ZXTD_HttpRequest(String url){
		this.url = url;
	}
	
	public ZXTD_HttpRequest(){
		
	}
	
	public void setParmas(String xml){
		this.mXml = xml;
	}
	
	public void setEncode(String encoding){
		encode = encoding;
	}
	
	public String doPost() throws ClientProtocolException, IOException{
		String json = "";
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		HttpPost httpPost = new HttpPost(url);
		if(mXml != null && !mXml.equals("")){
			ByteArrayEntity entity = new ByteArrayEntity(GzipUtil.gzipBytes(mXml.getBytes(encode)));
			httpPost.setEntity(entity);
		}
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);		
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
					json = new String(out, encode);
				}
			}
			
			Log.i(TAG, json);
			inputStream.close();
		}else{
			Log.e(TAG, "请求出错");
		}
		
		outputStream.close();
		return json;
	}
	
	public String uploadImages(Bitmap bitmap, HashMap<String, String> params){
		try {   
            Log.i("Test", "theTrue --");  
            String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线            
            URL urlImage = new URL(url);   
           
            HttpURLConnection conn = (HttpURLConnection)urlImage.openConnection();   
       
            // 发送POST请求必须设置如下两行      
            conn.setDoOutput(true);
       
            conn.setDoInput(true);   
       
            conn.setRequestMethod("POST"); 
            
            conn.setUseCaches(false); 
       
            conn.setRequestProperty("connection", "Keep-Alive");   
       
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");   
       
            conn.setRequestProperty("Charsert", encode);   
       
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);   
       
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线 
            
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + "\r\n");
                sb.append("Content-Type: text/plain; charset=" + encode + "\r\n");
                sb.append("Content-Transfer-Encoding: 8bit" + "\r\n");
                sb.append("\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }   
        
           
            sb.append("--");   
            sb.append(BOUNDARY);   
            sb.append("\r\n");   
            sb.append("Content-Disposition: form-data;name=\"file"+"\";filename=\"" + "0.jpg" + "\"\r\n");  
            
//            String imageType = getImageType(file.getName());
//            Log.i("Test---------------", imageType);
            sb.append("Content-Type: " + "image/jpeg" + "\r\n\r\n");   
       
            System.out.println(sb.toString());
            
            byte[] data = sb.toString().getBytes();   
         
            out.write(data);
            
            // 图片数据流
            Log.i("Test", "line --");   
            bitmap.compress(CompressFormat.JPEG, 50, out);
            out.write(end_data);  // 结束标记
            out.flush(); 
           
            out.close(); 

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            InputStream inputStream = conn.getInputStream();
            
            byte[] btyeBuffer = new byte[1024];
            int length = 0;
            while((length = inputStream.read(btyeBuffer)) > 0) {  
            	try {
            		outputStream.write(btyeBuffer,0,length);
				} catch (Exception e) {
					outputStream.write(btyeBuffer,0,length);
				}
            } 
            
            String imageUrl;
            byte[] outBuffer = GzipUtil.unGzipBytes(outputStream.toByteArray());
            if(outBuffer == null || outBuffer.length == 0) {
            	imageUrl = null;
            }else {
            	String imageData = new String(outBuffer, encode);
            	Log.i("---------------------Test", "strBuffer --" + imageData);
            	imageUrl = parseData1(imageData);
            }
			
			inputStream.close();
			outputStream.close();
            conn.disconnect();
            return imageUrl;
        } catch(Exception e) {   
            e.printStackTrace();
            return null;
        }   
	}
	
	public String uploadImages(String imagePath, int imageWidth, int imageHeight) {
		 try {   
	            Log.i("Test", "theTrue --");  
	            String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线            
	            URL urlImage = new URL(url);   
	           
	            HttpURLConnection conn = (HttpURLConnection)urlImage.openConnection();   
	       
	            // 发送POST请求必须设置如下两行      
	            conn.setDoOutput(true);
	       
	            conn.setDoInput(true);   
	       
	            conn.setRequestMethod("POST"); 
	            
	            conn.setUseCaches(false); 
	       
	            conn.setRequestProperty("connection", "Keep-Alive");   
	       
	            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");   
	       
	            conn.setRequestProperty("Charsert", encode);   
	       
	            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);   
	       
	            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线 
	            
	            conn.connect();
	            OutputStream out = new DataOutputStream(conn.getOutputStream());     
	        
	            StringBuilder sb = new StringBuilder();
	            sb.append("--");   
	            sb.append(BOUNDARY);   
	            sb.append("\r\n");   
	            sb.append("Content-Disposition: form-data;name=\"newsImage"+"\";filename=\"" + "0.jpg" + "\"\r\n");  
	            
//	            String imageType = getImageType(file.getName());
//	            Log.i("Test---------------", imageType);
	            sb.append("Content-Type: " + "image/jpeg" + "\r\n\r\n");   
	       
	            byte[] data = sb.toString().getBytes();   
	         
	            out.write(data);
	            
	            // 图片数据流
	            Log.i("Test", "line --");   
	            Bitmap bitmap = Utils.decodeSampledBitmapFromResource(imagePath, imageWidth, imageHeight);
	            bitmap.compress(CompressFormat.JPEG, 50, out);
	            out.write(end_data);  // 结束标记
	            out.flush(); 
	           
	            out.close(); 
	            bitmap.recycle(); 
 
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            
	            InputStream inputStream = conn.getInputStream();
	            
	            byte[] btyeBuffer = new byte[1024];
	            int length = 0;
	            while((length = inputStream.read(btyeBuffer)) > 0) {  
	            	try {
	            		outputStream.write(btyeBuffer,0,length);
					} catch (Exception e) {
						outputStream.write(btyeBuffer,0,length);
					}
	            } 
	            
	            String imageUrl;
	            byte[] outBuffer = GzipUtil.unGzipBytes(outputStream.toByteArray());
	            if(outBuffer == null || outBuffer.length == 0) {
	            	imageUrl = null;
	            }else {
	            	String imageData = new String(outBuffer, encode);
	            	Log.i("---------------------Test", "strBuffer --" + imageData);
	            	imageUrl = parseData(imageData);
	            }
				
				inputStream.close();
				outputStream.close();
	            conn.disconnect();
	            return imageUrl;
	        } catch(Exception e) {   
	            e.printStackTrace();
	            return null;
	        }   
	}
	
	private String parseData1(String in) throws JDOMException, IOException{
		if(in == null || "".equals(in)){
			return null;
		}
		
		XmlToObject xmlToObject = new XmlToObject(in);
		Map<String, Object> map = xmlToObject.parse();
		if(map.get("ZXTD.result-code").equals("1")) {
			return map.get("ZXTD.DATA.image-url").toString();
		} else {
			return null;
		}
	}
	
	private String parseData(String buffer) throws JDOMException, IOException {
		if(buffer == null || "".equals(buffer)){
			return null;
		}
		
		XmlToObject xmlToObject = new XmlToObject(buffer);
		Map<String, Object> map = xmlToObject.parse();
		if(map.get("ZXTD.responsecode").equals("200")) {
			return map.get("ZXTD.DATA.image-url").toString();
		} else {
			return null;
		}
	}
	
	public interface DownLoadListener{
		void start(int totalSize);
		void progress(int size);
		void finish();
	}
}
