package com.farmers.bean.BillPresentMents;

public class BillingAllocatedPaymentView {
	  private Payment payment;
	    public Payment getPayment() {
	        return payment;
	    }
	    public void setPayment(Payment payment) {
	        this.payment = payment;
	    }
		@Override
		public String toString() {
			return "BillingAllocatedPaymentView [payment=" + payment + ", getPayment()=" + getPayment()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
}
