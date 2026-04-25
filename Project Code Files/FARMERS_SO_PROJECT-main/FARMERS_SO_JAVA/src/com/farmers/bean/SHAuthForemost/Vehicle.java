package com.farmers.bean.SHAuthForemost;

public class Vehicle {
	 private String number;
	    private String vehicleYear;
	    private String vehicleMake;
	    private String vehicleModel;
	    private String vin;
	    private String riskCode;
	    private String lob;
	    private Address_ address;
	    public String getNumber() {
	        return number;
	    }
	    public void setNumber(String number) {
	        this.number = number;
	    }
	    public String getVehicleYear() {
	        return vehicleYear;
	    }
	    public void setVehicleYear(String vehicleYear) {
	        this.vehicleYear = vehicleYear;
	    }
	    public String getVehicleMake() {
	        return vehicleMake;
	    }
	    public void setVehicleMake(String vehicleMake) {
	        this.vehicleMake = vehicleMake;
	    }
	    public String getVehicleModel() {
	        return vehicleModel;
	    }
	    public void setVehicleModel(String vehicleModel) {
	        this.vehicleModel = vehicleModel;
	    }
	    public String getVin() {
	        return vin;
	    }
	    public void setVin(String vin) {
	        this.vin = vin;
	    }
	    public String getRiskCode() {
	        return riskCode;
	    }
	    public void setRiskCode(String riskCode) {
	        this.riskCode = riskCode;
	    }
	    public String getLob() {
	        return lob;
	    }
	    public void setLob(String lob) {
	        this.lob = lob;
	    }
	    public Address_ getAddress() {
	        return address;
	    }
	    public void setAddress(Address_ address) {
	        this.address = address;
	    }
		@Override
		public String toString() {
			return "Vehicle [number=" + number + ", vehicleYear=" + vehicleYear + ", vehicleMake=" + vehicleMake
					+ ", vehicleModel=" + vehicleModel + ", vin=" + vin + ", riskCode=" + riskCode + ", lob=" + lob
					+ ", address=" + address + ", getNumber()=" + getNumber() + ", getVehicleYear()=" + getVehicleYear()
					+ ", getVehicleMake()=" + getVehicleMake() + ", getVehicleModel()=" + getVehicleModel()
					+ ", getVin()=" + getVin() + ", getRiskCode()=" + getRiskCode() + ", getLob()=" + getLob()
					+ ", getAddress()=" + getAddress() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
	    
}
