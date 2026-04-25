package com.farmers.bean.SHAuthForemost;

import java.util.ArrayList;
import java.util.List;

public class ForemostRoot {
    private List<Policy> policies = new ArrayList<Policy>();
    public List<Policy> getPolicies() {
        return policies;
    }
    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }
	@Override
	public String toString() {
		return "Root [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
    
    
    
    
}
