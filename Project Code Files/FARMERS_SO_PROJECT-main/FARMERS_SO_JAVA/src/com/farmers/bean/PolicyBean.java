package com.farmers.bean;

import com.farmers.util.Constants;

public class PolicyBean {
	
	private String strPolicyNum = Constants.EmptyString;
	private String strDOB = Constants.EmptyString;
	private String strZipCode = Constants.EmptyString;
	private String strPolicySource = Constants.EmptyString;
	private String strPolicyState = Constants.EmptyString;
	private String strPolicySuffix = Constants.EmptyString;
	private String strEffectiveDate = Constants.EmptyString;
	private String strFirstName = Constants.EmptyString;
	private String strLastName = Constants.EmptyString;
	private String strMiddleName = Constants.EmptyString;
	private String strInternalPolicyNumber = Constants.EmptyString;
	private String strInternalPolicyVersion = Constants.EmptyString;
	private String strBillingAccountNumber = Constants.EmptyString;
	private String strCompanyCode = Constants.EmptyString;
	private String strPolicyLOB = Constants.EmptyString;
	private String strPolicyProductCode = Constants.EmptyString;
	private String strAgentAORID = Constants.EmptyString;
	private String strsubCategory = Constants.EmptyString;
	private String strEmail = Constants.EmptyString;
	private String gpcCode = Constants.EmptyString;
	private String policyType = Constants.EmptyString;
	private String policyStatus = Constants.EmptyString;
	private String policyCategory= Constants.EmptyString;
	//CS1348016 - All BU's - Onboarding Line Routing
	private String inceptionDate= Constants.EmptyString;
	public String getInceptionDate() {
		return inceptionDate;
	}
	public void setInceptionDate(String inceptionDate) {
		this.inceptionDate = inceptionDate;
	}
	//-----------------------------
	//CS1360621-Add 'IA' Routing Qualifier

	public String getpolicyCategory() {
		return policyCategory;
	}
	public void setpolicyCategory(String policyCategory) {
		this.policyCategory = policyCategory;
	}
	//-------------------------------
	
