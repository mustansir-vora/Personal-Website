package com.farmers.bean.Agent.POST;

public class AgentSearchCriterium {
	 private String businessUnit;
	    private String phoneNumber;
	    public String getBusinessUnit() {
	        return businessUnit;
	    }
	    public void setBusinessUnit(String businessUnit) {
	        this.businessUnit = businessUnit;
	    }
	    public String getPhoneNumber() {
	        return phoneNumber;
	    }
	    public void setPhoneNumber(String phoneNumber) {
	        this.phoneNumber = phoneNumber;
	    }
		@Override
		public String toString() {
			return "AgentSearchCriterium [businessUnit=" + businessUnit + ", phoneNumber=" + phoneNumber
					+ ", getBusinessUnit()=" + getBusinessUnit() + ", getPhoneNumber()=" + getPhoneNumber()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
}
