package com.farmers.bean.SHAuthBWPHNOSearch;

public class HomePolicy {
	private String insurerCompanyCode;
    private String lineOfBusiness;
    private String policyModNumber;
    private String policyNumber;
    private String policySymbol;
    private AgentDetails_ agentDetails;
    private NamedPerson namedPerson;
    private PropertyAddress propertyAddress;
    public String getInsurerCompanyCode() {
        return insurerCompanyCode;
    }
    public void setInsurerCompanyCode(String insurerCompanyCode) {
        this.insurerCompanyCode = insurerCompanyCode;
    }
    public String getLineOfBusiness() {
        return lineOfBusiness;
    }
    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }
    public String getPolicyModNumber() {
        return policyModNumber;
    }
    public void setPolicyModNumber(String policyModNumber) {
        this.policyModNumber = policyModNumber;
    }
    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    public String getPolicySymbol() {
        return policySymbol;
    }
    public void setPolicySymbol(String policySymbol) {
        this.policySymbol = policySymbol;
    }
    public AgentDetails_ getAgentDetails() {
        return agentDetails;
    }
    public void setAgentDetails(AgentDetails_ agentDetails) {
        this.agentDetails = agentDetails;
    }
    public NamedPerson getNamedPerson() {
        return namedPerson;
    }
    public void setNamedPerson(NamedPerson namedPerson) {
        this.namedPerson = namedPerson;
    }
    public PropertyAddress getPropertyAddress() {
        return propertyAddress;
    }
    public void setPropertyAddress(PropertyAddress propertyAddress) {
        this.propertyAddress = propertyAddress;
    }
	@Override
	public String toString() {
		return "HomePolicy [insurerCompanyCode=" + insurerCompanyCode + ", lineOfBusiness=" + lineOfBusiness
				+ ", policyModNumber=" + policyModNumber + ", policyNumber=" + policyNumber + ", policySymbol="
				+ policySymbol + ", agentDetails=" + agentDetails + ", getInsurerCompanyCode()="
				+ getInsurerCompanyCode() + ", getLineOfBusiness()=" + getLineOfBusiness() + ", getPolicyModNumber()="
				+ getPolicyModNumber() + ", getPolicyNumber()=" + getPolicyNumber() + ", getPolicySymbol()="
				+ getPolicySymbol() + ", getAgentDetails()=" + getAgentDetails() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
    
}
