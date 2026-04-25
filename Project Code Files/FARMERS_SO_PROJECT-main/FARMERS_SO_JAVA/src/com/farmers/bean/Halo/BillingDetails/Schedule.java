package com.farmers.bean.Halo.BillingDetails;

public class Schedule {
	private String billNumber;
    private String billIssueIndicator;
    private String billIssueDate;
    private String billDueDate;
    private Double billAmount;
    private Double billSurtax;
    private Double billServiceCharge;
    private String billCancelNoticeIssueDate;
    private String billCancelRequestDate;
    private String whoPaidCode;
    private String whoPaidDesc;
    public String getBillNumber() {
        return billNumber;
    }
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    public String getBillIssueIndicator() {
        return billIssueIndicator;
    }
    public void setBillIssueIndicator(String billIssueIndicator) {
        this.billIssueIndicator = billIssueIndicator;
    }
    public String getBillIssueDate() {
        return billIssueDate;
    }
    public void setBillIssueDate(String billIssueDate) {
        this.billIssueDate = billIssueDate;
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
    public String getBillCancelNoticeIssueDate() {
        return billCancelNoticeIssueDate;
    }
    public void setBillCancelNoticeIssueDate(String billCancelNoticeIssueDate) {
        this.billCancelNoticeIssueDate = billCancelNoticeIssueDate;
    }
    public String getBillCancelRequestDate() {
        return billCancelRequestDate;
    }
    public void setBillCancelRequestDate(String billCancelRequestDate) {
        this.billCancelRequestDate = billCancelRequestDate;
    }
    public String getWhoPaidCode() {
        return whoPaidCode;
    }
    public void setWhoPaidCode(String whoPaidCode) {
        this.whoPaidCode = whoPaidCode;
    }
    public String getWhoPaidDesc() {
        return whoPaidDesc;
    }
    public void setWhoPaidDesc(String whoPaidDesc) {
        this.whoPaidDesc = whoPaidDesc;
    }
	@Override
	public String toString() {
		return "Schedule [billNumber=" + billNumber + ", billIssueIndicator=" + billIssueIndicator + ", billIssueDate="
				+ billIssueDate + ", billDueDate=" + billDueDate + ", billAmount=" + billAmount + ", billSurtax="
				+ billSurtax + ", billServiceCharge=" + billServiceCharge + ", billCancelNoticeIssueDate="
				+ billCancelNoticeIssueDate + ", billCancelRequestDate=" + billCancelRequestDate + ", whoPaidCode="
				+ whoPaidCode + ", whoPaidDesc=" + whoPaidDesc + ", getBillNumber()=" + getBillNumber()
				+ ", getBillIssueIndicator()=" + getBillIssueIndicator() + ", getBillIssueDate()=" + getBillIssueDate()
				+ ", getBillDueDate()=" + getBillDueDate() + ", getBillAmount()=" + getBillAmount()
				+ ", getBillSurtax()=" + getBillSurtax() + ", getBillServiceCharge()=" + getBillServiceCharge()
				+ ", getBillCancelNoticeIssueDate()=" + getBillCancelNoticeIssueDate() + ", getBillCancelRequestDate()="
				+ getBillCancelRequestDate() + ", getWhoPaidCode()=" + getWhoPaidCode() + ", getWhoPaidDesc()="
				+ getWhoPaidDesc() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
