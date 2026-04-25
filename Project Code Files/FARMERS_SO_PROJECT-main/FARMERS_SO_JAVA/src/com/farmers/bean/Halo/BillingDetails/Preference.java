package com.farmers.bean.Halo.BillingDetails;

public class Preference {
	private String code;
    private String value;
    private String sourceSystem;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getSourceSystem() {
        return sourceSystem;
    }
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
	@Override
	public String toString() {
		return "Preference [code=" + code + ", value=" + value + ", sourceSystem=" + sourceSystem + ", getCode()="
				+ getCode() + ", getValue()=" + getValue() + ", getSourceSystem()=" + getSourceSystem()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
    
}
