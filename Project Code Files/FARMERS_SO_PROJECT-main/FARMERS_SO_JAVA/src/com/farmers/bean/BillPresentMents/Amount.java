package com.farmers.bean.BillPresentMents;

public class Amount {
	 private String theCurrencyAmount;
	    public String getTheCurrencyAmount() {
	        return theCurrencyAmount;
	    }
	    public void setTheCurrencyAmount(String theCurrencyAmount) {
	        this.theCurrencyAmount = theCurrencyAmount;
	    }
		@Override
		public String toString() {
			return "Amount [theCurrencyAmount=" + theCurrencyAmount + ", getTheCurrencyAmount()="
					+ getTheCurrencyAmount() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
