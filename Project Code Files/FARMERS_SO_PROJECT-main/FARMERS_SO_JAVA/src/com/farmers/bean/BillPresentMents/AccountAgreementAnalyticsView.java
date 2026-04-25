package com.farmers.bean.BillPresentMents;

public class AccountAgreementAnalyticsView {
	  private String payerRoleType;
	    private PostalAddress postalAddress;
	    public String getPayerRoleType() {
	        return payerRoleType;
	    }
	    public void setayerRoleType(String payerRoleType) {
	        this.payerRoleType = payerRoleType;
	    }
	    public PostalAddress getpostalAddress() {
	        return postalAddress;
	    }
	    public void setpostalAddress(PostalAddress postalAddress) {
	        this.postalAddress = postalAddress;
	    }
		@Override
		public String toString() {
			return "AccountAgreementAnalyticsView [payerRoleType=" + payerRoleType + ", postalAddress=" + postalAddress
					+ ", getPayerRoleType()=" + getPayerRoleType() + ", getPostalAddress()=" + getpostalAddress()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
}
