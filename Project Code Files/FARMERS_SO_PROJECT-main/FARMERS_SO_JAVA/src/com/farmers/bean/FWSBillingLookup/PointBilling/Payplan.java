package com.farmers.bean.FWSBillingLookup.PointBilling;

public class Payplan {
	 private String status;
	    private String code;
	    private String outstandingInstallments;
	    private String description;
	    public String getStatus() {
	        return status;
	    }
	    public void setStatus(String status) {
	        this.status = status;
	    }
	    public String getCode() {
	        return code;
	    }
	    public void setCode(String code) {
	        this.code = code;
	    }
	    public String getOutstandingInstallments() {
	        return outstandingInstallments;
	    }
	    public void setOutstandingInstallments(String outstandingInstallments) {
	        this.outstandingInstallments = outstandingInstallments;
	    }
	    public String getDescription() {
	        return description;
	    }
	    public void setDescription(String description) {
	        this.description = description;
	    }
		@Override
		public String toString() {
			return "Payplan [status=" + status + ", code=" + code + ", outstandingInstallments="
					+ outstandingInstallments + ", description=" + description + ", getStatus()=" + getStatus()
					+ ", getCode()=" + getCode() + ", getOutstandingInstallments()=" + getOutstandingInstallments()
					+ ", getDescription()=" + getDescription() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
}
