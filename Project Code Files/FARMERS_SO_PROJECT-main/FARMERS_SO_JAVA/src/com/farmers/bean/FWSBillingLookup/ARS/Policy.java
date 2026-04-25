package com.farmers.bean.FWSBillingLookup.ARS;

import java.util.ArrayList;
import java.util.List;

public class Policy {
	private String state;
    private String statusCode;
    private String effectiveDate;
    private String paymentLevelCode;
    private String producerDistSysCd;
    private List<Address> addresses = new ArrayList<Address>();
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public String getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public String getPaymentLevelCode() {
        return paymentLevelCode;
    }
    public void setPaymentLevelCode(String paymentLevelCode) {
        this.paymentLevelCode = paymentLevelCode;
    }
    public String getProducerDistSysCd() {
        return producerDistSysCd;
    }
    public void setProducerDistSysCd(String producerDistSysCd) {
        this.producerDistSysCd = producerDistSysCd;
    }
    public List<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
	@Override
	public String toString() {
		return "Policy [state=" + state + ", statusCode=" + statusCode + ", effectiveDate=" + effectiveDate
				+ ", paymentLevelCode=" + paymentLevelCode + ", producerDistSysCd=" + producerDistSysCd
				+ ", getState()=" + getState() + ", getStatusCode()=" + getStatusCode() + ", getEffectiveDate()="
				+ getEffectiveDate() + ", getPaymentLevelCode()=" + getPaymentLevelCode() + ", getProducerDistSysCd()="
				+ getProducerDistSysCd() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
