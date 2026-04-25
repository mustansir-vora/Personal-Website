package com.farmers.bean.FWSBillingLookup.PointBilling;

public class Producer {
	  private String roleCd;
	    private String subCd;
	    public String getRoleCd() {
	        return roleCd;
	    }
	    public void setRoleCd(String roleCd) {
	        this.roleCd = roleCd;
	    }
	    public String getSubCd() {
	        return subCd;
	    }
	    public void setSubCd(String subCd) {
	        this.subCd = subCd;
	    }
		@Override
		public String toString() {
			return "Producer [roleCd=" + roleCd + ", subCd=" + subCd + ", getRoleCd()=" + getRoleCd() + ", getSubCd()="
					+ getSubCd() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	    
}
