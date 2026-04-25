package com.farmers.bean.FWSBillingLookup.PointBilling;

public class Due {
	 private String type;
	    private String date;
	    private String amount;
	    private String currency;
	    public String getType() {
	        return type;
	    }
	    public void setType(String type) {
	        this.type = type;
	    }
	    public String getDate() {
	        return date;
	    }
	    public void setDate(String date) {
	        this.date = date;
	    }
	    public String getAmount() {
	        return amount;
	    }
	    public void setAmount(String amount) {
	        this.amount = amount;
	    }
	    public String getCurrency() {
	        return currency;
	    }
	    public void setCurrency(String currency) {
	        this.currency = currency;
	    }
		@Override
		public String toString() {
			return "Due [type=" + type + ", date=" + date + ", amount=" + amount + ", currency=" + currency
					+ ", getType()=" + getType() + ", getDate()=" + getDate() + ", getAmount()=" + getAmount()
					+ ", getCurrency()=" + getCurrency() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
}
