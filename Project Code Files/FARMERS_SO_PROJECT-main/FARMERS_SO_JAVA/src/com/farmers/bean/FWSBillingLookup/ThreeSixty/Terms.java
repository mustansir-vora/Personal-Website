package com.farmers.bean.FWSBillingLookup.ThreeSixty;

public class Terms {
	  private String code;
	    private String paymentAllowed;
	    private String paymentMethod;
	    public String getCode() {
	        return code;
	    }
	    public void setCode(String code) {
	        this.code = code;
	    }
	    public String getPaymentAllowed() {
	        return paymentAllowed;
	    }
	    public void setPaymentAllowed(String paymentAllowed) {
	        this.paymentAllowed = paymentAllowed;
	    }
	    public String getPaymentMethod() {
	        return paymentMethod;
	    }
	    public void setPaymentMethod(String paymentMethod) {
	        this.paymentMethod = paymentMethod;
	    }
		@Override
		public String toString() {
			return "Terms [code=" + code + ", paymentAllowed=" + paymentAllowed + ", paymentMethod=" + paymentMethod
					+ ", getCode()=" + getCode() + ", getPaymentAllowed()=" + getPaymentAllowed()
					+ ", getPaymentMethod()=" + getPaymentMethod() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
}
