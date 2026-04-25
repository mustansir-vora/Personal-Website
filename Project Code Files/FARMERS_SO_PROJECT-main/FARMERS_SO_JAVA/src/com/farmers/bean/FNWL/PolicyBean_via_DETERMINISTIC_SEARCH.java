package com.farmers.bean.FNWL;

import java.util.List;

public class PolicyBean_via_DETERMINISTIC_SEARCH {

	private String policyType;
	private String policyCategory;
	private String policyStateCode;
	private String policySource;
	private String policyNumber;
	private String issueDate;
	private String policyStatus;
	private String lineOfBusiness;
	private String bookOfBusiness;
	private List<String> rolesList;

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

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
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

	public List<String> getRolesList() {
		return rolesList;
	}

	public void setRolesList(List<String> rolesList) {
		this.rolesList = rolesList;
	}

	@Override
	public String toString() {
		return "PolicyBean_via_DOB_ZIP_SSN [policyType=" + policyType + ", policyCategory=" + policyCategory + ", policyStateCode=" + policyStateCode + ", policySource=" + policySource + ", policyNumber=" + policyNumber + ", issueDate=" + issueDate + ", policyStatus=" + policyStatus + ", lineOfBusiness=" + lineOfBusiness + ", bookOfBusiness=" + bookOfBusiness + ", rolesList=" + rolesList + "]";
	}

}
