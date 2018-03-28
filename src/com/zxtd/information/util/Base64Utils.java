package com.zxtd.information.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.text.TextUtils;

import com.tencent.weibo.sdk.android.component.sso.tools.Base64;

public class Base64Utils {

	public static String encodeFile(File file){
		 byte[] data = null;   
	        // 读取图片字节数组   
	        try {   
	            InputStream in = new FileInputStream(file);   
	            data = new byte[in.available()];   
	            in.read(data);   
	            in.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }
	        // 对字节数组Base64编码   
	       return Base64.encode(data);
	}
	
	
	public static boolean decodeFile(String source,String saveFile){
		   if (TextUtils.isEmpty(source)) // 图像数据为空   
	            return false;   
	        try {   
	            // Base64解码   
	            byte[] bytes = Base64.decode(source);   
	            for (int i = 0; i < bytes.length; ++i) {   
	                if (bytes[i] < 0) {// 调整异常数据   
	                    bytes[i] += 256;   
	                }   
	            }   
	            // 生成jpeg图片   
	            OutputStream out = new FileOutputStream(saveFile);   
	            out.write(bytes);   
	            out.flush();   
	            out.close();   
	            return true;   
	        } catch (Exception e) {   
	            return false;   
	        }   
	}
	
	
	public static String formatString(String source){
		String temp="                      ";
		if(source.length()<Constant.XMPP_HEAD_LENGTH){
			return source+temp.substring(0,(Constant.XMPP_HEAD_LENGTH-source.length()));
		}
		return source;
	}
	
	
}
