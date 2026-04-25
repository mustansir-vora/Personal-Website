package com.farmers.bean.AccountLookupLink;

public class AccountLookupRoot {
	 private ResponseBody responseBody;
	    private String aPIEndTime;
	    private RequestBody requestBody;
	    private String aPIStartTime;
	    private Integer responseCode;
	    private String responseMsg;
	    public ResponseBody getResponseBody() {
	        return responseBody;
	    }
	    public void setResponseBody(ResponseBody responseBody) {
	        this.responseBody = responseBody;
	    }
	    public String getAPIEndTime() {
	        return aPIEndTime;
	    }
	    public void setAPIEndTime(String aPIEndTime) {
	        this.aPIEndTime = aPIEndTime;
	    }
	    public RequestBody getRequestBody() {
	        return requestBody;
	    }
	    public void setRequestBody(RequestBody requestBody) {
	        this.requestBody = requestBody;
	    }
	    public String getAPIStartTime() {
	        return aPIStartTime;
	    }
	    public void setAPIStartTime(String aPIStartTime) {
	        this.aPIStartTime = aPIStartTime;
	    }
	    public Integer getResponseCode() {
	        return responseCode;
	    }
	    public void setResponseCode(Integer responseCode) {
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
					+ ", aPIStartTime=" + aPIStartTime + ", responseCode=" + responseCode + ", responseMsg="
					+ responseMsg + ", getResponseBody()=" + getResponseBody() + ", getAPIEndTime()=" + getAPIEndTime()
					+ ", getRequestBody()=" + getRequestBody() + ", getAPIStartTime()=" + getAPIStartTime()
					+ ", getResponseCode()=" + getResponseCode() + ", getResponseMsg()=" + getResponseMsg()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
	    
}
