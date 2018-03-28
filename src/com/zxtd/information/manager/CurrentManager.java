package com.zxtd.information.manager;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.DownloadNewsListBean;
import com.zxtd.information.bean.NewBean;

public class CurrentManager {
	private static CurrentManager manager;
	private Bean mBean;
	private CurrentManager(){
		
	}
	
	public static CurrentManager newInstance(){
		if(manager == null){
			manager = new CurrentManager();
		}
		return manager;
	}
	
	public synchronized void setCurrentBean(Bean bean){
		mBean = bean;
	}
	
	public synchronized String getCurrentNewId(){
		if(mBean == null){
			return null;
		}
		if(mBean instanceof NewBean){
			return ((NewBean) mBean).newId;
		}else if(mBean instanceof DownloadNewsListBean){
			return ((DownloadNewsListBean) mBean).id;
		}
		return null;
	}
	
	public synchronized void setPostCount(String postCount){
		if(mBean == null){
			return;
		}
		if(mBean instanceof NewBean){
			((NewBean) mBean).postCount = postCount;
		}else if(mBean instanceof DownloadNewsListBean){
			((DownloadNewsListBean) mBean).postcount = postCount;
		}
	}
	
	public synchronized String getInfoUrl(){
		if(mBean == null){
			return null;
		}
		if(mBean instanceof NewBean){
			return ((NewBean) mBean).infoUrl;
		}else if(mBean instanceof DownloadNewsListBean){
			return ((DownloadNewsListBean) mBean).infoUrl;
		}
		return null;
	}
	
	public synchronized String getPostCount(){
		if(mBean == null){
			return null;
		}
		if(mBean instanceof NewBean){
			return ((NewBean) mBean).postCount;
		}else if(mBean instanceof DownloadNewsListBean){
			return ((DownloadNewsListBean) mBean).postcount;
		}
		return null;
	}
	
	public synchronized Bean getBean(){
		return mBean;
	}
}
