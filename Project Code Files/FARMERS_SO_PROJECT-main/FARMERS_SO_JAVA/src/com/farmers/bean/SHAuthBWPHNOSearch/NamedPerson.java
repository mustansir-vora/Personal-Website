package com.farmers.bean.SHAuthBWPHNOSearch;

public class NamedPerson {
    private String birthDate;
    private BirthName_ birthName;
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public BirthName_ getBirthName() {
        return birthName;
    }
    public void setBirthName(BirthName_ birthName) {
        this.birthName = birthName;
    }
	@Override
	public String toString() {
		return "NamedPerson [birthDate=" + birthDate + ", birthName=" + birthName + ", getBirthDate()=" + getBirthDate()
				+ ", getBirthName()=" + getBirthName() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
    
    
    
    
}
