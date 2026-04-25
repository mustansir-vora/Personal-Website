package com.farmers.bean.PolicyInqReteval;

import java.util.ArrayList;
import java.util.List;

public class Household {
	private List<AutoPolicy> autoPolicies = new ArrayList<AutoPolicy>();
    public List<AutoPolicy> getAutoPolicies() {
        return autoPolicies;
    }
    public void setAutoPolicies(List<AutoPolicy> autoPolicies) {
        this.autoPolicies = autoPolicies;
    }
	@Override
	public String toString() {
		return "Household [autoPolicies=" + autoPolicies + ", getAutoPolicies()=" + getAutoPolicies() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
}
