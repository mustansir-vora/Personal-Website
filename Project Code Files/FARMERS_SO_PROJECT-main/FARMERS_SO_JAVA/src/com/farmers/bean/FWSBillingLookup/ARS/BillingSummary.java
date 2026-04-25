package com.farmers.bean.FWSBillingLookup.ARS;

import java.util.ArrayList;
import java.util.List;
public class BillingSummary {
    private String gPC;
    private List<Producer> producer = new ArrayList<Producer>();
    private ReturnMessage returnMessage;
    private Policy policy;
    private Terms terms;
    private List<Due> dues = new ArrayList<Due>();
    public String getGPC() {
        return gPC;
    }
    public void setGPC(String gPC) {
        this.gPC = gPC;
    }
    public List<Producer> getProducer() {
        return producer;
    }
    public void setProducer(List<Producer> producer) {
        this.producer = producer;
    }
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
		return "BillingSummary [gPC=" + gPC + ", dues=" + dues + ", getGPC()=" + getGPC() + ", getDues()=" + getDues()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
}
