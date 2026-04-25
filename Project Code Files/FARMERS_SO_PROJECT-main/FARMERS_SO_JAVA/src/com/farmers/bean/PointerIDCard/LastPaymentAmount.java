package com.farmers.bean.PointerIDCard;

public class LastPaymentAmount {
	 private String theCurrencyAmount;
	    public String getTheCurrencyAmount()
	    {
	        return theCurrencyAmount;
	    }
	    public void setTheCurrencyAmount(String theCurrencyAmount) 
	    {
	        this.theCurrencyAmount = theCurrencyAmount;
	    }
		@Override
		public String toString() {
			return "LastPaymentAmount [theCurrencyAmount=" + theCurrencyAmount + ", getTheCurrencyAmount()="
					+ getTheCurrencyAmount() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
}
