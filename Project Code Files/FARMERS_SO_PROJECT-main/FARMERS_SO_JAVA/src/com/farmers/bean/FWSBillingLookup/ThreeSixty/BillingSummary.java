package com.farmers.bean.FWSBillingLookup.ThreeSixty;

import java.util.ArrayList;
import java.util.List;

public class BillingSummary {
	private ReturnMessage returnMessage;
    private Policy policy;
    private Terms terms;
    private List<Due> dues = new ArrayList<Due>();
    public ReturnMessage getReturnMessage() {
        return returnMessage;
    }
    public void setReturnMessage(ReturnMessage returnMessage) {
        this.returnMessage = returnMessage;
    }
    public Policy getPolicy() {
        return policy;
    }
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
    public Terms getTerms() {
        return terms;
    }
    public void setTerms(Terms terms) {
        this.terms = terms;
    }
    public List<Due> getDues() {
        return dues;
    }
    public void setDues(List<Due> dues) {
        this.dues = dues;
    }
	@Override
	public String toString() {
		return "BillingSummary [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
