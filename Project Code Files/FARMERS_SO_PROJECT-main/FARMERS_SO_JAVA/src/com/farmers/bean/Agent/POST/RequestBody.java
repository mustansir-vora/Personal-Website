package com.farmers.bean.Agent.POST;

public class RequestBody {
	 private Criteria criteria;
	    public Criteria getCriteria() {
	        return criteria;
	    }
	    public void setCriteria(Criteria criteria) {
	        this.criteria = criteria;
	    }
		@Override
		public String toString() {
			return "RequestBody [criteria=" + criteria + ", getCriteria()=" + getCriteria() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
