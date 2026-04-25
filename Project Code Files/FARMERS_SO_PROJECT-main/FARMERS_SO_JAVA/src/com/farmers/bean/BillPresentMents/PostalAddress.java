package com.farmers.bean.BillPresentMents;

public class PostalAddress
{
	  private String postalCode;
	    public String getpostalCode() {
	        return postalCode;
	    }
	    public void setpostalCode(String postalCode) {
	        this.postalCode = postalCode;
	    }
		@Override
		public String toString() {
			return "PostalAddress [postalCode=" + postalCode + ", getPostalCode()=" + getpostalCode() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    

}
