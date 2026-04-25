package com.farmers.bean.Agent.POST;

public class AgentPost_Root {
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
		public String getaPIEndTime() {
			return aPIEndTime;
		}
		public void setaPIEndTime(String aPIEndTime) {
			this.aPIEndTime = aPIEndTime;
		}
		public String getaPIStartTime() {
			return aPIStartTime;
		}
		public void setaPIStartTime(String aPIStartTime) {
			this.aPIStartTime = aPIStartTime;
		}
		@Override
		public String toString() {
			return "Root [responseBody=" + responseBody + ", aPIEndTime=" + aPIEndTime + ", requestBody=" + requestBody
					+ ", aPIStartTime=" + aPIStartTime + ", responseCode=" + responseCode + ", responseMsg="
					+ responseMsg + ", getResponseBody()=" + getResponseBody() + ", getAPIEndTime()=" + getAPIEndTime()
					+ ", getRequestBody()=" + getRequestBody() + ", getAPIStartTime()=" + getAPIStartTime()
					+ ", getResponseCode()=" + getResponseCode() + ", getResponseMsg()=" + getResponseMsg()
					+ ", getaPIEndTime()=" + getaPIEndTime() + ", getaPIStartTime()=" + getaPIStartTime()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
	    
	    
}
