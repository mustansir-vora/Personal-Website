package com.farmers.bean.FWSBillingLookup.ThreeSixty;

public class Due {
	private String type;
    private String amount;
    private String currency;
    private String activityDesc;
    private String date;
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
    public String getActivityDesc() {
        return activityDesc;
    }
    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
	@Override
	public String toString() {
		return "Due [type=" + type + ", amount=" + amount + ", currency=" + currency + ", activityDesc=" + activityDesc
				+ ", date=" + date + ", getType()=" + getType() + ", getAmount()=" + getAmount() + ", getCurrency()="
				+ getCurrency() + ", getActivityDesc()=" + getActivityDesc() + ", getDate()=" + getDate()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
}
