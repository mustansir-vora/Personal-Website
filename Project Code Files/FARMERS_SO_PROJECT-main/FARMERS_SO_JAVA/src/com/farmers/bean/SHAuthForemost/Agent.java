package com.farmers.bean.SHAuthForemost;

import java.util.ArrayList;
import java.util.List;

public class Agent {
	private String agentOfRecordId;
    private String agencyName;
    private Name__ name;
    private List<Address___> addresses = new ArrayList<Address___>();
    private String agentTypeCode;
    private String faxNumber;
    private List<PhoneNumber__> phoneNumbers = new ArrayList<PhoneNumber__>();
    private List<EmailAddress_> emailAddresses = new ArrayList<EmailAddress_>();
    public String getAgentOfRecordId() {
        return agentOfRecordId;
    }
    public void setAgentOfRecordId(String agentOfRecordId) {
        this.agentOfRecordId = agentOfRecordId;
    }
    public String getAgencyName() {
        return agencyName;
    }
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    public Name__ getName() {
        return name;
    }
    public void setName(Name__ name) {
        this.name = name;
    }
    public List<Address___> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address___> addresses) {
        this.addresses = addresses;
    }
    public String getAgentTypeCode() {
        return agentTypeCode;
    }
    public void setAgentTypeCode(String agentTypeCode) {
        this.agentTypeCode = agentTypeCode;
    }
    public String getFaxNumber() {
        return faxNumber;
    }
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }
    public List<PhoneNumber__> getPhoneNumbers() {
        return phoneNumbers;
    }
    public void setPhoneNumbers(List<PhoneNumber__> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    public List<EmailAddress_> getEmailAddresses() {
        return emailAddresses;
    }
    public void setEmailAddresses(List<EmailAddress_> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
	@Override
	public String toString() {
		return "Agent [agentOfRecordId=" + agentOfRecordId + ", agencyName=" + agencyName + ", agentTypeCode="
				+ agentTypeCode + ", faxNumber=" + faxNumber + ", getAgentOfRecordId()=" + getAgentOfRecordId()
				+ ", getAgencyName()=" + getAgencyName() + ", getAgentTypeCode()=" + getAgentTypeCode()
				+ ", getFaxNumber()=" + getFaxNumber() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
    
    
    
    
}
