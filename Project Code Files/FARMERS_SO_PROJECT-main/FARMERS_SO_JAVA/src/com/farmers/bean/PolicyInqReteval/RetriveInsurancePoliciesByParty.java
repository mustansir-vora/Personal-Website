package com.farmers.bean.PolicyInqReteval;

public class RetriveInsurancePoliciesByParty {
	private Household household;
    public Household getHousehold() {
        return household;
    }
    public void setHousehold(Household household) {
        this.household = household;
    }
	@Override
	public String toString() {
		return "Root [household=" + household + ", getHousehold()=" + getHousehold() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
