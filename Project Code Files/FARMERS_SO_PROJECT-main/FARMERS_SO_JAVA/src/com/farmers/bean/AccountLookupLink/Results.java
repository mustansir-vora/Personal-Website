package com.farmers.bean.AccountLookupLink;

import java.util.ArrayList;
import java.util.List;

public class Results {
	 private List<Policy> policies = new ArrayList<Policy>();
	    private Object claims;
	    public List<Policy> getPolicies() {
	        return policies;
	    }
	    public void setPolicies(List<Policy> policies) {
	        this.policies = policies;
	    }
	    public Object getClaims() {
	        return claims;
	    }
	    public void setClaims(Object claims) {
	        this.claims = claims;
	    }
		@Override
		public String toString() {
			return "Results [claims=" + claims + ", getClaims()=" + getClaims() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
