package com.farmers.bean.BillPresentMents;

public class MoneyScheduler 
{
	
	
	 private NextPaymentAmount nextPaymentAmount;
	    public NextPaymentAmount getNextPaymentAmount() {
	        return nextPaymentAmount;
	    }
	    public void setNextPaymentAmount(NextPaymentAmount nextPaymentAmount) {
	        this.nextPaymentAmount = nextPaymentAmount;
	    }
		@Override
		public String toString() {
			return "MoneyScheduler [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	    
	    
	    

}
