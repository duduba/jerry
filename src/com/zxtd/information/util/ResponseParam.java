package com.zxtd.information.util;

public class ResponseParam {

	private String status;
	private String failureReason;
	private int returnId;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	public boolean isSuccess(){
		return "1".equals(status) ? true : false;
	}
	public int getReturnId() {
		return returnId;
	}
	public void setReturnId(int returnId) {
		this.returnId = returnId;
	}
	
	
	
}
