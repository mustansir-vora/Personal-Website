package com.farmers.bean.FNWL;

import java.util.List;

public class PolicyBean_via_ANI {
	
	private String policyType;
    private String policyCategory;
    private String policyStateCode;
    private String policySource;
    private String policyNumber;
    private String policyStatus;
    private String lineOfBusiness;
    private String bookOfBusiness;
    private String issueDate;
    private List<String> roles;
    private String dateOfBirth;
    private String zip;
    private String addressType;
    private String addressLine1;
    private String city;
    private String state;
    private String country;
    private String ssn;
    private String firstName;
    private String lastName;
    private String taxID;
    
	
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	public String getPolicyCategory() {
		return policyCategory;
	}
	public void setPolicyCategory(String policyCategory) {
		this.policyCategory = policyCategory;
	}
	public String getPolicyStateCode() {
		return policyStateCode;
	}
	public void setPolicyStateCode(String policyStateCode) {
		this.policyStateCode = policyStateCode;
	}
	public String getPolicySource() {
		return policySource;
	}
	public void setPolicySource(String policySource) {
		this.policySource = policySource;
	}
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}
	public String getPolicyStatus() {
		return policyStatus;
	}
	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}
	public String getLineOfBusiness() {
		return lineOfBusiness;
	}
	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}
	public String getBookOfBusiness() {
		return bookOfBusiness;
	}
	public void setBookOfBusiness(String bookOfBusiness) {
		this.bookOfBusiness = bookOfBusiness;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
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
	public String getTaxID() {
		return taxID;
	}
	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}
	
	@Override
	public String toString() {
		return "PolicyBean_via_ANI [policyType = " + policyType + ", policyCategory = " + policyCategory + ", policyStateCode = " + policyStateCode + ", policySource = " + policySource + ", policyNumber = " + policyNumber + ", policyStatus = " + policyStatus + ", lineOfBusiness = " + lineOfBusiness + ", bookOfBusiness = " + bookOfBusiness + ", issueDate = " + issueDate + ", roles = " + roles + ", dateOfBirth = " + dateOfBirth + ", zip = " + zip + ", addressType = " + addressType + ", addressLine1 = " + addressLine1 +", city = " + city + ", state = " + state + ", country = " + country + ", ssn = " + ssn + ", taxID = " + taxID + ", firstName = " + firstName + ", lastName = " + lastName + "]";
	}
}
