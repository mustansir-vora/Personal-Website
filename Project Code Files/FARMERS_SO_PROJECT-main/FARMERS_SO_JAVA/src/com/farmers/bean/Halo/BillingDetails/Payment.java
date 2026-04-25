package com.farmers.bean.Halo.BillingDetails;

public class Payment {
	 private String paymentRecordType;
	    private String paymentStatusCode;
	    private String paymentReferenceNumber;
	    private String parentPaymentReferenceNumber;
	    private String paymentDate;
	    private Double paymentAmount;
	    private String paymentCreationDate;
	    private PaymentMethod paymentMethod;
	    private String paymentStatus;
	    public String getPaymentRecordType() {
	        return paymentRecordType;
	    }
	    public void setPaymentRecordType(String paymentRecordType) {
	        this.paymentRecordType = paymentRecordType;
	    }
	    public String getPaymentStatusCode() {
	        return paymentStatusCode;
	    }
	    public void setPaymentStatusCode(String paymentStatusCode) {
	        this.paymentStatusCode = paymentStatusCode;
	    }
	    public String getPaymentReferenceNumber() {
	        return paymentReferenceNumber;
	    }
	    public void setPaymentReferenceNumber(String paymentReferenceNumber) {
	        this.paymentReferenceNumber = paymentReferenceNumber;
	    }
	    public String getParentPaymentReferenceNumber() {
	        return parentPaymentReferenceNumber;
	    }
	    public void setParentPaymentReferenceNumber(String parentPaymentReferenceNumber) {
	        this.parentPaymentReferenceNumber = parentPaymentReferenceNumber;
	    }
	    public String getPaymentDate() {
	        return paymentDate;
	    }
	    public void setPaymentDate(String paymentDate) {
	        this.paymentDate = paymentDate;
	    }
	    public Double getPaymentAmount() {
	        return paymentAmount;
	    }
	    public void setPaymentAmount(Double paymentAmount) {
	        this.paymentAmount = paymentAmount;
	    }
	    public String getPaymentCreationDate() {
	        return paymentCreationDate;
	    }
	    public void setPaymentCreationDate(String paymentCreationDate) {
	        this.paymentCreationDate = paymentCreationDate;
	    }
	    public PaymentMethod getPaymentMethod() {
	        return paymentMethod;
	    }
	    public void setPaymentMethod(PaymentMethod paymentMethod) {
	        this.paymentMethod = paymentMethod;
	    }
	    public String getPaymentStatus() {
	        return paymentStatus;
	    }
	    public void setPaymentStatus(String paymentStatus) {
	        this.paymentStatus = paymentStatus;
	    }
		@Override
		public String toString() {
			return "Payment [paymentRecordType=" + paymentRecordType + ", paymentStatusCode=" + paymentStatusCode
					+ ", paymentReferenceNumber=" + paymentReferenceNumber + ", parentPaymentReferenceNumber="
					+ parentPaymentReferenceNumber + ", paymentDate=" + paymentDate + ", paymentAmount=" + paymentAmount
					+ ", paymentCreationDate=" + paymentCreationDate + ", paymentStatus=" + paymentStatus
					+ ", getPaymentRecordType()=" + getPaymentRecordType() + ", getPaymentStatusCode()="
					+ getPaymentStatusCode() + ", getPaymentReferenceNumber()=" + getPaymentReferenceNumber()
					+ ", getParentPaymentReferenceNumber()=" + getParentPaymentReferenceNumber() + ", getPaymentDate()="
					+ getPaymentDate() + ", getPaymentAmount()=" + getPaymentAmount() + ", getPaymentCreationDate()="
					+ getPaymentCreationDate() + ", getPaymentStatus()=" + getPaymentStatus() + ", getClass()="
					+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
}
