package com.farmers.bean.Halo.BillingDetails;

public class TransactionsHistory {
	 private String billingTransactionUid;
	    private String billDocumentUid;
	    private String flexActivityDesc;
	    private String activityDescCode;
	    private String activityDescVariable1;
	    private String activityDescVariable2;
	    private String processDate;
	    private Double amount;
	    private Double surtax;
	    private Double serviceCharge;
	    private String whoPaidCode;
	    private String notes;
	    private String cashBatchCode;
	    private String cashStampNumber;
	    private String whoPaidDesc;
	    private String cashBatchActorDesc;
	    private String cashBatchActorDescTranslated;
	    private String activityDescTranslated;
	    private String activityActorDesc;
	    public String getBillingTransactionUid() {
	        return billingTransactionUid;
	    }
	    public void setBillingTransactionUid(String billingTransactionUid) {
	        this.billingTransactionUid = billingTransactionUid;
	    }
	    public String getBillDocumentUid() {
	        return billDocumentUid;
	    }
	    public void setBillDocumentUid(String billDocumentUid) {
	        this.billDocumentUid = billDocumentUid;
	    }
	    public String getFlexActivityDesc() {
	        return flexActivityDesc;
	    }
	    public void setFlexActivityDesc(String flexActivityDesc) {
	        this.flexActivityDesc = flexActivityDesc;
	    }
	    public String getActivityDescCode() {
	        return activityDescCode;
	    }
	    public void setActivityDescCode(String activityDescCode) {
	        this.activityDescCode = activityDescCode;
	    }
	    public String getActivityDescVariable1() {
	        return activityDescVariable1;
	    }
	    public void setActivityDescVariable1(String activityDescVariable1) {
	        this.activityDescVariable1 = activityDescVariable1;
	    }
	    public String getActivityDescVariable2() {
	        return activityDescVariable2;
	    }
	    public void setActivityDescVariable2(String activityDescVariable2) {
	        this.activityDescVariable2 = activityDescVariable2;
	    }
	    public String getProcessDate() {
	        return processDate;
	    }
	    public void setProcessDate(String processDate) {
	        this.processDate = processDate;
	    }
	    public Double getAmount() {
	        return amount;
	    }
	    public void setAmount(Double amount) {
	        this.amount = amount;
	    }
	    public Double getSurtax() {
	        return surtax;
	    }
	    public void setSurtax(Double surtax) {
	        this.surtax = surtax;
	    }
	    public Double getServiceCharge() {
	        return serviceCharge;
	    }
	    public void setServiceCharge(Double serviceCharge) {
	        this.serviceCharge = serviceCharge;
	    }
	    public String getWhoPaidCode() {
	        return whoPaidCode;
	    }
	    public void setWhoPaidCode(String whoPaidCode) {
	        this.whoPaidCode = whoPaidCode;
	    }
	    public String getNotes() {
	        return notes;
	    }
	    public void setNotes(String notes) {
	        this.notes = notes;
	    }
	    public String getCashBatchCode() {
	        return cashBatchCode;
	    }
	    public void setCashBatchCode(String cashBatchCode) {
	        this.cashBatchCode = cashBatchCode;
	    }
	    public String getCashStampNumber() {
	        return cashStampNumber;
	    }
	    public void setCashStampNumber(String cashStampNumber) {
	        this.cashStampNumber = cashStampNumber;
	    }
	    public String getWhoPaidDesc() {
	        return whoPaidDesc;
	    }
	    public void setWhoPaidDesc(String whoPaidDesc) {
	        this.whoPaidDesc = whoPaidDesc;
	    }
	    public String getCashBatchActorDesc() {
	        return cashBatchActorDesc;
	    }
	    public void setCashBatchActorDesc(String cashBatchActorDesc) {
	        this.cashBatchActorDesc = cashBatchActorDesc;
	    }
	    public String getCashBatchActorDescTranslated() {
	        return cashBatchActorDescTranslated;
	    }
	    public void setCashBatchActorDescTranslated(String cashBatchActorDescTranslated) {
	        this.cashBatchActorDescTranslated = cashBatchActorDescTranslated;
	    }
	    public String getActivityDescTranslated() {
	        return activityDescTranslated;
	    }
	    public void setActivityDescTranslated(String activityDescTranslated) {
	        this.activityDescTranslated = activityDescTranslated;
	    }
	    public String getActivityActorDesc() {
	        return activityActorDesc;
	    }
	    public void setActivityActorDesc(String activityActorDesc) {
	        this.activityActorDesc = activityActorDesc;
	    }
		@Override
		public String toString() {
			return "TransactionsHistory [billingTransactionUid=" + billingTransactionUid + ", billDocumentUid="
					+ billDocumentUid + ", flexActivityDesc=" + flexActivityDesc + ", activityDescCode="
					+ activityDescCode + ", activityDescVariable1=" + activityDescVariable1 + ", activityDescVariable2="
					+ activityDescVariable2 + ", processDate=" + processDate + ", amount=" + amount + ", surtax="
					+ surtax + ", serviceCharge=" + serviceCharge + ", whoPaidCode=" + whoPaidCode + ", notes=" + notes
					+ ", cashBatchCode=" + cashBatchCode + ", cashStampNumber=" + cashStampNumber + ", whoPaidDesc="
					+ whoPaidDesc + ", cashBatchActorDesc=" + cashBatchActorDesc + ", cashBatchActorDescTranslated="
					+ cashBatchActorDescTranslated + ", activityDescTranslated=" + activityDescTranslated
					+ ", activityActorDesc=" + activityActorDesc + ", getBillingTransactionUid()="
					+ getBillingTransactionUid() + ", getBillDocumentUid()=" + getBillDocumentUid()
					+ ", getFlexActivityDesc()=" + getFlexActivityDesc() + ", getActivityDescCode()="
					+ getActivityDescCode() + ", getActivityDescVariable1()=" + getActivityDescVariable1()
					+ ", getActivityDescVariable2()=" + getActivityDescVariable2() + ", getProcessDate()="
					+ getProcessDate() + ", getAmount()=" + getAmount() + ", getSurtax()=" + getSurtax()
					+ ", getServiceCharge()=" + getServiceCharge() + ", getWhoPaidCode()=" + getWhoPaidCode()
					+ ", getNotes()=" + getNotes() + ", getCashBatchCode()=" + getCashBatchCode()
					+ ", getCashStampNumber()=" + getCashStampNumber() + ", getWhoPaidDesc()=" + getWhoPaidDesc()
					+ ", getCashBatchActorDesc()=" + getCashBatchActorDesc() + ", getCashBatchActorDescTranslated()="
					+ getCashBatchActorDescTranslated() + ", getActivityDescTranslated()=" + getActivityDescTranslated()
					+ ", getActivityActorDesc()=" + getActivityActorDesc() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
}
