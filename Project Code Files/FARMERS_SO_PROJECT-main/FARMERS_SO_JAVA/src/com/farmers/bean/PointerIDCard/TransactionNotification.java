package com.farmers.bean.PointerIDCard;

public class TransactionNotification {
	 private String transactionStatus;
	    public String getTransactionStatus() {
	        return transactionStatus;
	    }
	    public void setTransactionStatus(String transactionStatus) {
	        this.transactionStatus = transactionStatus;
	    }
		@Override
		public String toString() {
			return "TransactionNotification [transactionStatus=" + transactionStatus + ", getTransactionStatus()="
					+ getTransactionStatus() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
