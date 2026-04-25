package com.farmers.bean.BillPresentMents;

public class InsurancePolicyBillingSummaryView 
{
	
		private String productGroupName;
	    private InsurancePolicy insurancePolicy;
	    public String getProductGroupName() {
	        return productGroupName;
	    }
	    public void setProductGroupName(String productGroupName) {
	        this.productGroupName = productGroupName;
	    }
	    public InsurancePolicy getInsurancePolicy() {
	        return insurancePolicy;
	    }
	    public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
	        this.insurancePolicy = insurancePolicy;
	    }
		@Override
		public String toString() {
			return "InsurancePolicyBillingSummaryView [productGroupName=" + productGroupName
					+ ", getProductGroupName()=" + getProductGroupName() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    

}
