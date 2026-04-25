package com.farmers.bean.BillPresentMents;

public class BasicBillingAccountAgreementView {
		private String endDate;
		private MinimumPaymentDueAmount minimumPaymentDueAmount;
	    private OutStandingAmount outStandingAmount;
	    private String paymentDueDate;
	    private String blockPayment;
	    private String accountAgreementNumber;
	    public MinimumPaymentDueAmount getMinimumPaymentDueAmount() {
	        return minimumPaymentDueAmount;
	    }
	    public void setMinimumPaymentDueAmount(MinimumPaymentDueAmount minimumPaymentDueAmount) {
	        this.minimumPaymentDueAmount = minimumPaymentDueAmount;
	    }
	    public OutStandingAmount getOutStandingAmount() {
	        return outStandingAmount;
	    }
	    public void setOutStandingAmount(OutStandingAmount outStandingAmount) {
	        this.outStandingAmount = outStandingAmount;
	    }
		public String getPaymentDueDate() {
			return paymentDueDate;
		}
		public void setPaymentDueDate(String paymentDueDate) {
			this.paymentDueDate = paymentDueDate;
		}
		public String getBlockPayment() {
			return blockPayment;
		}
		public void setBlockPayment(String blockPayment) {
			this.blockPayment = blockPayment;
		}
		public String getAccountAgreementNumber() {
			return accountAgreementNumber;
		}
		public void setAccountAgreementNumber(String accountAgreementNumber) {
			this.accountAgreementNumber = accountAgreementNumber;
		}
		
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		@Override
		public String toString() {
			return "BasicBillingAccountAgreementView [endDate=" + endDate + ", minimumPaymentDueAmount="
					+ minimumPaymentDueAmount + ", outStandingAmount=" + outStandingAmount + ", paymentDueDate="
					+ paymentDueDate + ", blockPayment=" + blockPayment + ", accountAgreementNumber="
					+ accountAgreementNumber + "]";
		}
		
	    
}
