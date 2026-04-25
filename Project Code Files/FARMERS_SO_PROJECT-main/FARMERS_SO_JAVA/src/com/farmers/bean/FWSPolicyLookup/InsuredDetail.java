package com.farmers.bean.FWSPolicyLookup;

public class InsuredDetail {
	 private String birthDate;
	    private String firstName;
	    private String lastName;
	    private String type;
	    public String getBirthDate() {
	        return birthDate;
	    }
	    public void setBirthDate(String birthDate) {
	        this.birthDate = birthDate;
	    }
	    public String getFirstName() {
	        return firstName;
	    }
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }
	    public String getLastName() {
	        return lastName;
	    }
	    public void setLastName(String lastName) {
	        this.lastName = lastName;
	    }
	    public String getType() {
	        return type;
	    }
	    public void setType(String type) {
	        this.type = type;
	    }
		@Override
		public String toString() {
			return "InsuredDetail [birthDate=" + birthDate + ", firstName=" + firstName + ", lastName=" + lastName
					+ ", type=" + type + ", getBirthDate()=" + getBirthDate() + ", getFirstName()=" + getFirstName()
					+ ", getLastName()=" + getLastName() + ", getType()=" + getType() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
}
