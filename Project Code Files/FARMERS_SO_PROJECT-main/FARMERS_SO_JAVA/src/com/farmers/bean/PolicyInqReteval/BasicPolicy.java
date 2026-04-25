package com.farmers.bean.PolicyInqReteval;

public class BasicPolicy {
	private String ineOfBusiness;
    private String policyModNumber;
    private String policyNumber;
    public String getIneOfBusiness() {
        return ineOfBusiness;
    }
    public void setIneOfBusiness(String ineOfBusiness) {
        this.ineOfBusiness = ineOfBusiness;
    }
    public String getPolicyModNumber() {
        return policyModNumber;
    }
    public void setPolicyModNumber(String policyModNumber) {
        this.policyModNumber = policyModNumber;
    }
    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
	@Override
	public String toString() {
		return "BasicPolicy [ineOfBusiness=" + ineOfBusiness + ", policyModNumber=" + policyModNumber
				+ ", policyNumber=" + policyNumber + ", getIneOfBusiness()=" + getIneOfBusiness()
				+ ", getPolicyModNumber()=" + getPolicyModNumber() + ", getPolicyNumber()=" + getPolicyNumber()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
}
