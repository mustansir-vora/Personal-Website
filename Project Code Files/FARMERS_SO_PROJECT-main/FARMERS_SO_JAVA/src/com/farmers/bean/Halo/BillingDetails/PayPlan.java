package com.farmers.bean.Halo.BillingDetails;

public class PayPlan {
	 private String code;
	    private String desc;
	    private String payQty;
	    public String getCode() 
		{
	        return code;
	    }
	    public void setCode(String code) {
	        this.code = code;
	    }
	    public String getDesc() {
	        return desc;
	    }
	    public void setDesc(String desc) {
	        this.desc = desc;
	    }
	    public String getPayQty() {
	        return payQty;
	    }
	    public void setPayQty(String payQty) {
	        this.payQty = payQty;
	    }
		@Override
		public String toString() {
			return "PayPlan [code=" + code + ", desc=" + desc + ", payQty=" + payQty + ", getCode()=" + getCode()
					+ ", getDesc()=" + getDesc() + ", getPayQty()=" + getPayQty() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
}
