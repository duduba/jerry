package com.zxtd.information.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

import com.zxtd.information.bean.NewImageBean;
import com.zxtd.information.manager.AlbumsManager;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

public class FileUploadHelper {

	private static final long IMAGESIZE=40*1024;
	/**
	 * 
	 * @param params
	 * @param files
	 * @param handler
	 * @param what
	 * @param type  0头像 1相册
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	public NewImageBean uploadFile(Map<String,String> params,Map<String, File> files,Handler handler,int what,int type,int userId) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(Constant.Url.ALBUMS_HEAD_REQUEST_URL+"?userid="+userId+"&type="+type);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000); 
        conn.setDoInput(true);//允许输入
        conn.setDoOutput(true);//允许输出
        conn.setUseCaches(false); 
        conn.setRequestMethod("POST");  //Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "utf-8");
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("contentType", "utf-8");

        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA+ ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }    
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        
        //发送文件数据
        for(Map.Entry<String, File> file : files.entrySet()){
        	StringBuilder sb1 = new StringBuilder();
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
            sb1.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINEND);
            sb1.append(LINEND);
            outStream.write(sb1.toString().getBytes());
            File tpFile=file.getValue();
            InputStream is = new FileInputStream(tpFile);
            long fileLength=tpFile.length();
            long formatFileLength=fileLength;
            if(fileLength>IMAGESIZE){
            	Bitmap bitmap=getSmallBitmap(tpFile.getPath());
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    			int options = 100;  
    			if(tpFile.getName().endsWith(".jpg") || tpFile.getName().endsWith(".jpeg")){
    				bitmap.compress(CompressFormat.JPEG, options, baos);
            	}else if(tpFile.getName().endsWith(".png")){
            		bitmap.compress(CompressFormat.PNG, options, baos);
            	}
    			//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
    			while(baos.toByteArray().length >IMAGESIZE){
    				 baos.reset();//重置baos即清空baos  
    				 if(tpFile.getName().endsWith(".jpg") || tpFile.getName().endsWith(".jpeg")){
    					bitmap.compress(CompressFormat.JPEG, options, baos);
    		         }else if(tpFile.getName().endsWith(".png")){
    		        	bitmap.compress(CompressFormat.PNG, options, baos);
    		        }
    				if(options>10)
    					options -= 10;
    				else{
    					if(options<=5)
    						break;
    					else
    					options -= 1;
    				}
    			}
    			tpFile.delete();
    			tpFile.createNewFile();
    			FileOutputStream tempOutStream=new FileOutputStream(tpFile);
    			baos.writeTo(tempOutStream);
    			tempOutStream.flush();
    			tempOutStream.close();
    			baos.close();
    			is = new FileInputStream(tpFile);
    			formatFileLength=is.available();
            }
            //else{
            	 byte[] buffer = new byte[1024];
                 int len = 0;
                 int size=0;
                 int totalSend=0;
                 while ((len = is.read(buffer)) != -1){
                     outStream.write(buffer, 0, len);
                     totalSend+=len;
                     size+=len;
                     if(size>1024*4){
                     	updateProgress(handler,(totalSend*1.0f/formatFileLength)*100,what);
                     	size=0;
                     }
                 }
                 if(size>0){
                 	updateProgress(handler,(totalSend*1.0f/formatFileLength)*100,what);
                 }
           //}
           is.close();
           outStream.write(LINEND.getBytes());     
        }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        
        // 得到响应码
        int res = conn.getResponseCode(); 
        if(res==200){
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
             byte[] outBuffer = GzipUtil.unGzipBytes(outputStream.toByteArray());
             NewImageBean bean=null;
             if(null!=outBuffer && outBuffer.length>0){
             	String imageData = new String(outBuffer, "UTF-8");
             	Log.e(Constant.TAG, "uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu: "+imageData);
             	bean=new NewImageBean();
             	try{
             		XmlPullParser parser=Xml.newPullParser();
         			parser.setInput(new StringReader(imageData));
         			int eventType=parser.getEventType();
         			while(eventType!=XmlPullParser.END_DOCUMENT){
     					if(eventType==XmlPullParser.START_TAG){
     						String tagName=parser.getName();
     						if(tagName.equals("imageid")){
     							bean.setImgId(Integer.valueOf(parser.nextText()));
     						}else if(tagName.equals("image-url")){
     							bean.setImageUrl(parser.nextText());
     						}else if(tagName.equals("smallImage_url")){
     							bean.setSmallUrl(parser.nextText());
     							bean.setDescribe("");
     							break;
     						}
     					}
     					eventType=parser.next();
     				}
             	}catch(Exception ex){
             		Utils.printException(ex);
             	}
             }
     		inputStream.close();
     		outputStream.close();
     		outStream.close();
            conn.disconnect();
            
            //添加本地图片,升级用户版本号
            AlbumsManager.getInstance().addAlbums(bean);
            //可以复制图片，避免重新下载
            return bean;
        }else{
        	return null;
        }
    }


	private void updateProgress(Handler handler,float size,int what){
		Message msg=handler.obtainMessage();
    	msg.what=what;
    	msg.obj=size;
    	msg.sendToTarget();
	}
	
	
	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}

	
	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);
	    // Calculate inSampleSize
	    if(options.outWidth>480 || options.outHeight>800){
	    	options.inSampleSize = calculateInSampleSize(options, 480, 800);
	    }
	    //Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}
	
}
