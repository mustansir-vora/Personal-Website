package com.farmers.bean.Agent.POST;

import java.util.ArrayList;
import java.util.List;

public class ResponseBody {
    private List<Agent> agents = new ArrayList<Agent>();
    public List<Agent> getAgents() {
        return agents;
    }
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }
	@Override
	public String toString() {
		return "ResponseBody [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
    
    
}
