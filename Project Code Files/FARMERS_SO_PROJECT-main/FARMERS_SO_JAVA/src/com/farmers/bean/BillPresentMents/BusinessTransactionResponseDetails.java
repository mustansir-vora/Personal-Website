package com.farmers.bean.BillPresentMents;

public class BusinessTransactionResponseDetails {
	 private String transactionStatus;
	    public String getTransactionStatus() {
	        return transactionStatus;
	    }
	    public void setTransactionStatus(String transactionStatus) {
	        this.transactionStatus = transactionStatus;
	    }
		@Override
		public String toString() {
			return "BusinessTransactionResponseDetails [transactionStatus=" + transactionStatus
					+ ", getTransactionStatus()=" + getTransactionStatus() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
}
