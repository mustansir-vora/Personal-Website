package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class AgentDetails {
    public String agentCode;
    public String organisationname;
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
