package com.farmers.bean.AccountLookupLink;

public class InsuredDetails {
	 private String firstName;
	    private String lastName;
	    private PostalAddress postalAddress;
	    public String getFirstName() {
	        return firstName;
	    }
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }
	    public String getLastName() {
	        return lastName;
	    }
	    public void setLastName(String lastName) {
	        this.lastName = lastName;
	    }
	    public PostalAddress getPostalAddress() {
	        return postalAddress;
	    }
	    public void setPostalAddress(PostalAddress postalAddress) {
	        this.postalAddress = postalAddress;
	    }
		@Override
		public String toString() {
			return "InsuredDetails [firstName=" + firstName + ", lastName=" + lastName + ", getFirstName()="
					+ getFirstName() + ", getLastName()=" + getLastName() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
