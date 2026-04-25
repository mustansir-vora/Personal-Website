package com.farmers.bean.BillPresentMents;

public class RefundPaymentView {
	private Payment_ payment;
    public Payment_ getPayment() {
        return payment;
    }
    public void setPayment(Payment_ payment) {
        this.payment = payment;
    }
	@Override
	public String toString() {
		return "RefundPaymentView [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
    
    
}
