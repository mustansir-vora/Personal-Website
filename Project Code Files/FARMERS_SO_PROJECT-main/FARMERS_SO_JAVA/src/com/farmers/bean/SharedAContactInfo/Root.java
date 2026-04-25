package com.farmers.bean.SharedAContactInfo;

import java.util.ArrayList;

public class Root {
	 public ArrayList<Agent> agents;

	public ArrayList<Agent> getAgents() {
		return agents;
	}

	public void setAgents(ArrayList<Agent> agents) {
		this.agents = agents;
	}

	@Override
	public String toString() {
		return "Root [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	 
	 
}
