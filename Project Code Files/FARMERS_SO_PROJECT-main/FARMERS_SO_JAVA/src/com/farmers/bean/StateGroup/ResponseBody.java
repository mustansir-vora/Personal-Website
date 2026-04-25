package com.farmers.bean.StateGroup;

import java.util.ArrayList;

public class ResponseBody {
	 public ArrayList<Re> res;
	    @Override
	public String toString() {
		return "ResponseBody [res=" + res + ", message=" + message + ", status=" + status + ", getRes()=" + getRes()
				+ ", getMessage()=" + getMessage() + ", isStatus()=" + isStatus() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
		public ArrayList<Re> getRes() {
		return res;
	}
	public void setRes(ArrayList<Re> res) {
		this.res = res;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
		public String message;
	    public boolean status;
	    
	    
	    
	    
}
