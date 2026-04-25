package com.farmers.bean.PointerIDCard;

public class RenewalPolicyBillingDetails {
	 private FullBalanceDue_ fullBalanceDue;
	    private LastPaymentAmount_ lastPaymentAmount;
	    private String lastPaymentDate;
	    private NextPaymentAmount_ nextPaymentAmount;
	    private String nextPaymentDate;
	    private String policyActivityDate;
	    public FullBalanceDue_ getFullBalanceDue() {
	        return fullBalanceDue;
	    }
	    public void setFullBalanceDue(FullBalanceDue_ fullBalanceDue) {
	        this.fullBalanceDue = fullBalanceDue;
	    }
	    public LastPaymentAmount_ getLastPaymentAmount() {
	        return lastPaymentAmount;
	    }
	    public void setLastPaymentAmount(LastPaymentAmount_ lastPaymentAmount) {
	        this.lastPaymentAmount = lastPaymentAmount;
	    }
	    public String getLastPaymentDate() {
	        return lastPaymentDate;
	    }
	    public void setLastPaymentDate(String lastPaymentDate) {
	        this.lastPaymentDate = lastPaymentDate;
	    }
	    public NextPaymentAmount_ getNextPaymentAmount() {
	        return nextPaymentAmount;
	    }
	    public void setNextPaymentAmount(NextPaymentAmount_ nextPaymentAmount) {
	        this.nextPaymentAmount = nextPaymentAmount;
	    }
	    public String getNextPaymentDate() {
	        return nextPaymentDate;
	    }
	    public void setNextPaymentDate(String nextPaymentDate) {
	        this.nextPaymentDate = nextPaymentDate;
	    }
	    public String getPolicyActivityDate() {
	        return policyActivityDate;
	    }
	    public void setPolicyActivityDate(String policyActivityDate) {
	        this.policyActivityDate = policyActivityDate;
	    }
		@Override
		public String toString() {
			return "RenewalPolicyBillingDetails [fullBalanceDue=" + fullBalanceDue + ", lastPaymentAmount="
					+ lastPaymentAmount + ", lastPaymentDate=" + lastPaymentDate + ", nextPaymentAmount="
					+ nextPaymentAmount + ", nextPaymentDate=" + nextPaymentDate + ", policyActivityDate="
					+ policyActivityDate + ", getFullBalanceDue()=" + getFullBalanceDue() + ", getLastPaymentAmount()="
					+ getLastPaymentAmount() + ", getLastPaymentDate()=" + getLastPaymentDate()
					+ ", getNextPaymentAmount()=" + getNextPaymentAmount() + ", getNextPaymentDate()="
					+ getNextPaymentDate() + ", getPolicyActivityDate()=" + getPolicyActivityDate() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
