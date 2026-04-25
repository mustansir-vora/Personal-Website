package com.farmers.bean.SharedAContactInfo;

import java.util.ArrayList;

public class Agent
{
    public String agentOfRecordId;
    public String parentUniqueProducerNumber;
    public String agentCode;
    public String agentType;
    public ArrayList<Object> awards;
	public String getAgentOfRecordId() {
		return agentOfRecordId;
	}
	public void setAgentOfRecordId(String agentOfRecordId) {
		this.agentOfRecordId = agentOfRecordId;
	}
	public String getParentUniqueProducerNumber() {
		return parentUniqueProducerNumber;
	}
	public void setParentUniqueProducerNumber(String parentUniqueProducerNumber) {
		this.parentUniqueProducerNumber = parentUniqueProducerNumber;
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
	public ArrayList<Object> getAwards() {
		return awards;
	}
	public void setAwards(ArrayList<Object> awards) {
		this.awards = awards;
	}
	@Override
	public String toString() {
		return "Agent [agentOfRecordId=" + agentOfRecordId + ", parentUniqueProducerNumber="
				+ parentUniqueProducerNumber + ", agentCode=" + agentCode + ", agentType=" + agentType + ", awards="
				+ awards + ", getAgentOfRecordId()=" + getAgentOfRecordId() + ", getParentUniqueProducerNumber()="
				+ getParentUniqueProducerNumber() + ", getAgentCode()=" + getAgentCode() + ", getAgentType()="
				+ getAgentType() + ", getAwards()=" + getAwards() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
    
}
