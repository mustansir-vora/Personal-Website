package com.farmers.bean.AdminStateAreaCode;

public class RequestBody {
	 private Integer tenantid;
	    private String callerid;
	    private String areacode;
	    public Integer getTenantid() {
	        return tenantid;
	    }
	    public void setTenantid(Integer tenantid) {
	        this.tenantid = tenantid;
	    }
	    public String getCallerid() {
	        return callerid;
	    }
	    public void setCallerid(String callerid) {
	        this.callerid = callerid;
	    }
	    public String getAreacode() {
	        return areacode;
	    }
	    public void setAreacode(String areacode) {
	        this.areacode = areacode;
	    }
		@Override
		public String toString() {
			return "RequestBody [tenantid=" + tenantid + ", callerid=" + callerid + ", areacode=" + areacode
					+ ", getTenantid()=" + getTenantid() + ", getCallerid()=" + getCallerid() + ", getAreacode()="
					+ getAreacode() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	    
	    
	    
	    
}
