package com.farmers.bean.BillPresentMents;

public class ResponseHeader {
	 private String systemName;
	    private String applicationType;
	    public String getSystemName() {
	        return systemName;
	    }
	    public void setSystemName(String systemName) {
	        this.systemName = systemName;
	    }
	    public String getApplicationType() {
	        return applicationType;
	    }
	    public void setApplicationType(String applicationType) {
	        this.applicationType = applicationType;
	    }
		@Override
		public String toString() {
			return "ResponseHeader [systemName=" + systemName + ", applicationType=" + applicationType
					+ ", getSystemName()=" + getSystemName() + ", getApplicationType()=" + getApplicationType()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
}
