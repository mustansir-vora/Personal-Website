package com.farmers.bean.Halo.BillingDetails;

import java.util.ArrayList;
import java.util.List;

public class HalloBillingDetails {
	private List<BillingDetail> billingDetails = new ArrayList<BillingDetail>();
    public List<BillingDetail> getBillingDetails() {
        return billingDetails;
    }
    public void setBillingDetails(List<BillingDetail> billingDetails) {
        this.billingDetails = billingDetails;
    }
	@Override
	public String toString() {
		return "Root [billingDetails=" + billingDetails + ", getBillingDetails()=" + getBillingDetails()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
}
