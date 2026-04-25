package com.farmers.bean.BillPresentMents;

public class NextFinancialTransactionCommunicationView {
	  private BillingPostedPaymentDueView billingPostedPaymentDueView;
	    private Communication communication;
	    public BillingPostedPaymentDueView getBillingPostedPaymentDueView() {
	        return billingPostedPaymentDueView;
	    }
	    public void setBillingPostedPaymentDueView(BillingPostedPaymentDueView billingPostedPaymentDueView) {
	        this.billingPostedPaymentDueView = billingPostedPaymentDueView;
	    }
	    public Communication getCommunication() {
	        return communication;
	    }
	    public void setCommunication(Communication communication) {
	        this.communication = communication;
	    }
		@Override
		public String toString() {
			return "NextFinancialTransactionCommunicationView [getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
