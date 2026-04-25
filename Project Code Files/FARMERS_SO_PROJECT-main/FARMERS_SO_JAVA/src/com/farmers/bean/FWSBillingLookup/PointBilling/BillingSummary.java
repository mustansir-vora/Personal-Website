package com.farmers.bean.FWSBillingLookup.PointBilling;

import java.util.ArrayList;
import java.util.List;

public class BillingSummary {
	 private List<Term> terms = new ArrayList<Term>();
	    public List<Term> getTerms() {
	        return terms;
	    }
	    public void setTerms(List<Term> terms) {
	        this.terms = terms;
	    }
		@Override
		public String toString() {
			return "BillingSummary [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	
}
