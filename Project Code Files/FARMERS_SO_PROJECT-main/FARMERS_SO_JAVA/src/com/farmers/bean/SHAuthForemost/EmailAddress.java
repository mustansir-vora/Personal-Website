package com.farmers.bean.SHAuthForemost;

public class EmailAddress {

	
	 private String emailAddress;
	    public String getEmailAddress() {
	        return emailAddress;
	    }
	    public void setEmailAddress(String emailAddress) {
	        this.emailAddress = emailAddress;
	    }
		@Override
		public String toString() {
			return "EmailAddress [emailAddress=" + emailAddress + ", getEmailAddress()=" + getEmailAddress()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
}
