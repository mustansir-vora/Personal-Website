package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class InsuredVehicle {
	 public GaragingAddress garagingAddress;

	public GaragingAddress getGaragingAddress() {
		return garagingAddress;
	}

	public void setGaragingAddress(GaragingAddress garagingAddress) {
		this.garagingAddress = garagingAddress;
	}

	@Override
	public String toString() {
		return "InsuredVehicle [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	 
	 
}
