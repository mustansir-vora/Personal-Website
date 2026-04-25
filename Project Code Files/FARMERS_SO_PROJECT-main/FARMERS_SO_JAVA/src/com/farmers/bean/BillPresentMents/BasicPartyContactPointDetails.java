package com.farmers.bean.BillPresentMents;

public class BasicPartyContactPointDetails {
	 private PersonName personName;
	    private PostalAddress_ postalAddress;
	    public PersonName getPersonName() {
	        return personName;
	    }
	    public void setPersonName(PersonName personName) {
	        this.personName = personName;
	    }
	    public PostalAddress_ getPostalAddress() {
	        return postalAddress;
	    }
	    public void setPostalAddress(PostalAddress_ postalAddress) {
	        this.postalAddress = postalAddress;
	    }
		@Override
		public String toString() {
			return "BasicPartyContactPointDetails [personName=" + personName + ", postalAddress=" + postalAddress
					+ ", getPersonName()=" + getPersonName() + ", getPostalAddress()=" + getPostalAddress()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
}
