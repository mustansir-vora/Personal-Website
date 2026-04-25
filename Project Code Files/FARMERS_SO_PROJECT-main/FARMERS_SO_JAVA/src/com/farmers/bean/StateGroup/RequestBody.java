package com.farmers.bean.StateGroup;

public class RequestBody {
	 public int tenantid;
	    public String callerid;
	    public String key;
		public int getTenantid() {
			return tenantid;
		}
		public void setTenantid(int tenantid) {
			this.tenantid = tenantid;
		}
		public String getCallerid() {
			return callerid;
		}
		public void setCallerid(String callerid) {
			this.callerid = callerid;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		@Override
		public String toString() {
			return "RequestBody [tenantid=" + tenantid + ", callerid=" + callerid + ", key=" + key + ", getTenantid()="
					+ getTenantid() + ", getCallerid()=" + getCallerid() + ", getKey()=" + getKey() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
}
