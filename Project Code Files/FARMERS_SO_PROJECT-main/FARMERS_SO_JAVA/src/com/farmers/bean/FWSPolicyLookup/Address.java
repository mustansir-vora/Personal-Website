package com.farmers.bean.FWSPolicyLookup;

public class Address {
	 private String zip;
	    public String getZip() {
	        return zip;
	    }
	    public void setZip(String zip) {
	        this.zip = zip;
	    }
		@Override
		public String toString() {
			return "Address [zip=" + zip + ", getZip()=" + getZip() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
