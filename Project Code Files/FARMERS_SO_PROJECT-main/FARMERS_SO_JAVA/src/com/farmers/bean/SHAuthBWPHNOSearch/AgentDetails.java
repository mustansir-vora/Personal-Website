package com.farmers.bean.SHAuthBWPHNOSearch;

public class AgentDetails {

	
	
	private String agentCode;
    private String organisationname;
    public String getAgentCode() {
        return agentCode;
    }
    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }
    public String getOrganisationname() {
        return organisationname;
    }
    public void setOrganisationname(String organisationname) {
        this.organisationname = organisationname;
    }
	@Override
	public String toString() {
		return "AgentDetails [agentCode=" + agentCode + ", organisationname=" + organisationname + ", getAgentCode()="
				+ getAgentCode() + ", getOrganisationname()=" + getOrganisationname() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
    
    
    
}
