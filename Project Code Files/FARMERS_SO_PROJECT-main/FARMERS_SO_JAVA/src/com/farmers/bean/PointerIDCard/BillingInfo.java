package com.farmers.bean.PointerIDCard;

public class BillingInfo {
	private String caseType;
    private BillingDetails billingDetails;
    private InsuredDetails insuredDetails;
    private PolicyDetails policyDetails;
    private RenewalPolicyBillingDetails renewalPolicyBillingDetails;
    private RenewalPolicyDetails renewalPolicyDetails;
    private TransactionNotification transactionNotification;
    public String getCaseType() {
        return caseType;
    }
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    public BillingDetails getBillingDetails() {
        return billingDetails;
    }
    public void setBillingDetails(BillingDetails billingDetails) {
        this.billingDetails = billingDetails;
    }
    public InsuredDetails getInsuredDetails() {
        return insuredDetails;
    }
    public void setInsuredDetails(InsuredDetails insuredDetails) {
        this.insuredDetails = insuredDetails;
    }
    public PolicyDetails getPolicyDetails() {
        return policyDetails;
    }
    public void setPolicyDetails(PolicyDetails policyDetails) {
        this.policyDetails = policyDetails;
    }
    public RenewalPolicyBillingDetails getRenewalPolicyBillingDetails() {
        return renewalPolicyBillingDetails;
    }
    public void setRenewalPolicyBillingDetails(RenewalPolicyBillingDetails renewalPolicyBillingDetails) {
        this.renewalPolicyBillingDetails = renewalPolicyBillingDetails;
    }
    public RenewalPolicyDetails getRenewalPolicyDetails() {
        return renewalPolicyDetails;
    }

    public void setRenewalPolicyDetails(RenewalPolicyDetails renewalPolicyDetails) {
        this.renewalPolicyDetails = renewalPolicyDetails;
    }
    public TransactionNotification getTransactionNotification() {
        return transactionNotification;
    }
    public void setTransactionNotification(TransactionNotification transactionNotification) {
        this.transactionNotification = transactionNotification;
    }
	@Override
	public String toString() {
		return "BillingInfo [caseType=" + caseType + ", billingDetails=" + billingDetails + ", getCaseType()="
				+ getCaseType() + ", getBillingDetails()=" + getBillingDetails() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
    
}
