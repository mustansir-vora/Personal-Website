package com.farmers.bean.FWSPolicyLookup;

import java.util.ArrayList;
import java.util.List;

public class Root {
	private List<Policy> policies = new ArrayList<Policy>();
    public List<Policy> getPolicies() {
        return policies;
    }
    public void setPolicies(List<Policy> policies) {
        
	    
	    
}
	@Override
	public String toString() {
		return "Root [policies=" + policies + ", getPolicies()=" + getPolicies() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
