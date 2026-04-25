package com.farmers.bean.AccountLookupLink;

public class Policy {
	private String lineOfBusiness;
    private String policyType;
    private String policyCategory;
    private String policyStateCode;
    private String policySource;
    private String policyNumber;
    private InsuredDetails insuredDetails;
    public String getLineOfBusiness() {
        return lineOfBusiness;
    }
    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }
    public String getPolicyType() {
        return policyType;
    }
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
    public String getPolicyCategory() {
        return policyCategory;
    }
    public void setPolicyCategory(String policyCategory) {
        this.policyCategory = policyCategory;
    }
    public String getPolicyStateCode() {
        return policyStateCode;
    }
    public void setPolicyStateCode(String policyStateCode) {
        this.policyStateCode = policyStateCode;
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
    public InsuredDetails getInsuredDetails() {
        return insuredDetails;
    }
    public void setInsuredDetails(InsuredDetails insuredDetails) {
        this.insuredDetails = insuredDetails;
    }
	@Override
	public String toString() {
		return "Policy [lineOfBusiness=" + lineOfBusiness + ", policyType=" + policyType + ", policyCategory="
				+ policyCategory + ", policyStateCode=" + policyStateCode + ", policySource=" + policySource
				+ ", policyNumber=" + policyNumber + ", insuredDetails=" + insuredDetails + ", getLineOfBusiness()="
				+ getLineOfBusiness() + ", getPolicyType()=" + getPolicyType() + ", getPolicyCategory()="
				+ getPolicyCategory() + ", getPolicyStateCode()=" + getPolicyStateCode() + ", getPolicySource()="
				+ getPolicySource() + ", getPolicyNumber()=" + getPolicyNumber() + ", getInsuredDetails()="
				+ getInsuredDetails() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
    
    
    
    
}
