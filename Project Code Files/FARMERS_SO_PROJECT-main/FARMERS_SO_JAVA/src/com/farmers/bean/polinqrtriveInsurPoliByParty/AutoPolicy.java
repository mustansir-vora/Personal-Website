package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class AutoPolicy {
	  public AgentDetails agentDetails;
	    public BasicPolicyDetail basicPolicyDetail;
	    public BasicPolicy basicPolicy;
	    public InsuredVehicle insuredVehicle;
	    public NamedInsured namedInsured;
		public AgentDetails getAgentDetails() {
			return agentDetails;
		}
		public void setAgentDetails(AgentDetails agentDetails) {
			this.agentDetails = agentDetails;
		}
		public BasicPolicyDetail getBasicPolicyDetail() {
			return basicPolicyDetail;
		}
		public void setBasicPolicyDetail(BasicPolicyDetail basicPolicyDetail) {
			this.basicPolicyDetail = basicPolicyDetail;
		}
		public BasicPolicy getBasicPolicy() {
			return basicPolicy;
		}
		public void setBasicPolicy(BasicPolicy basicPolicy) {
			this.basicPolicy = basicPolicy;
		}
		public InsuredVehicle getInsuredVehicle() {
			return insuredVehicle;
		}
		public void setInsuredVehicle(InsuredVehicle insuredVehicle) {
			this.insuredVehicle = insuredVehicle;
		}
		public NamedInsured getNamedInsured() {
			return namedInsured;
		}
		public void setNamedInsured(NamedInsured namedInsured) {
			this.namedInsured = namedInsured;
		}
		@Override
		public String toString() {
			return "AutoPolicy [agentDetails=" + agentDetails + ", getAgentDetails()=" + getAgentDetails()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
}
