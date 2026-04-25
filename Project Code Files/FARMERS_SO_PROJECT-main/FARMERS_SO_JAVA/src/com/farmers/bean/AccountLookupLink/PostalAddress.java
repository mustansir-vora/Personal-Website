package com.farmers.bean.AccountLookupLink;

public class PostalAddress {
	 private String telephoneNumber;
	    private String address;
	    private String state;
	    private String postalCode;
	    private String city;
	    private String email;
	    public String getTelephoneNumber() {
	        return telephoneNumber;
	    }
	    public void setTelephoneNumber(String telephoneNumber) {
	        this.telephoneNumber = telephoneNumber;
	    }
	    public String getAddress() {
	        return address;
	    }
	    public void setAddress(String address) {
	        this.address = address;
	    }
	    public String getState() {
	        return state;
	    }
	    public void setState(String state) {
	        this.state = state;
	    }
	    public String getPostalCode() {
	        return postalCode;
	    }
	    public void setPostalCode(String postalCode) {
	        this.postalCode = postalCode;
	    }
	    public String getCity() {
	        return city;
	    }
	    public void setCity(String city) {
	        this.city = city;
	    }
	    public String getEmail() {
	        return email;
	    }
	    public void setEmail(String email) {
	        this.email = email;
	    }
		@Override
		public String toString() {
			return "PostalAddress [telephoneNumber=" + telephoneNumber + ", address=" + address + ", state=" + state
					+ ", postalCode=" + postalCode + ", city=" + city + ", email=" + email + ", getTelephoneNumber()="
					+ getTelephoneNumber() + ", getAddress()=" + getAddress() + ", getState()=" + getState()
					+ ", getPostalCode()=" + getPostalCode() + ", getCity()=" + getCity() + ", getEmail()=" + getEmail()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
	    
}
