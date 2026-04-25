package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class NamedInsured {

	public String birthDate;
    public BirthName birthName;
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
		return "NamedInsured [birthDate=" + birthDate + "]";
	}
	
}
