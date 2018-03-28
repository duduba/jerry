package com.zxtd.information.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.test.IsolatedContext;
import android.util.Log;

public class GzipUtil {

	//private static final Logger log = LogManager.getLogger(GzipUtil.class);
	
	private static final String Tag = "com.zxtd.information.util.GzipUtil";

	/**
	 * gzip解压
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] unGzipBytes(byte[] input) {
		
		//log.info("解压前大小：" + input.length);
		if((input == null) || (input.length == 0)) {
			return new byte[0];
		}
		int cachesize = 2048;
		byte[] out = null;
		ByteArrayOutputStream bos = null;
		GZIPInputStream gzip = null;
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(input);
			gzip = new GZIPInputStream(byteArrayInputStream);
			byte[] buf = new byte[cachesize];
			int num = -1;
			bos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				bos.write(buf, 0, num);
			}
			out = bos.toByteArray();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(bos != null){
					bos.flush();
					bos.close();
					gzip.close();
					byteArrayInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		Log.i(Tag,"解压后大小：" + out.length + ", 压缩率："
				+ (out.length == 0 ? 0 : input.length * 100 / out.length) + "%");

		return out;
	}

	/**
	 * gzip压缩
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] gzipBytes(byte[] input) {

		Log.i(Tag,"压缩前大小：" + input.length);

		byte[] out = null;
		GZIPOutputStream gzip = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			// gzip压缩流
			gzip = new GZIPOutputStream(byteArrayOutputStream);
			gzip.write(input);
			gzip.finish();

			out = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				gzip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.i(Tag,"压缩后大小：" + out.length + ", 压缩率："
				+ (out.length * 100 / input.length) + "%");

		return out;
	}

}
