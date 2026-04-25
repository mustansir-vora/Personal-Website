package com.farmers.bean.BillPresentMents;

public class BillingPostedPaymentDueView {
	private PaymentDue paymentDue;
    public PaymentDue getPaymentDue() {
        return paymentDue;
    }
    public void setPaymentDue(PaymentDue paymentDue) {
        this.paymentDue = paymentDue;
    }
	@Override
	public String toString() {
		return "BillingPostedPaymentDueView [paymentDue=" + paymentDue + ", getPaymentDue()=" + getPaymentDue()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
}
