package com.farmers.bean.Halo.BillingDetails;

import java.util.ArrayList;
import java.util.List;

public class Term {
	  private String id;
	    private String termSequence;
	    private String status;
	    private String effectiveDate;
	    private String expirationDate;
	    private String escrowBillIndicator;
	    private String maximumPaymentDate;
	    private String callCSRFlag;
	    private PayPlan payPlan;
	    private String billingStatusCode;
	    private String nextActivityCode;
	    private String nextActivityDate;
	    private String cancelNoticeDate;
	    private String billDueDate;
	    private Double billAmount;
	    private Double billSurtax;
	    private Double billServiceCharge;
	    private String billStopIndicator;
	    private String billStopDate;
	    private String billProductDesc;
	    private Double minimumAmount;
	    private Double outstandingBalance;
	    private String paymentCreditCardEligInd;
	    private String paymentDebitCardEligInd;
	    private String paymentAchEligInd;
	    private String billingStatusDesc;
	    private String billingStatusAlternateDesc;
	    private String nextActivityDesc;
	    private String billingProductDesc;
	    private String lastUpdatedTimeStamp;
	    private Payment payment;
	    private List<Schedule> schedules = new ArrayList<Schedule>();
	    private List<TransactionsHistory> transactionsHistory = new ArrayList<TransactionsHistory>();
	    public String getId() {
	        return id;
	    }
	    public void setId(String id) {
	        this.id = id;
	    }
	    public String getTermSequence() {
	        return termSequence;
	    }
	    public void setTermSequence(String termSequence) {
	        this.termSequence = termSequence;
	    }
	    public String getStatus() {
	        return status;
	    }
	    public void setStatus(String status) {
	        this.status = status;
	    }
	    public String getEffectiveDate() {
	        return effectiveDate;
	    }
	    public void setEffectiveDate(String effectiveDate) {
	        this.effectiveDate = effectiveDate;
	    }
	    public String getExpirationDate() {
	        return expirationDate;
	    }
	    public void setExpirationDate(String expirationDate) {
	        this.expirationDate = expirationDate;
	    }
	    public String getEscrowBillIndicator() {
	        return escrowBillIndicator;
	    }
	    public void setEscrowBillIndicator(String escrowBillIndicator) {
	        this.escrowBillIndicator = escrowBillIndicator;
	    }
	    public String getMaximumPaymentDate() {
	        return maximumPaymentDate;
	    }
	    public void setMaximumPaymentDate(String maximumPaymentDate) {
	        this.maximumPaymentDate = maximumPaymentDate;
	    }
	    public String getCallCSRFlag() {
	        return callCSRFlag;
	    }
	    public void setCallCSRFlag(String callCSRFlag) {
	        this.callCSRFlag = callCSRFlag;
	    }
	    public PayPlan getPayPlan() {
	        return payPlan;
	    }
	    public void setPayPlan(PayPlan payPlan) {
	        this.payPlan = payPlan;
	    }
	    public String getBillingStatusCode() {
	        return billingStatusCode;
	    }
	    public void setBillingStatusCode(String billingStatusCode) {
	        this.billingStatusCode = billingStatusCode;
	    }
	    public String getNextActivityCode() {
	        return nextActivityCode;
	    }
	    public void setNextActivityCode(String nextActivityCode) {
	        this.nextActivityCode = nextActivityCode;
	    }
	    public String getNextActivityDate() {
	        return nextActivityDate;
	    }
	    public void setNextActivityDate(String nextActivityDate) {
	        this.nextActivityDate = nextActivityDate;
	    }
	    public String getCancelNoticeDate() {
	        return cancelNoticeDate;
	    }
	    public void setCancelNoticeDate(String cancelNoticeDate) {
	        this.cancelNoticeDate = cancelNoticeDate;
	    }
	    public String getBillDueDate() {
	        return billDueDate;
	    }
	    public void setBillDueDate(String billDueDate) {
	        this.billDueDate = billDueDate;
	    }
	    public Double getBillAmount() {
	        return billAmount;
	    }
	    public void setBillAmount(Double billAmount) {
	        this.billAmount = billAmount;
	    }
	    public Double getBillSurtax() {
	        return billSurtax;
	    }
	    public void setBillSurtax(Double billSurtax) {
	        this.billSurtax = billSurtax;
	    }
	    public Double getBillServiceCharge() {
	        return billServiceCharge;
	    }
	    public void setBillServiceCharge(Double billServiceCharge) {
	        this.billServiceCharge = billServiceCharge;
	    }
	    public String getBillStopIndicator() {
	        return billStopIndicator;
	    }
	    public void setBillStopIndicator(String billStopIndicator) {
	        this.billStopIndicator = billStopIndicator;
	    }
	    public String getBillStopDate() {
	        return billStopDate;
	    }
	    public void setBillStopDate(String billStopDate) {
	        this.billStopDate = billStopDate;
	    }
	    public String getBillProductDesc() {
	        return billProductDesc;
	    }
	    public void setBillProductDesc(String billProductDesc) {
	        this.billProductDesc = billProductDesc;
	    }
	    public Double getMinimumAmount() {
	        return minimumAmount;
	    }
	    public void setMinimumAmount(Double minimumAmount) {
	        this.minimumAmount = minimumAmount;
	    }
	    public Double getOutstandingBalance() {
	        return outstandingBalance;
	    }
	    public void setOutstandingBalance(Double outstandingBalance) {
	        this.outstandingBalance = outstandingBalance;
	    }
	    public String getPaymentCreditCardEligInd() {
	        return paymentCreditCardEligInd;
	    }
	    public void setPaymentCreditCardEligInd(String paymentCreditCardEligInd) {
	        this.paymentCreditCardEligInd = paymentCreditCardEligInd;
	    }
	    public String getPaymentDebitCardEligInd() {
	        return paymentDebitCardEligInd;
	    }
	    public void setPaymentDebitCardEligInd(String paymentDebitCardEligInd) {
	        this.paymentDebitCardEligInd = paymentDebitCardEligInd;
	    }
	    public String getPaymentAchEligInd() {
	        return paymentAchEligInd;
	    }
	    public void setPaymentAchEligInd(String paymentAchEligInd) {
	        this.paymentAchEligInd = paymentAchEligInd;
	    }
	    public String getBillingStatusDesc() {
	        return billingStatusDesc;
	    }
	    public void setBillingStatusDesc(String billingStatusDesc) {
	        this.billingStatusDesc = billingStatusDesc;
	    }
	    public String getBillingStatusAlternateDesc() {
	        return billingStatusAlternateDesc;
	    }
	    public void setBillingStatusAlternateDesc(String billingStatusAlternateDesc) {
	        this.billingStatusAlternateDesc = billingStatusAlternateDesc;
	    }
	    public String getNextActivityDesc() {
	        return nextActivityDesc;
	    }
	    public void setNextActivityDesc(String nextActivityDesc) {
	        this.nextActivityDesc = nextActivityDesc;
	    }
	    public String getBillingProductDesc() {
	        return billingProductDesc;
	    }
	    public void setBillingProductDesc(String billingProductDesc) {
	        this.billingProductDesc = billingProductDesc;
	    }
	    public String getLastUpdatedTimeStamp() {
	        return lastUpdatedTimeStamp;
	    }
	    public void setLastUpdatedTimeStamp(String lastUpdatedTimeStamp) {
	        this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
	    }
	    public Payment getPayment() {
	        return payment;
	    }
	    public void setPayment(Payment payment) {
	        this.payment = payment;
	    }
	    public List<Schedule> getSchedules() {
	        return schedules;
	    }
	    public void setSchedules(List<Schedule> schedules) {
	        this.schedules = schedules;
	    }
	    public List<TransactionsHistory> getTransactionsHistory() {
	        return transactionsHistory;
	    }
	    public void setTransactionsHistory(List<TransactionsHistory> transactionsHistory) {
	        this.transactionsHistory = transactionsHistory;
	    }
		@Override
		public String toString() {
			return "Term [id=" + id + ", termSequence=" + termSequence + ", status=" + status + ", effectiveDate="
					+ effectiveDate + ", expirationDate=" + expirationDate + ", escrowBillIndicator="
					+ escrowBillIndicator + ", maximumPaymentDate=" + maximumPaymentDate + ", callCSRFlag="
					+ callCSRFlag + ", payPlan=" + payPlan + ", billingStatusCode=" + billingStatusCode
					+ ", nextActivityCode=" + nextActivityCode + ", nextActivityDate=" + nextActivityDate
					+ ", cancelNoticeDate=" + cancelNoticeDate + ", billDueDate=" + billDueDate + ", billAmount="
					+ billAmount + ", billSurtax=" + billSurtax + ", billServiceCharge=" + billServiceCharge
					+ ", billStopIndicator=" + billStopIndicator + ", billStopDate=" + billStopDate
					+ ", billProductDesc=" + billProductDesc + ", minimumAmount=" + minimumAmount
					+ ", outstandingBalance=" + outstandingBalance + ", paymentCreditCardEligInd="
					+ paymentCreditCardEligInd + ", paymentDebitCardEligInd=" + paymentDebitCardEligInd
					+ ", paymentAchEligInd=" + paymentAchEligInd + ", billingStatusDesc=" + billingStatusDesc
					+ ", billingStatusAlternateDesc=" + billingStatusAlternateDesc + ", nextActivityDesc="
					+ nextActivityDesc + ", billingProductDesc=" + billingProductDesc + ", lastUpdatedTimeStamp="
					+ lastUpdatedTimeStamp + ", payment=" + payment + ", getId()=" + getId() + ", getTermSequence()="
					+ getTermSequence() + ", getStatus()=" + getStatus() + ", getEffectiveDate()=" + getEffectiveDate()
					+ ", getExpirationDate()=" + getExpirationDate() + ", getEscrowBillIndicator()="
					+ getEscrowBillIndicator() + ", getMaximumPaymentDate()=" + getMaximumPaymentDate()
					+ ", getCallCSRFlag()=" + getCallCSRFlag() + ", getPayPlan()=" + getPayPlan()
					+ ", getBillingStatusCode()=" + getBillingStatusCode() + ", getNextActivityCode()="
					+ getNextActivityCode() + ", getNextActivityDate()=" + getNextActivityDate()
					+ ", getCancelNoticeDate()=" + getCancelNoticeDate() + ", getBillDueDate()=" + getBillDueDate()
					+ ", getBillAmount()=" + getBillAmount() + ", getBillSurtax()=" + getBillSurtax()
					+ ", getBillServiceCharge()=" + getBillServiceCharge() + ", getBillStopIndicator()="
					+ getBillStopIndicator() + ", getBillStopDate()=" + getBillStopDate() + ", getBillProductDesc()="
					+ getBillProductDesc() + ", getMinimumAmount()=" + getMinimumAmount() + ", getOutstandingBalance()="
					+ getOutstandingBalance() + ", getPaymentCreditCardEligInd()=" + getPaymentCreditCardEligInd()
					+ ", getPaymentDebitCardEligInd()=" + getPaymentDebitCardEligInd() + ", getPaymentAchEligInd()="
					+ getPaymentAchEligInd() + ", getBillingStatusDesc()=" + getBillingStatusDesc()
					+ ", getBillingStatusAlternateDesc()=" + getBillingStatusAlternateDesc()
					+ ", getNextActivityDesc()=" + getNextActivityDesc() + ", getBillingProductDesc()="
					+ getBillingProductDesc() + ", getLastUpdatedTimeStamp()=" + getLastUpdatedTimeStamp()
					+ ", getPayment()=" + getPayment() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
					+ ", toString()=" + super.toString() + "]";
		}
}
