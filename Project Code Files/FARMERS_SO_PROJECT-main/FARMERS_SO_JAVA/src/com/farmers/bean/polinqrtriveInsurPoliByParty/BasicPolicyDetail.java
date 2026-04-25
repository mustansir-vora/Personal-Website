package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class BasicPolicyDetail {
	
    public String insurerCompanyCode;
    public String policySymbol;
	public String getInsurerCompanyCode() {
		return insurerCompanyCode;
	}
	public void setInsurerCompanyCode(String insurerCompanyCode) {
		this.insurerCompanyCode = insurerCompanyCode;
	}
	public String getPolicySymbol() {
		return policySymbol;
	}
	public void setPolicySymbol(String policySymbol) {
		this.policySymbol = policySymbol;
	}
	@Override
	public String toString() {
		return "BasicPolicyDetail [insurerCompanyCode=" + insurerCompanyCode + ", policySymbol=" + policySymbol
				+ ", getInsurerCompanyCode()=" + getInsurerCompanyCode() + ", getPolicySymbol()=" + getPolicySymbol()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
    

}
