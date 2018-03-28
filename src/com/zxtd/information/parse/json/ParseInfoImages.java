package com.zxtd.information.parse.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zxtd.information.bean.ImageBean;

public class ParseInfoImages {
	private List<ImageBean> imageBeans;
	public void parse(String json){
		
		imageBeans = new ArrayList<ImageBean>();
		
		try {
			JSONArray jsonArray = new JSONArray(json);
			for(int i = 0; i < jsonArray.length(); i ++){
				JSONObject jsonImages = jsonArray.getJSONObject(i);
				ImageBean imageBean = new ImageBean();
				imageBean.imageUrl = jsonImages.getString("imageUrl");
				imageBean.describe = jsonImages.getString("describe");
				imageBeans.add(imageBean);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<ImageBean> getImageBeans(){
		return imageBeans;
	}
}