	public String getGpcCode() {
		return gpcCode;
	}
	public void setGpcCode(String gpcCode) {
		this.gpcCode = gpcCode;
	}
	public String getPayPlan() {
		return payPlan;
	}
	public void setPayPlan(String payPlan) {
		this.payPlan = payPlan;
	}
	public String getServicingPhoneNumber() {
		return servicingPhoneNumber;
	}
	public void setServicingPhoneNumber(String servicingPhoneNumber) {
		this.servicingPhoneNumber = servicingPhoneNumber;
	}
	private String payPlan = Constants.EmptyString;
	private String servicingPhoneNumber = Constants.EmptyString;
	public String getStrAgentAORID() {
		return strAgentAORID;
	}
	public void setStrAgentAORID(String strAgentAORID) {
		this.strAgentAORID = strAgentAORID;
	}
	public String getStrPolicyProductCode() {
		return strPolicyProductCode;
	}
	public void setStrPolicyProductCode(String strPolicyProductCode) {
		this.strPolicyProductCode = strPolicyProductCode;
	}
	public String getStrPolicyNum() {
		return strPolicyNum;
	}
	public void setStrPolicyNum(String strPolicyNum) {
		this.strPolicyNum = strPolicyNum;
	}
	public String getStrDOB() {
		return strDOB;
	}
	public void setStrDOB(String strDOB) {
		this.strDOB = strDOB;
	}
	public String getStrZipCode() {
		return strZipCode;
	}
	public void setStrZipCode(String strZipCode) {
		this.strZipCode = strZipCode;
	}
	public String getStrPolicySource() {
		return strPolicySource;
	}
	public void setStrPolicySource(String strPolicySource) {
		this.strPolicySource = strPolicySource;
	}
	public String getStrPolicyState() {
		return strPolicyState;
	}
	public void setStrPolicyState(String strPolicyState) {
		this.strPolicyState = strPolicyState;
	}
	public String getStrPolicySuffix() {
		return strPolicySuffix;
	}
	public void setStrPolicySuffix(String strPolicySuffix) {
		this.strPolicySuffix = strPolicySuffix;
	}
	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}
	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}
	public String getStrFirstName() {
		return strFirstName;
	}
	public void setStrFirstName(String strFirstName) {
		this.strFirstName = strFirstName;
	}
	public String getStrLastName() {
		return strLastName;
	}
	public void setStrLastName(String strLastName) {
		this.strLastName = strLastName;
	}
	public String getStrMiddleName() {
		return strMiddleName;
	}
	public void setStrMiddleName(String strMiddleName) {
		this.strMiddleName = strMiddleName;
	}
	public String getStrInternalPolicyNumber() {
		return strInternalPolicyNumber;
	}
	public void setStrInternalPolicyNumber(String strInternalPolicyNumber) {
		this.strInternalPolicyNumber = strInternalPolicyNumber;
	}
	public String getStrInternalPolicyVersion() {
		return strInternalPolicyVersion;
	}
	public void setStrInternalPolicyVersion(String strInternalPolicyVersion) {
		this.strInternalPolicyVersion = strInternalPolicyVersion;
	}
	
	public String getStrBillingAccountNumber() {
		return strBillingAccountNumber;
	}
	public void setStrBillingAccountNumber(String strBillingAccountNumber) {
		this.strBillingAccountNumber = strBillingAccountNumber;
	}
	public String getStrCompanyCode() {
		return strCompanyCode;
	}
	public void setStrCompanyCode(String strCompanyCode) {
		this.strCompanyCode = strCompanyCode;
	}
	public String getStrPolicyLOB() {
		return strPolicyLOB;
	}
	public void setStrPolicyLOB(String strPolicyLOB) {
		this.strPolicyLOB = strPolicyLOB;
	}
	public String getStrsubCategory() {
		return strsubCategory;
	}
	public void setStrsubCategory(String strsubCategory) {
		this.strsubCategory = strsubCategory;
	}
	public String getStrEmail() {
		return strEmail;
	}
	public void setStrEmail(String Email) {
		this.strEmail = strEmail;
	}
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	public String getPolicyType() {
		return policyType;
	}
	// CS1336023 - Cancel policy - Arshath - start
	public String getPolicyStatus() {
		return policyStatus;
	}
	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}
	// CS1336023 - Cancel policy - Arshath - End
	//CS1348016 - All BU's - Onboarding Line Routing--inception Date 
	@Override
	public String toString() {
		return "PolicyBean [strPolicyNum=" + strPolicyNum + ", strDOB=" + strDOB + ", strZipCode=" + strZipCode
				+ ", strPolicySource=" + strPolicySource + ", strPolicyState=" + strPolicyState + ", strPolicySuffix="
				+ strPolicySuffix + ", strEffectiveDate=" + strEffectiveDate + ", strFirstName=" + strFirstName
				+ ", strLastName=" + strLastName + ", strMiddleName=" + strMiddleName + ", strInternalPolicyNumber="
				+ strInternalPolicyNumber + ", strInternalPolicyVersion=" + strInternalPolicyVersion
				+ ", strBillingAccountNumber=" + strBillingAccountNumber + ", strCompanyCode=" + strCompanyCode
				+ ", strPolicyLOB=" + strPolicyLOB + ", strPolicyProductCode=" + strPolicyProductCode
				+ ", strAgentAORID=" + strAgentAORID + ", strsubCategory=" + strsubCategory + ", strEmail=" + strEmail
				+ ", gpcCode=" + gpcCode + ", policyType=" + policyType + ", policyStatus=" + policyStatus
				+ ", inceptionDate=" + inceptionDate + ", payPlan=" + payPlan + ", servicingPhoneNumber="
				+ servicingPhoneNumber + "]";
	}
	

	

}
