package com.farmers.bean.PointerIDCard;

public class BWRoot {
	private BillingInfo billingInfo;
    public BillingInfo getBillingInfo() {
        return billingInfo;
    }
    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }
	@Override
	public String toString() {
		return "Root [billingInfo=" + billingInfo + ", getBillingInfo()=" + getBillingInfo() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
    
}
