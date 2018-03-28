package com.zxtd.information.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.zxtd.information.application.ZXTD_Application;
import com.zxtd.information.util.Utils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DBHelper {

	//static{
	//	copyDataBase();
	//}
	
	public static void copyDataBase(){
		try{
			boolean isDelete=false;
			String baseDir =Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/";
	        File filePath = new File(baseDir);
	        if (!filePath.exists()){
	        	filePath.mkdirs();
	        }
	        filePath=new File(baseDir+"citys.db");
	        if(filePath.exists()){
	        	if(filePath.length()<1024*1024){
	        		filePath.delete();
	        		isDelete=true;
	        	}
	        }
	        
        	if(!filePath.exists() || isDelete){
        		filePath.createNewFile();
        		String[] splitFile=new String[]{"citydb1.db","citydb2.db"};
        		FileOutputStream fos = new FileOutputStream(filePath);
        		
        		for(int i=0;i<splitFile.length;i++){
        			InputStream inStream=ZXTD_Application.getMyContext().getAssets().open(splitFile[i]);
                	byte[] buffer = new byte[1024];
                	int count = -1;
                	while ((count = inStream.read(buffer)) > 0){
                		fos.write(buffer, 0, count);
                	}
                	fos.flush();
                	inStream.close();
        		}
            	fos.flush();
            	fos.close();
        	}
        	
		}catch(Exception ex){
			Utils.printException(ex);
		}
	}
	
	
	public static final SQLiteDatabase getDataBase(){
		SQLiteDatabase db=null;
		try {
			String dbFile = Environment.getExternalStorageDirectory().getPath()+"/zxtd_zase/citys.db";
	        File filePath = new File(dbFile);
	        if (!filePath.exists()){
	        	copyDataBase();
	        }
	        db=SQLiteDatabase.openOrCreateDatabase(filePath,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}
	
}
