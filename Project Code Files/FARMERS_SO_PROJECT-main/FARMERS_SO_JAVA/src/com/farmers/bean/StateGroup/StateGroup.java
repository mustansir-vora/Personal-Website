package com.farmers.bean.StateGroup;

import java.util.ArrayList;

public class StateGroup 
{
	 public boolean status;
	    public String message;
	    public ArrayList<Details> res;
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public ArrayList<Details> getRes() {
			return res;
		}

		public void setRes(ArrayList<Details> res) {
			this.res = res;
		}

		@Override
		public String toString() 
		{
			return "StateGroup [status=" + status + ", message=" + message + ", res=" + res + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
