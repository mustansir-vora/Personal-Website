package com.farmers.bean.AdminStateAreaCode;

public class Re {
	 private Integer tenantid;
	    private String name;
	    private String code;
	    private String areacode;
	    private String farmerstatecode;
	    public Integer getTenantid() {
	        return tenantid;
	    }
	    public void setTenantid(Integer tenantid) {
	        this.tenantid = tenantid;
	    }
	    public String getName() {
	        return name;
	    }
	    public void setName(String name) {
	        this.name = name;
	    }
	    public String getCode() {
	        return code;
	    }
	    public void setCode(String code) {
	        this.code = code;
	    }
	    public String getAreacode() {
	        return areacode;
	    }
	    public void setAreacode(String areacode) {
	        this.areacode = areacode;
	    }
	    public String getFarmerstatecode() {
	        return farmerstatecode;
	    }
	    public void setFarmerstatecode(String farmerstatecode) {
	        this.farmerstatecode = farmerstatecode;
	    }
		@Override
		public String toString() {
			return "Re [tenantid=" + tenantid + ", name=" + name + ", code=" + code + ", areacode=" + areacode
					+ ", farmerstatecode=" + farmerstatecode + ", getTenantid()=" + getTenantid() + ", getName()="
					+ getName() + ", getCode()=" + getCode() + ", getAreacode()=" + getAreacode()
					+ ", getFarmerstatecode()=" + getFarmerstatecode() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
}
