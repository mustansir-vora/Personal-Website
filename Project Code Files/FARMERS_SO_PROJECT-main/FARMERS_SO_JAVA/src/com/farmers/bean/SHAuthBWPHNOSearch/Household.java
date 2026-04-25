package com.farmers.bean.SHAuthBWPHNOSearch;

import java.util.ArrayList;
import java.util.List;

public class Household {
	  private List<AutoPolicy> autoPolicies = new ArrayList<AutoPolicy>();
	    private List<HomePolicy> homePolicies = new ArrayList<HomePolicy>();
	    public List<AutoPolicy> getAutoPolicies() {
	        return autoPolicies;
	    }
	    public void setAutoPolicies(List<AutoPolicy> autoPolicies) {
	        this.autoPolicies = autoPolicies;
	    }
	    public List<HomePolicy> getHomePolicies() {
	        return homePolicies;
	    }
	    public void setHomePolicies(List<HomePolicy> homePolicies) {
	        this.homePolicies = homePolicies;
	    }
		@Override
		public String toString() {
			return "Household [autoPolicies=" + autoPolicies + ", homePolicies=" + homePolicies + ", getAutoPolicies()="
					+ getAutoPolicies() + ", getHomePolicies()=" + getHomePolicies() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
