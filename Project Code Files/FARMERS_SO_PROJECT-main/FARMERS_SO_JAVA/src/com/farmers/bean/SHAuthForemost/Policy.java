package com.farmers.bean.SHAuthForemost;

import java.util.ArrayList;
import java.util.List;

public class Policy {
	 private String policyNumber;
	    private String insurerCompanyCode;
	    private String termID;
	    private String policyProductCode;
	    private String policySystemCode;
	    private String policyStatusCode;
	    private String policyStateCode;
	    private String policyStatus;
	    private String termStartDate;
	    private String termEndDate;
	    private String inceptionDate;
	    private String agentOfRecordId;
	    private String countryCode;
	    private String policyTransactionId;
	    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
	    private List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
	    private List<Address> addresses = new ArrayList<Address>();
	    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	    private List<Driver> drivers = new ArrayList<Driver>();
	    private List<Insured> insureds = new ArrayList<Insured>();
	    private List<Agent> agents = new ArrayList<Agent>();
	    public String getPolicyNumber() {
	        return policyNumber;
	    }
	    public void setPolicyNumber(String policyNumber) {
	        this.policyNumber = policyNumber;
	    }
	    public String getInsurerCompanyCode() {
	        return insurerCompanyCode;
	    }
	    public void setInsurerCompanyCode(String insurerCompanyCode) {
	        this.insurerCompanyCode = insurerCompanyCode;
	    }
	    public String getTermID() {
	        return termID;
	    }
	    public void setTermID(String termID) {
	        this.termID = termID;
	    }
	    public String getPolicyProductCode() {
	        return policyProductCode;
	    }
	    public void setPolicyProductCode(String policyProductCode) {
	        this.policyProductCode = policyProductCode;
	    }
	    public String getPolicySystemCode() {
	        return policySystemCode;
	    }
	    public void setPolicySystemCode(String policySystemCode) {
	        this.policySystemCode = policySystemCode;
	    }
	    public String getPolicyStatusCode() {
	        return policyStatusCode;
	    }
	    public void setPolicyStatusCode(String policyStatusCode) {
	        this.policyStatusCode = policyStatusCode;
	    }
	    public String getPolicyStateCode() {
	        return policyStateCode;
	    }
	    public void setPolicyStateCode(String policyStateCode) {
	        this.policyStateCode = policyStateCode;
	    }
	    public String getPolicyStatus() {
	        return policyStatus;
	    }
	    public void setPolicyStatus(String policyStatus) {
	        this.policyStatus = policyStatus;
	    }
	    public String getTermStartDate() {
	        return termStartDate;
	    }
	    public void setTermStartDate(String termStartDate) {
	        this.termStartDate = termStartDate;
	    }
	    public String getTermEndDate() {
	        return termEndDate;
	    }
	    public void setTermEndDate(String termEndDate) {
	        this.termEndDate = termEndDate;
	    }
	    public String getInceptionDate() {
	        return inceptionDate;
	    }
	    public void setInceptionDate(String inceptionDate) {
	        this.inceptionDate = inceptionDate;
	    }
	    public String getAgentOfRecordId() {
	        return agentOfRecordId;
	    }
	    public void setAgentOfRecordId(String agentOfRecordId) {
	        this.agentOfRecordId = agentOfRecordId;
	    }
	    public String getCountryCode() {
	        return countryCode;
	    }
	    public void setCountryCode(String countryCode) {
	        this.countryCode = countryCode;
	    }
	    public String getPolicyTransactionId() {
	        return policyTransactionId;
	    }
	    public void setPolicyTransactionId(String policyTransactionId) {
	        this.policyTransactionId = policyTransactionId;
	    }
	    public List<PhoneNumber> getPhoneNumbers() {
	        return phoneNumbers;
	    }
	    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
	        this.phoneNumbers = phoneNumbers;
	    }
	    public List<EmailAddress> getEmailAddresses() {
	        return emailAddresses;
	    }
	    public void setEmailAddresses(List<EmailAddress> emailAddresses) {
	        this.emailAddresses = emailAddresses;
	    }
	    public List<Address> getAddresses() {
	        return addresses;
	    }
	    public void setAddresses(List<Address> addresses) {
	        this.addresses = addresses;
	    }
	    public List<Vehicle> getVehicles() {
	        return vehicles;
	    }
	    public void setVehicles(List<Vehicle> vehicles) {
	        this.vehicles = vehicles;
	    }
	    public List<Driver> getDrivers() {
	        return drivers;
	    }
	    public void setDrivers(List<Driver> drivers) {
	        this.drivers = drivers;
	    }
	    public List<Insured> getInsureds() {
	        return insureds;
	    }
	    public void setInsureds(List<Insured> insureds) {
	        this.insureds = insureds;
	    }
	    public List<Agent> getAgents() {
	        return agents;
	    }
	    public void setAgents(List<Agent> agents) {
	        this.agents = agents;
	    }
		@Override
		public String toString() {
			return "Policy [policyNumber=" + policyNumber + ", insurerCompanyCode=" + insurerCompanyCode + ", termID="
					+ termID + ", policyProductCode=" + policyProductCode + ", policySystemCode=" + policySystemCode
					+ ", policyStatusCode=" + policyStatusCode + ", policyStateCode=" + policyStateCode
					+ ", policyStatus=" + policyStatus + ", termStartDate=" + termStartDate + ", termEndDate="
					+ termEndDate + ", inceptionDate=" + inceptionDate + ", agentOfRecordId=" + agentOfRecordId
					+ ", countryCode=" + countryCode + ", policyTransactionId=" + policyTransactionId
					+ ", getPolicyNumber()=" + getPolicyNumber() + ", getInsurerCompanyCode()="
					+ getInsurerCompanyCode() + ", getTermID()=" + getTermID() + ", getPolicyProductCode()="
					+ getPolicyProductCode() + ", getPolicySystemCode()=" + getPolicySystemCode()
					+ ", getPolicyStatusCode()=" + getPolicyStatusCode() + ", getPolicyStateCode()="
					+ getPolicyStateCode() + ", getPolicyStatus()=" + getPolicyStatus() + ", getTermStartDate()="
					+ getTermStartDate() + ", getTermEndDate()=" + getTermEndDate() + ", getInceptionDate()="
					+ getInceptionDate() + ", getAgentOfRecordId()=" + getAgentOfRecordId() + ", getCountryCode()="
					+ getCountryCode() + ", getPolicyTransactionId()=" + getPolicyTransactionId() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
	    
}
