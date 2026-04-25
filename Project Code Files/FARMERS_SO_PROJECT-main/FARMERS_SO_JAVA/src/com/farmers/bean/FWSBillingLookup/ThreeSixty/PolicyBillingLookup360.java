package com.farmers.bean.FWSBillingLookup.ThreeSixty;

public class PolicyBillingLookup360 {
	 private BillingSummary billingSummary;
	    public BillingSummary getBillingSummary() {
	        return billingSummary;
	    }
	    public void setBillingSummary(BillingSummary billingSummary) {
	        this.billingSummary = billingSummary;
	    }
		@Override
		public String toString() {
			return "Root [billingSummary=" + billingSummary + ", getBillingSummary()=" + getBillingSummary()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
}
