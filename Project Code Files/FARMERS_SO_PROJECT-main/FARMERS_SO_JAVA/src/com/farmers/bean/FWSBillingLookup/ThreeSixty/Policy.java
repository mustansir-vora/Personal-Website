package com.farmers.bean.FWSBillingLookup.ThreeSixty;

import java.util.ArrayList;
import java.util.List;

public class Policy {
	private String insuredName;
    private String state;
    private String cancelStatus;
    private String statusCode;
    private String nsfCounter;
    private String paymentLevelCode;
    private String producerDistSysCd;
    private List<Address> addresses = new ArrayList<Address>();
    public String getInsuredName() {
        return insuredName;
    }
    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCancelStatus() {
        return cancelStatus;
    }
    public void setCancelStatus(String cancelStatus) {
        this.cancelStatus = cancelStatus;
    }
    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public String getNsfCounter() {
        return nsfCounter;
    }
    public void setNsfCounter(String nsfCounter) {
        this.nsfCounter = nsfCounter;
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
		return "Policy [insuredName=" + insuredName + ", state=" + state + ", cancelStatus=" + cancelStatus
				+ ", statusCode=" + statusCode + ", nsfCounter=" + nsfCounter + ", paymentLevelCode=" + paymentLevelCode
				+ ", producerDistSysCd=" + producerDistSysCd + ", getInsuredName()=" + getInsuredName()
				+ ", getState()=" + getState() + ", getCancelStatus()=" + getCancelStatus() + ", getStatusCode()="
				+ getStatusCode() + ", getNsfCounter()=" + getNsfCounter() + ", getPaymentLevelCode()="
				+ getPaymentLevelCode() + ", getProducerDistSysCd()=" + getProducerDistSysCd() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
