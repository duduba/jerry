package com.zxtd.information.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Province implements Parcelable {

	private int privId;
	private String provName;
	private int sort;
	
	public Province(){}
	
	public Province(Parcel p){
		privId=p.readInt();
		provName=p.readString();
		sort=p.readInt();
	}
	
	public int getPrivId() {
		return privId;
	}

	public void setPrivId(int privId) {
		this.privId = privId;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel p, int arg1) {
		// TODO Auto-generated method stub
		p.writeInt(privId);
		p.writeString(provName);
		p.writeInt(sort);
	}

	
	public static final Creator<Province> CREATOR = new Creator<Province>() {
		public Province[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Province[size];
		}
		
		public Province createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Province(source);
		}
	};
	
}
