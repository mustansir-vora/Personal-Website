package com.farmers.bean.PointerIDCard;

public class NotSufficientFundAmount {
	 private Object theCurrencyAmount;
	    public Object getTheCurrencyAmount() {
	        return theCurrencyAmount;
	    }
	    public void setTheCurrencyAmount(Object theCurrencyAmount) {
	        this.theCurrencyAmount = theCurrencyAmount;
	    }
		@Override
		public String toString() {
			return "NotSufficientFundAmount [theCurrencyAmount=" + theCurrencyAmount + ", getTheCurrencyAmount()="
					+ getTheCurrencyAmount() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
}
