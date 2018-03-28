package com.zxtd.information.manager;

import java.util.ArrayList;
import java.util.List;

import com.zxtd.information.bean.Bean;
import com.zxtd.information.bean.NewTypeBean;

public class NewTypeBeansManager {
	private static NewTypeBeansManager newTypeBeansManager = null;
	private List<Bean> mNewTypeBeans = null;
	private List<String> mNewTypeNames = null;

	private NewTypeBeansManager() {
		mNewTypeBeans = new ArrayList<Bean>();
		mNewTypeNames = new ArrayList<String>();
		
	}

	private void initNewTypeBeans(List<Bean> newTypeBeans) {
		
		mNewTypeBeans.clear();
		if(newTypeBeans != null){
			mNewTypeBeans.addAll(newTypeBeans);
		}
		
	}

	public static void initNewTypeBeansMananger(List<Bean> newTypeBeans) {
		if(newTypeBeansManager == null){
			getNewInstance();
		}
		newTypeBeansManager.initNewTypeBeans(newTypeBeans );
	}

	public static NewTypeBeansManager getNewInstance() {
		if (newTypeBeansManager == null) {
			newTypeBeansManager = new NewTypeBeansManager();
		}
		return newTypeBeansManager;
	}

	public List<Bean> getNewTypeBeans() {

		return mNewTypeBeans;
	}

	public List<String> getNewTypeNames() {
		mNewTypeNames.clear();
		for (Bean newTypeBean : mNewTypeBeans) {
			mNewTypeNames.add(((NewTypeBean)newTypeBean).name);
		}
		return mNewTypeNames;
	}
	
	public int getNewTypeIndex(NewTypeBean newTypeBean){
		for (int i = 0; i < mNewTypeBeans.size(); i++) {
			NewTypeBean tem = (NewTypeBean)mNewTypeBeans.get(i);
			if(tem.id.equals(newTypeBean.id)){
				return i;
			}
		}
		return -1;
	}
	
	public String getTypeNameById(String id){
		String name = "";
		for (Bean newTypeBean : mNewTypeBeans) {
			NewTypeBean typeBean = (NewTypeBean) newTypeBean;
			if(id != null && id.equals(typeBean.id)){
				name =  typeBean.name;
				break;
			}
		}
		return name;
	}
}
