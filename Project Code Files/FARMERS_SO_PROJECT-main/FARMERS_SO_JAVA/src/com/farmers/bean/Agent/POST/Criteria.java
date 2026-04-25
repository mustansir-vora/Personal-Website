package com.farmers.bean.Agent.POST;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
	  private String systemName;
	    private List<AgentSearchCriterium> agentSearchCriteria = new ArrayList<AgentSearchCriterium>();
	    private String userId;
	    public String getSystemName() {
	        return systemName;
	    }
	    public void setSystemName(String systemName) {
	        this.systemName = systemName;
	    }
	    public List<AgentSearchCriterium> getAgentSearchCriteria() {
	        return agentSearchCriteria;
	    }
	    public void setAgentSearchCriteria(List<AgentSearchCriterium> agentSearchCriteria) {
	        this.agentSearchCriteria = agentSearchCriteria;
	    }
	    public String getUserId() {
	        return userId;
	    }
	    public void setUserId(String userId) {
	        this.userId = userId;
	    }
		@Override
		public String toString() {
			return "Criteria [systemName=" + systemName + ", agentSearchCriteria=" + agentSearchCriteria + ", userId="
					+ userId + ", getSystemName()=" + getSystemName() + ", getAgentSearchCriteria()="
					+ getAgentSearchCriteria() + ", getUserId()=" + getUserId() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
}
