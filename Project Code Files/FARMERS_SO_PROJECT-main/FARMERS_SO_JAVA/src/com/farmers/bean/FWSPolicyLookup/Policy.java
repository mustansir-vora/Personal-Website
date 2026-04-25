package com.farmers.bean.FWSPolicyLookup;

import java.util.ArrayList;
import java.util.List;

public class Policy {
	private String status;
    private String producerRoleCode;
    private String policySource;
    private String policyNumber;
    private String internalPolicyNumber;
    private String internalPolicyVersion;
    private String billingAccountNumber;
    private String effectiveDate;
    private String expirationDate;
    private String renewalEffectiveDate;
    private String suffix;
    private String policyState;
    private String policyStatus;
    private String lineOfBusiness;
    private String serviceLevels;
    private List<InsuredDetail> insuredDetails = new ArrayList<InsuredDetail>();
    private String producerSubCode;
    private List<Address> addresses = new ArrayList<Address>();
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getProducerRoleCode() {
        return producerRoleCode;
    }
    public void setProducerRoleCode(String producerRoleCode) {
        this.producerRoleCode = producerRoleCode;
    }
    public String getPolicySource() {
        return policySource;
    }
    public void setPolicySource(String policySource) {
        this.policySource = policySource;
    }
    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    public String getInternalPolicyNumber() {
        return internalPolicyNumber;
    }
    public void setInternalPolicyNumber(String internalPolicyNumber) {
        this.internalPolicyNumber = internalPolicyNumber;
    }
    public String getInternalPolicyVersion() {
        return internalPolicyVersion;
    }
    public void setInternalPolicyVersion(String internalPolicyVersion) {
        this.internalPolicyVersion = internalPolicyVersion;
    }
    public String getBillingAccountNumber() {
        return billingAccountNumber;
    }
    public void setBillingAccountNumber(String billingAccountNumber) {
        this.billingAccountNumber = billingAccountNumber;
    }
    public String getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getRenewalEffectiveDate() {
        return renewalEffectiveDate;
    }
    public void setRenewalEffectiveDate(String renewalEffectiveDate) {
        this.renewalEffectiveDate = renewalEffectiveDate;
    }
    public String getSuffix() {
        return suffix;
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    public String getPolicyState() {
        return policyState;
    }
    public void setPolicyState(String policyState) {
        this.policyState = policyState;
    }
    public String getPolicyStatus() {
        return policyStatus;
    }
    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }
    public String getLineOfBusiness() {
        return lineOfBusiness;
    }
    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }
    public String getServiceLevels() {
        return serviceLevels;
    }
    public void setServiceLevels(String serviceLevels) {
        this.serviceLevels = serviceLevels;
    }
    public List<InsuredDetail> getInsuredDetails() {
        return insuredDetails;
    }
    public void setInsuredDetails(List<InsuredDetail> insuredDetails) {
        this.insuredDetails = insuredDetails;
    }
    public String getProducerSubCode() {
        return producerSubCode;
    }
    public void setProducerSubCode(String producerSubCode) {
        this.producerSubCode = producerSubCode;
    }
    public List<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
