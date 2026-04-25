package com.farmers.bean.Agent.POST;

import java.util.ArrayList;
import java.util.List;

public class Agent {
	  private String agentType;
	    private String agentOfRecordId;
	    private String agentCode;
	    private List<Award> awards = new ArrayList<Award>();
	    private String parentUniqueProducerNumber;
	    public String getAgentType() {
	        return agentType;
	    }
	    public void setAgentType(String agentType) {
	        this.agentType = agentType;
	    }
	    public String getAgentOfRecordId() {
	        return agentOfRecordId;
	    }
	    public void setAgentOfRecordId(String agentOfRecordId) {
	        this.agentOfRecordId = agentOfRecordId;
	    }
	    public String getAgentCode() {
	        return agentCode;
	    }
	    public void setAgentCode(String agentCode) {
	        this.agentCode = agentCode;
	    }
	    public List<Award> getAwards() {
	        return awards;
	    }
	    public void setAwards(List<Award> awards) {
	        this.awards = awards;
	    }
	    public String getParentUniqueProducerNumber() {
	        return parentUniqueProducerNumber;
	    }
	    public void setParentUniqueProducerNumber(String parentUniqueProducerNumber) {
	        this.parentUniqueProducerNumber = parentUniqueProducerNumber;
	    }
		@Override
		public String toString() {
			return "Agent [agentType=" + agentType + ", agentOfRecordId=" + agentOfRecordId + ", agentCode=" + agentCode
					+ ", parentUniqueProducerNumber=" + parentUniqueProducerNumber + ", getAgentType()="
					+ getAgentType() + ", getAgentOfRecordId()=" + getAgentOfRecordId() + ", getAgentCode()="
					+ getAgentCode() + ", getParentUniqueProducerNumber()=" + getParentUniqueProducerNumber()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
}
