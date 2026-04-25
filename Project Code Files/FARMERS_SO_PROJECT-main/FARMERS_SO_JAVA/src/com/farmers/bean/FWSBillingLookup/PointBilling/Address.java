package com.farmers.bean.FWSBillingLookup.PointBilling;

public class Address {
	 private String street;
	    private String zip;
	    private String city;
	    private String state;
	    public String getStreet() {
	        return street;
	    }
	    public void setStreet(String street) {
	        this.street = street;
	    }
	    public String getZip() {
	        return zip;
	    }
	    public void setZip(String zip) {
	        this.zip = zip;
	    }
	    public String getCity() {
	        return city;
	    }
	    public void setCity(String city) {
	        this.city = city;
	    }
	    public String getState() {
	        return state;
	    }
	    public void setState(String state) {
	        this.state = state;
	    }
		@Override
		public String toString() {
			return "Address [street=" + street + ", zip=" + zip + ", city=" + city + ", state=" + state
					+ ", getStreet()=" + getStreet() + ", getZip()=" + getZip() + ", getCity()=" + getCity()
					+ ", getState()=" + getState() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
}
