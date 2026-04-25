package com.farmers.bean.BillPresentMents;

public class PaymentDue {
	private Amount_ amount;
    private String dueDate;
    public Amount_ getAmount() {
        return amount;
    }
    public void setAmount(Amount_ amount) {
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
		return "PaymentDue [dueDate=" + dueDate + ", getDueDate()=" + getDueDate() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    

}
