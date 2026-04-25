package com.farmers.bean.SHAuthBWPHNOSearch;

public class NamedInsured {
	 private String birthDate;
	    private BirthName birthName;
	    public String getBirthDate() {
	        return birthDate;
	    }
	    public void setBirthDate(String birthDate) {
	        this.birthDate = birthDate;
	    }
	    public BirthName getBirthName() {
	        return birthName;
	    }
	    public void setBirthName(BirthName birthName) {
	        this.birthName = birthName;
	    }
		@Override
		public String toString() {
			return "NamedInsured [birthDate=" + birthDate + ", birthName=" + birthName + ", getBirthDate()="
					+ getBirthDate() + ", getBirthName()=" + getBirthName() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
}
