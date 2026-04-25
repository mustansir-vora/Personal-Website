package com.farmers.bean.FWSBillingLookup.PointBilling;

public class ReturnMessage {
	private String statusCode;
    private String statusDesc;
    private String errorCode;
    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public String getStatusDesc() {
        return statusDesc;
    }
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
	@Override
	public String toString() {
		return "ReturnMessage [statusCode=" + statusCode + ", statusDesc=" + statusDesc + ", errorCode=" + errorCode
				+ ", getStatusCode()=" + getStatusCode() + ", getStatusDesc()=" + getStatusDesc() + ", getErrorCode()="
				+ getErrorCode() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
