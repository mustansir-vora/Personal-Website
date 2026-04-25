package com.farmers.bean.SHAuthForemost;

import java.util.ArrayList;
import java.util.List;

public class Insured {
	  private Name_ name;
	    private List<Object> legalNames = new ArrayList<Object>();
	    private List<Address__> addresses = new ArrayList<Address__>();
	    private List<PhoneNumber_> phoneNumbers = new ArrayList<PhoneNumber_>();
	    private String birthDate;
	    private String ssnLast4;
	    public Name_ getName() {
	        return name;
	    }
	    public void setName(Name_ name) {
	        this.name = name;
	    }
	    public List<Object> getLegalNames() {
	        return legalNames;
	    }
	    public void setLegalNames(List<Object> legalNames) {
	        this.legalNames = legalNames;
	    }
	    public List<Address__> getAddresses() {
	        return addresses;
	    }
	    public void setAddresses(List<Address__> addresses) {
	        this.addresses = addresses;
	    }
	    public List<PhoneNumber_> getPhoneNumbers() {
	        return phoneNumbers;
	    }
	    public void setPhoneNumbers(List<PhoneNumber_> phoneNumbers) {
	        this.phoneNumbers = phoneNumbers;
	    }
	    public String getBirthDate() {
	        return birthDate;
	    }
	    public void setBirthDate(String birthDate) {
	        this.birthDate = birthDate;
	    }
	    public String getSsnLast4() {
	        return ssnLast4;
	    }
	    public void setSsnLast4(String ssnLast4) {
	        this.ssnLast4 = ssnLast4;
	    }
		@Override
		public String toString() {
			return "Insured [birthDate=" + birthDate + ", ssnLast4=" + ssnLast4 + ", getBirthDate()=" + getBirthDate()
					+ ", getSsnLast4()=" + getSsnLast4() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
	    
}
