package com.farmers.bean.FWSBillingLookup.PointBilling;

import java.util.ArrayList;
import java.util.List;

public class Term {
	 private Payplan payplan;
	    private List<Due> dues = new ArrayList<Due>();
	    public Payplan getPayplan() {
	        return payplan;
	    }
	    public void setPayplan(Payplan payplan) {
	        this.payplan = payplan;
	    }
	    public List<Due> getDues() {
	        return dues;
	    }
	    public void setDues(List<Due> dues) {
	        this.dues = dues;
	    }
		@Override
		public String toString() {
			return "Terms [payplan=" + payplan + ", getPayplan()=" + getPayplan() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	 
}
