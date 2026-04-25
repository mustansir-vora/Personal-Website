package com.farmers.bean.BillPresentMents;

public class BillingSummary {
	 private AccountAgreementBillingSummaryView accountAgreementBillingSummaryView;
	    public AccountAgreementBillingSummaryView getAccountAgreementBillingSummaryView() {
	        return accountAgreementBillingSummaryView;
	    }
	    public void setAccountAgreementBillingSummaryView(AccountAgreementBillingSummaryView accountAgreementBillingSummaryView) {
	        this.accountAgreementBillingSummaryView = accountAgreementBillingSummaryView;
	    }
		@Override
		public String toString() {
			return "BillingSummary [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
					+ super.toString() + "]";
		}
	    
	    
}
