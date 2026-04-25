package com.farmers.bean.FWSBillingLookup.ARS;

public class Terms 
{
	private String paymentMethod;
	private String code;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
	@Override
	public String toString() {
		return "Terms [paymentMethod=" + paymentMethod + ", getCode()=" + getCode() + ", getPaymentMethod()="
				+ getPaymentMethod() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
}
