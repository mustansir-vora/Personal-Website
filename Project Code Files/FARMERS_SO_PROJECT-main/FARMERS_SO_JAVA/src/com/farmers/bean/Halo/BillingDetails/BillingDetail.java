package com.farmers.bean.Halo.BillingDetails;

public class BillingDetail
{
    private Policy policy;
    public Policy getPolicy() 
	{
        return policy;
    }
    public void setPolicy(Policy policy) 
	{
        this.policy = policy;
    }
	@Override
	public String toString() {
		return "BillingDetail [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
}
