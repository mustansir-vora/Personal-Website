package com.farmers.bean.Halo.BillingDetails;

import java.util.ArrayList;
import java.util.List;

public class Policy {
	private String policyNumber;
    private String productCode;
    private String actorType;
    private String policySystemCode;
    private String policySystemDesc;
    private String productDesc;
    private String policyStatusCode;
    private String policyStatusDesc;
    private String policyStatusMessage;
    private List<Preference> preferences = new ArrayList<Preference>();
    private List<Payment> payments = new ArrayList<Payment>();
    private List<Term> terms = new ArrayList<Term>();
    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public String getActorType() {
        return actorType;
    }
    public void setActorType(String actorType) {
        this.actorType = actorType;
    }
    public String getPolicySystemCode() {
        return policySystemCode;
    }
    public void setPolicySystemCode(String policySystemCode) {
        this.policySystemCode = policySystemCode;
    }
    public String getPolicySystemDesc() {
        return policySystemDesc;
    }
    public void setPolicySystemDesc(String policySystemDesc) {
        this.policySystemDesc = policySystemDesc;
    }
    public String getProductDesc() {
        return productDesc;
    }
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
    public String getPolicyStatusCode() {
        return policyStatusCode;
    }
    public void setPolicyStatusCode(String policyStatusCode) {
        this.policyStatusCode = policyStatusCode;
    }
    public String getPolicyStatusDesc() {
        return policyStatusDesc;
    }
    public void setPolicyStatusDesc(String policyStatusDesc) {
        this.policyStatusDesc = policyStatusDesc;
    }
    public String getPolicyStatusMessage() {
        return policyStatusMessage;
    }
    public void setPolicyStatusMessage(String policyStatusMessage) {
        this.policyStatusMessage = policyStatusMessage;
    }
    public List<Preference> getPreferences() {
        return preferences;
    }
    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }
    public List<Payment> getPayments() {
        return payments;
    }
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    public List<Term> getTerms() {
        return terms;
    }
    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }
	@Override
	public String toString() {
		return "Policy [policyNumber=" + policyNumber + ", productCode=" + productCode + ", actorType=" + actorType
				+ ", policySystemCode=" + policySystemCode + ", policySystemDesc=" + policySystemDesc + ", productDesc="
				+ productDesc + ", policyStatusCode=" + policyStatusCode + ", policyStatusDesc=" + policyStatusDesc
				+ ", policyStatusMessage=" + policyStatusMessage + ", getPolicyNumber()=" + getPolicyNumber()
				+ ", getProductCode()=" + getProductCode() + ", getActorType()=" + getActorType()
				+ ", getPolicySystemCode()=" + getPolicySystemCode() + ", getPolicySystemDesc()="
				+ getPolicySystemDesc() + ", getProductDesc()=" + getProductDesc() + ", getPolicyStatusCode()="
				+ getPolicyStatusCode() + ", getPolicyStatusDesc()=" + getPolicyStatusDesc()
				+ ", getPolicyStatusMessage()=" + getPolicyStatusMessage() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
