package com.farmers.bean.StateGroup;

public class State {
	 public String code;
	    public int tenantid;
	    public String name;
	    public String farmerstatecode;
	    public String areacode;

	    
	    
	    
		public int getTenantid() {
			return tenantid;
		}




		public void setTenantid(int tenantid) {
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
			return "State [tenantid=" + tenantid + ", name=" + name + ", code=" + code + ", areacode=" + areacode
					+ ", farmerstatecode=" + farmerstatecode + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	   
}
