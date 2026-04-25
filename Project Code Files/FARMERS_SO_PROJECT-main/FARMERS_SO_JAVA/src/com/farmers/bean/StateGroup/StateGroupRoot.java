package com.farmers.bean.StateGroup;



public class StateGroupRoot {
	public ResponseBody responseBody;
    public String aPIEndTime;
    public RequestBody requestBody; 
    public String aPIStartTime;
    public int responseCode;
    public String responseMsg;
	public ResponseBody getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(ResponseBody responseBody) {
		this.responseBody = responseBody;
	}
	public String getaPIEndTime() {
		return aPIEndTime;
	}
	public void setaPIEndTime(String aPIEndTime) {
		this.aPIEndTime = aPIEndTime;
	}
	public RequestBody getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
	}
	public String getaPIStartTime() {
		return aPIStartTime;
	}
	public void setaPIStartTime(String aPIStartTime) {
		this.aPIStartTime = aPIStartTime;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	@Override
	public String toString() {
		return "Root [responseBody=" + responseBody + ", aPIEndTime=" + aPIEndTime + ", requestBody=" + requestBody
				+ ", aPIStartTime=" + aPIStartTime + ", responseCode=" + responseCode + ", responseMsg=" + responseMsg
				+ ", getResponseBody()=" + getResponseBody() + ", getaPIEndTime()=" + getaPIEndTime()
				+ ", getRequestBody()=" + getRequestBody() + ", getaPIStartTime()=" + getaPIStartTime()
				+ ", getResponseCode()=" + getResponseCode() + ", getResponseMsg()=" + getResponseMsg()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
    
    
    
}
