package com.farmers.bean.BillPresentMents;

public class PaymentDue_ {
	
	private Amount__ amount;
    private String dueDate;
    public Amount__ getAmount() {
        return amount;
    }
    public void setAmount(Amount__ amount) {
        this.amount = amount;
    }
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
	@Override
	public String toString() {
		return "PaymentDue_ [dueDate=" + dueDate + ", getDueDate()=" + getDueDate() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
	

}
