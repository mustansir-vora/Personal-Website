package com.farmers.bean.PolicyInqReteval;

public class GaragingAddress {
	private String postalCode;
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
	@Override
	public String toString() {
		return "GaragingAddress [postalCode=" + postalCode + ", getPostalCode()=" + getPostalCode() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
}
