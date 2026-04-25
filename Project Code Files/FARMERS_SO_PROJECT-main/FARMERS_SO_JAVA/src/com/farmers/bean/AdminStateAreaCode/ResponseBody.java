package com.farmers.bean.AdminStateAreaCode;

import java.util.ArrayList;
import java.util.List;

public class ResponseBody {
	 private Boolean status;
	    private String message;
	    private List<Re> res = new ArrayList<Re>();
	    public Boolean getStatus() {
	        return status;
	    }
	    public void setStatus(Boolean status) {
	        this.status = status;
	    }
	    public String getMessage() {
	        return message;
	    }
	    public void setMessage(String message) {
	        this.message = message;
	    }
	    public List<Re> getRes() {
	        return res;
	    }
	    public void setRes(List<Re> res) {
	        this.res = res;
	    }
		@Override
		public String toString() {
			return "ResponseBody [status=" + status + ", message=" + message + ", res=" + res + ", getStatus()="
					+ getStatus() + ", getMessage()=" + getMessage() + ", getRes()=" + getRes() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
}
