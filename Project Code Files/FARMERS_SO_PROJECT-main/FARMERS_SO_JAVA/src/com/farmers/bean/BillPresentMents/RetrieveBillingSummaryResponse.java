package com.farmers.bean.BillPresentMents;

public class RetrieveBillingSummaryResponse {
	  private ResponseHeader responseHeader;
	    private BillingSummary billingSummary;
	    private BusinessTransactionResponseDetails businessTransactionResponseDetails;
	    private String policyFoundIndicatorAsString;
	    private TransactionNotification transactionNotification;
	    public ResponseHeader getResponseHeader() {
	        return responseHeader;
	    }
	    public void setResponseHeader(ResponseHeader responseHeader) {
	        this.responseHeader = responseHeader;
	    }
	    public BillingSummary getBillingSummary() {
	        return billingSummary;
	    }
	    public void setBillingSummary(BillingSummary billingSummary) {
	        this.billingSummary = billingSummary;
	    }
	    public BusinessTransactionResponseDetails getBusinessTransactionResponseDetails() {
	        return businessTransactionResponseDetails;
	    }
	    public void setBusinessTransactionResponseDetails(BusinessTransactionResponseDetails businessTransactionResponseDetails) {
	        this.businessTransactionResponseDetails = businessTransactionResponseDetails;
	    }
	    public String getPolicyFoundIndicatorAsString() {
	        return policyFoundIndicatorAsString;
	    }
	    public void setPolicyFoundIndicatorAsString(String policyFoundIndicatorAsString) {
	        this.policyFoundIndicatorAsString = policyFoundIndicatorAsString;
	    }
	    public TransactionNotification getTransactionNotification() {
	        return transactionNotification;
	    }
	    public void setTransactionNotification(TransactionNotification transactionNotification) {
	        this.transactionNotification = transactionNotification;
	    }
		@Override
		public String toString() {
			return "RetrieveBillingSummaryResponse [policyFoundIndicatorAsString=" + policyFoundIndicatorAsString
					+ ", transactionNotification=" + transactionNotification + ", getPolicyFoundIndicatorAsString()="
					+ getPolicyFoundIndicatorAsString() + ", getTransactionNotification()="
					+ getTransactionNotification() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
	    
}
