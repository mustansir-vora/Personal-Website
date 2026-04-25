package com.farmers.bean.SHAuthForemost;

public class Driver {
	 private Name name;
	    private String driverLicense;
	    private String birthDate;
	    public Name getName() {
	        return name;
	    }
	    public void setName(Name name) {
	        this.name = name;
	    }
	    public String getDriverLicense() {
	        return driverLicense;
	    }
	    public void setDriverLicense(String driverLicense) {
	        this.driverLicense = driverLicense;
	    }
	    public String getBirthDate() {
	        return birthDate;
	    }
	    public void setBirthDate(String birthDate) {
	        this.birthDate = birthDate;
	    }
		@Override
		public String toString() {
			return "Driver [driverLicense=" + driverLicense + ", birthDate=" + birthDate + ", getDriverLicense()="
					+ getDriverLicense() + ", getBirthDate()=" + getBirthDate() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
}
