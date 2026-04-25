package com.farmers.bean.BillPresentMents;

public class TransactionNotification {
	private String transactionStatus;
    private Remark remark;
    public String getTransactionStatus() {
        return transactionStatus;
    }
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
    public Remark getRemark() {
        return remark;
    }
    public void setRemark(Remark remark) {
        this.remark = remark;
    }
	@Override
	public String toString() {
		return "TransactionNotification [transactionStatus=" + transactionStatus + ", getTransactionStatus()="
				+ getTransactionStatus() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
    
    
}
