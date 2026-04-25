package com.farmers.bean.BillPresentMents;

public class InsurancePolicy {
	 private String externalReference;
	    private String duration;
	    private String endDate;
	    private String startDate;
	    private String typeName;
	    private String description;
	    private CumulativePremiumAmount cumulativePremiumAmount;
	    private Status status;
	    public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getExternalReference() {
	        return externalReference;
	    }
	    public void setExternalReference(String externalReference) {
	        this.externalReference = externalReference;
	    }
	    public String getDuration() {
	        return duration;
	    }
	    public void setDuration(String duration) {
	        this.duration = duration;
	    }
	    public String getEndDate() {
	        return endDate;
	    }
	    public void setEndDate(String endDate) {
	        this.endDate = endDate;
	    }
	    public String getStartDate() {
	        return startDate;
	    }
	    public void setStartDate(String startDate) {
	        this.startDate = startDate;
	    }
	    public String getTypeName() {
	        return typeName;
	    }
	    public void setTypeName(String typeName) {
	        this.typeName = typeName;
	    }
	    public CumulativePremiumAmount getCumulativePremiumAmount() {
	        return cumulativePremiumAmount;
	    }
	    public void setCumulativePremiumAmount(CumulativePremiumAmount cumulativePremiumAmount) {
	        this.cumulativePremiumAmount = cumulativePremiumAmount;
	    }
	    public Status getStatus() {
	        return status;
	    }
	    public void setStatus(Status status) {
	        this.status = status;
	    }
		@Override
		public String toString() {
			return "InsurancePolicy [externalReference=" + externalReference + ", duration=" + duration + ", endDate="
					+ endDate + ", startDate=" + startDate + ", typeName=" + typeName + ", description=" + description
					+ ", cumulativePremiumAmount=" + cumulativePremiumAmount + ", status=" + status
					+ ", getExternalReference()=" + getExternalReference() + ", getDuration()=" + getDuration()
					+ ", getEndDate()=" + getEndDate() + ", getStartDate()=" + getStartDate() + ", getTypeName()="
					+ getTypeName() + ", getCumulativePremiumAmount()=" + getCumulativePremiumAmount()
					+ ", getStatus()=" + getStatus() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
	    
	

}
