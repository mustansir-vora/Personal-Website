package com.farmers.bean.FWSBillingLookup.ARS;

public class Due {
	private String type;
    private String amount;
    private String currency;
    private String date;
    private String activityDesc;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getActivityDesc() {
        return activityDesc;
    }
    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }
	@Override
	public String toString() {
		return "Due [type=" + type + ", amount=" + amount + ", currency=" + currency + ", date=" + date
				+ ", activityDesc=" + activityDesc + ", getType()=" + getType() + ", getAmount()=" + getAmount()
				+ ", getCurrency()=" + getCurrency() + ", getDate()=" + getDate() + ", getActivityDesc()="
				+ getActivityDesc() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
