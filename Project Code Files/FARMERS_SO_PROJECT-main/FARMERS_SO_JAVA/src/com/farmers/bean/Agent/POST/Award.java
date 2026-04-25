package com.farmers.bean.Agent.POST;

public class Award {
	 private String awardYear;
	    private String award;
	    public String getAwardYear() {
	        return awardYear;
	    }
	    public void setAwardYear(String awardYear) {
	        this.awardYear = awardYear;
	    }
	    public String getAward() {
	        return award;
	    }
	    public void setAward(String award) {
	        this.award = award;
	    }
		@Override
		public String toString() {
			return "Award [awardYear=" + awardYear + ", award=" + award + ", getAwardYear()=" + getAwardYear()
					+ ", getAward()=" + getAward() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
