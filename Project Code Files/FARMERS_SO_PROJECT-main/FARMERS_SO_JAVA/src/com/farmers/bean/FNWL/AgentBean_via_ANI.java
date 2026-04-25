package com.farmers.bean.FNWL;

import org.json.simple.JSONArray;

public class AgentBean_via_ANI {
	
	private String AORId;
    private String UPN;
    private String agentCode;
    private String agentType;
    private JSONArray awards;
    
	public String getAORId() {
		return AORId;
	}
	public void setAORId(String aORId) {
		AORId = aORId;
	}
	public String getUPN() {
		return UPN;
	}
	public void setUPN(String uPN) {
		UPN = uPN;
	}
	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public JSONArray getAwards() {
		return awards;
	}
	public void setAwards(JSONArray awards) {
		this.awards = awards;
	}

	@Override
	public String toString() {
		return "AgentBean_via_ANI [AORId = " + AORId + ", UPN = " + UPN + ", agentCode = " + agentCode + ", agentType = " + agentType + ", awards = " + awards + "]";
	}
}
