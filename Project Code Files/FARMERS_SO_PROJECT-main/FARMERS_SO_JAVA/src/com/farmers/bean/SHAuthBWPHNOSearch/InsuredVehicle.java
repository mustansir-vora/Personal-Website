package com.farmers.bean.SHAuthBWPHNOSearch;

public class InsuredVehicle {
	  private GaragingAddress garagingAddress;
	    public GaragingAddress getGaragingAddress() {
	        return garagingAddress;
	    }
	    public void setGaragingAddress(GaragingAddress garagingAddress) {
	        this.garagingAddress = garagingAddress;
	    }
		@Override
		public String toString() {
			return "InsuredVehicle [garagingAddress=" + garagingAddress + ", getGaragingAddress()="
					+ getGaragingAddress() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
