package com.farmers.bean.BillPresentMents;

public class PreviousFinancialTransactionCommunicationView {
	  private BillingPostedPaymentDueView_ billingPostedPaymentDueView;
	    private Communication_ communication;
	    public BillingPostedPaymentDueView_ getBillingPostedPaymentDueView() {
	        return billingPostedPaymentDueView;
	    }
	    public void setBillingPostedPaymentDueView(BillingPostedPaymentDueView_ billingPostedPaymentDueView) {
	        this.billingPostedPaymentDueView = billingPostedPaymentDueView;
	    }
	    public Communication_ getCommunication() {
	        return communication;
	    }
	    public void setCommunication(Communication_ communication) {
	        this.communication = communication;
	    }
		@Override
		public String toString() {
			return "PreviousFinancialTransactionCommunicationView [getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
}
