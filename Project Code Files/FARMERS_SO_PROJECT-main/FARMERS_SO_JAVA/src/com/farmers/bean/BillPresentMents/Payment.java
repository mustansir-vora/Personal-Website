package com.farmers.bean.BillPresentMents;

public class Payment {
	 private Amount amount;
	    private String paymentDate;
	    private String receivedDate;
	    public String getReceivedDate() {
			return receivedDate;
		}
		public void setReceivedDate(String receivedDate) {
			this.receivedDate = receivedDate;
		}
		public Amount getAmount() {
	        return amount;
	    }
	    public void setAmount(Amount amount) {
	        this.amount = amount;
	    }
	    public String getPaymentDate() {
	        return paymentDate;
	    }
	    public void setPaymentDate(String paymentDate) {
	        this.paymentDate = paymentDate;
	    }
		@Override
		public String toString() {
			return "Payment [amount=" + amount + ", paymentDate=" + paymentDate + ", receivedDate=" + receivedDate
					+ ", getReceivedDate()=" + getReceivedDate() + ", getAmount()=" + getAmount()
					+ ", getPaymentDate()=" + getPaymentDate() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    

}
