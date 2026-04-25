package com.farmers.bean.polinqrtriveInsurPoliByParty;

public class BirthName {
	   public String firstName;
	    public String lastName;
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
		@Override
		public String toString() {
			return "BirthName [firstName=" + firstName + ", lastName=" + lastName + ", getFirstName()=" + getFirstName()
					+ ", getLastName()=" + getLastName() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
		
}
