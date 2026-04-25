package com.farmers.bean.BillPresentMents;

public class CancelNoticeFinTransCommunicationView {
	 private BillingPostedPaymentDueView__ billingPostedPaymentDueView;
	    private Communication__ communication;
	    public BillingPostedPaymentDueView__ getBillingPostedPaymentDueView() {
	        return billingPostedPaymentDueView;
	    }
	    public void setBillingPostedPaymentDueView(BillingPostedPaymentDueView__ billingPostedPaymentDueView) {
	        this.billingPostedPaymentDueView = billingPostedPaymentDueView;
	    }
	    public Communication__ getCommunication() {
	        return communication;
	    }
	    public void setCommunication(Communication__ communication) {
	        this.communication = communication;
	    }
		@Override
		public String toString() {
			return "CancelNoticeFinTransCommunicationView [communication=" + communication + ", getCommunication()="
					+ getCommunication() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	    
	    
	    
}
